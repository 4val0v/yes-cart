/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.shoppingcart.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * User: denispavlov
 * Date: 13-10-20
 * Time: 6:07 PM
 */
public class WeightBasedPriceListDeliveryCostCalculationStrategy implements DeliveryCostCalculationStrategy {

    private static final BigDecimal SINGLE = new BigDecimal(1).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal MULTI = new BigDecimal(2).setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
    private static final BigDecimal MAX = new BigDecimal(Integer.MAX_VALUE);

    private final CarrierSlaService carrierSlaService;
    private final ProductService productService;
    private final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver;
    private final PricingPolicyProvider pricingPolicyProvider;

    public WeightBasedPriceListDeliveryCostCalculationStrategy(final CarrierSlaService carrierSlaService,
                                                               final ProductService productService,
                                                               final PricingPolicyProvider pricingPolicyProvider,
                                                               final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver) {
        this.carrierSlaService = carrierSlaService;
        this.productService = productService;
        this.deliveryCostRegionalPriceResolver = deliveryCostRegionalPriceResolver;
        this.pricingPolicyProvider = pricingPolicyProvider;
    }

    /** {@inheritDoc} */
    public Total calculate(final MutableShoppingCart cart) {

        cart.removeShipping();

        if (cart.getCarrierSlaId() != null) {
            final CarrierSla carrierSla = carrierSlaService.getById(cart.getCarrierSlaId());
            if (carrierSla != null && CarrierSla.WEIGHT_VOLUME.equals(carrierSla.getSlaType())) {

                final String carrierSlaId = carrierSla.getGuid();

                final PricingPolicyProvider.PricingPolicy policy = pricingPolicyProvider.determinePricingPolicy(
                        cart.getShoppingContext().getShopCode(), cart.getCurrencyCode(), cart.getCustomerEmail(),
                        cart.getShoppingContext().getCountryCode(),
                        cart.getShoppingContext().getStateCode()
                );

                final BigDecimal qty = cart.getOrderInfo().isMultipleDelivery() ? MULTI : SINGLE;

                final Pair<BigDecimal, BigDecimal> weightAndVolume = determineProductWeightAndVolume(cart);

                final boolean useWeight = MoneyUtils.isFirstBiggerThanSecond(weightAndVolume.getFirst(), BigDecimal.ZERO);
                final boolean useVolume = MoneyUtils.isFirstBiggerThanSecond(weightAndVolume.getSecond(), BigDecimal.ZERO);

                final SkuPrice priceByWeightMax = useWeight ? getSkuPrice(cart, carrierSlaId + "_KGMAX", policy, MAX) : null;
                final SkuPrice priceByWeight = useWeight && isValidPrice(priceByWeightMax) && MoneyUtils.isFirstBiggerThanOrEqualToSecond(priceByWeightMax.getQuantity(), weightAndVolume.getFirst()) ? getSkuPrice(cart, carrierSlaId + "_KG", policy, weightAndVolume.getFirst()) : null;
                final SkuPrice priceByVolumeMax = useVolume ? getSkuPrice(cart, carrierSlaId + "_M3MAX", policy, MAX) : null;
                final SkuPrice priceByVolume = useVolume && isValidPrice(priceByVolumeMax) && MoneyUtils.isFirstBiggerThanOrEqualToSecond(priceByVolumeMax.getQuantity(), weightAndVolume.getSecond()) ? getSkuPrice(cart, carrierSlaId + "_M3", policy, weightAndVolume.getSecond()) : null;

                SkuPrice price = null;
                if (useWeight && useVolume) {

                    if (isValidPrice(priceByWeight) && isValidPrice(priceByVolume)) {
                        // Assign price only if we have both valid prices

                        final BigDecimal salePriceByWeight = MoneyUtils.minPositive(priceByWeight.getRegularPrice(), priceByWeight.getSalePriceForCalculation());
                        final BigDecimal salePriceByVolume = MoneyUtils.minPositive(priceByVolume.getRegularPrice(), priceByVolume.getSalePriceForCalculation());

                        if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(salePriceByVolume, salePriceByWeight)) {
                            // Volume has higher cost
                            price = priceByVolume;
                        } else {
                            // Weight has higher cost
                            price = priceByWeight;
                        }

                    }

                } else if (useWeight) {
                    // Only weight is available
                    price = priceByWeight;
                } else if (useVolume) {
                    // Only volume is available
                    price = priceByVolume;
                }

                if (isValidPrice(price)) {

                    final BigDecimal salePrice = MoneyUtils.minPositive(price.getRegularPrice(), price.getSalePriceForCalculation());

                    cart.addShippingToCart(carrierSlaId, qty);
                    cart.setShippingPrice(carrierSlaId, salePrice, salePrice);
                    final BigDecimal deliveryCost = salePrice.multiply(qty).setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP);

                    return new TotalImpl(
                            Total.ZERO,
                            Total.ZERO,
                            Total.ZERO,
                            Total.ZERO,
                            false,
                            null,
                            Total.ZERO,
                            Total.ZERO,
                            Total.ZERO,
                            deliveryCost,
                            deliveryCost,
                            false,
                            null,
                            Total.ZERO,
                            deliveryCost,
                            deliveryCost,
                            Total.ZERO,
                            deliveryCost,
                            deliveryCost
                    );

                }

            }
        }
        return null;
    }

    boolean isValidPrice(SkuPrice price) {
        return price != null && price.getSkuPriceId() > 0L;
    }

    protected Pair<BigDecimal, BigDecimal> determineProductWeightAndVolume(final MutableShoppingCart cart) {

        BigDecimal weight = BigDecimal.ZERO;
        BigDecimal volume = BigDecimal.ZERO;

        for (final CartItem cartItem : cart.getCartItemList()) {

            Product product = productService.getProductBySkuCode(cartItem.getProductSkuCode());

            if (product != null) {

                try {
                    product = productService.getProductById(product.getProductId(), true);
                    final String kgSetting = product.getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG);
                    if (StringUtils.isNotBlank(kgSetting)) {
                        final BigDecimal kg = new BigDecimal(kgSetting);
                        weight = weight.add(kg.multiply(cartItem.getQty()));
                    }
                } catch (Exception exp) {
                    ShopCodeContext.getLog(this).error("Product {} does not have valid weight");
                }

                try {
                    final String m3Setting = product.getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3);
                    if (StringUtils.isNotBlank(m3Setting)) {
                        final BigDecimal m3 = new BigDecimal(m3Setting);
                        volume = volume.add(m3.multiply(cartItem.getQty()));
                    }
                } catch (Exception exp) {
                    ShopCodeContext.getLog(this).error("Product {} does not have valid volume");
                }

            }
        }

        return new Pair<BigDecimal, BigDecimal>(weight, volume);
    }

    protected SkuPrice getSkuPrice(final MutableShoppingCart cart, final String carrierSlaId, final PricingPolicyProvider.PricingPolicy policy, final BigDecimal qty) {

        return deliveryCostRegionalPriceResolver.getSkuPrice(cart, carrierSlaId, policy, qty);

    }

}
