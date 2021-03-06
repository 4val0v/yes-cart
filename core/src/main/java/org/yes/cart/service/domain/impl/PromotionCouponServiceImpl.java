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

package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.entity.PromotionCoupon;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.promotion.PromotionCouponCodeGenerator;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.util.TimeContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * User: denispavlov
 * Date: 03/06/2014
 * Time: 18:35
 */
public class PromotionCouponServiceImpl extends BaseGenericServiceImpl<PromotionCoupon> implements PromotionCouponService {

    private final GenericDAO<Promotion, Long> promotionDao;
    private final PromotionCouponCodeGenerator couponCodeGenerator;

    public PromotionCouponServiceImpl(final GenericDAO<PromotionCoupon, Long> promotionCouponDao,
                                      final GenericDAO<Promotion, Long> promotionDao,
                                      final PromotionCouponCodeGenerator couponCodeGenerator) {
        super(promotionCouponDao);
        this.promotionDao = promotionDao;
        this.couponCodeGenerator = couponCodeGenerator;
    }

    /** {@inheritDoc} */
    public List<PromotionCoupon> findByPromotionId(Long promotionId) {
        return getGenericDao().findByNamedQuery("COUPONS.BY.PROMOTION.ID", promotionId);
    }

    /** {@inheritDoc} */
    public void create(Long promotionId, String couponCode, int limit, int limitPerUser) {

        final List<Object> promoIdAndCode = getGenericDao().findQueryObjectByNamedQuery("PROMOTION.ID.AND.CODE.BY.COUPON.CODE", couponCode);
        if (!promoIdAndCode.isEmpty()) {

            final Pair<Long, String> promoIdAndCodePair = (Pair<Long, String>) promoIdAndCode.get(0);

            if (promoIdAndCodePair.getFirst().equals(promotionId)) {
                return; // we already have this
            }
            throw new IllegalArgumentException("Coupon code '" + couponCode + "' already used in promotion: " + promoIdAndCodePair.getSecond());
        }

        final Promotion promotion = this.promotionDao.findById(promotionId);
        if (promotion == null || !promotion.isCouponTriggered()) {
            throw new IllegalArgumentException("Coupon code '" + couponCode + "' cannot be added to non-coupon promotion: " + promotion.getCode());
        }
        final PromotionCoupon coupon = getGenericDao().getEntityFactory().getByIface(PromotionCoupon.class);
        coupon.setPromotion(promotion);
        coupon.setCode(couponCode);
        coupon.setUsageLimit(limit);
        coupon.setUsageLimitPerCustomer(limitPerUser);
        coupon.setUsageCount(0);

        this.getGenericDao().saveOrUpdate(coupon);

    }

    /** {@inheritDoc} */
    public void create(Long promotionId, int couponCount, int limit, int limitPerUser) {

        final Promotion promotion = this.promotionDao.findById(promotionId);
        if (promotion == null || !promotion.isCouponTriggered()) {
            throw new IllegalArgumentException("Coupon codes cannot be added to non-coupon promotion: " + promotion.getCode());
        }

        for (int i = 0; i < couponCount; i++) {

            String couponCode;
            List<Object> promoIdAndCode;

            do {
                couponCode = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
                promoIdAndCode = getGenericDao().findQueryObjectByNamedQuery("PROMOTION.ID.AND.CODE.BY.COUPON.CODE", couponCode);
            } while (!promoIdAndCode.isEmpty());

            final PromotionCoupon coupon = getGenericDao().getEntityFactory().getByIface(PromotionCoupon.class);
            coupon.setPromotion(promotion);
            coupon.setCode(couponCode);
            coupon.setUsageLimit(limit);
            coupon.setUsageLimitPerCustomer(limitPerUser);
            coupon.setUsageCount(0);

            this.getGenericDao().saveOrUpdate(coupon);

        }

    }

    /** {@inheritDoc} */
    public PromotionCoupon findValidPromotionCoupon(String coupon, String customerEmail) {

        // Get enabled coupon code usage limit of which is greater than usage count
        final LocalDateTime now = now();
        final PromotionCoupon couponEntity = getGenericDao().findSingleByNamedQuery("ENABLED.COUPON.BY.CODE",
                coupon, true, now, now);
        if (couponEntity == null) {
            return null;
        }

        // if we have customer usage limit
        if (couponEntity.getUsageLimitPerCustomer() > 0) {
            final List<Object> count = getGenericDao()
                    .findQueryObjectByNamedQuery("COUPON.USAGE.BY.ID.AND.EMAIL",
                            couponEntity.getPromotioncouponId(), customerEmail, CustomerOrder.ORDER_STATUS_NONE);
            if (!count.isEmpty()) {

                final Number usage = (Number) count.get(0);
                // valid coupon only when we have not exceeded the limit
                if (usage.intValue() >= couponEntity.getUsageLimitPerCustomer()) {
                    return null;
                }

            }
        }

        return couponEntity;
    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }

    /** {@inheritDoc} */
    public void updateUsage(final PromotionCoupon promotionCoupon, final int offset) {

        final List<Object> count = getGenericDao()
                .findQueryObjectByNamedQuery("COUPON.USAGE.BY.COUPON.ID",
                        promotionCoupon.getPromotioncouponId(), CustomerOrder.ORDER_STATUS_NONE);

        int usage = offset;
        if (!count.isEmpty()) {

            final Number usageRecordsCount = (Number) count.get(0);
            usage += usageRecordsCount.intValue();

        }

        promotionCoupon.setUsageCount(usage);
        getGenericDao().saveOrUpdate(promotionCoupon);

    }
}
