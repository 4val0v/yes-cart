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
package org.yes.cart.domain.dto.impl;


import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.domain.dto.CustomerOrderDetailDTO;
import org.yes.cart.domain.misc.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Customer order detail DTO interface implementation.
 */
@Dto
public class CustomerOrderDetailDTOImpl implements CustomerOrderDeliveryDetailDTO, CustomerOrderDetailDTO {

    private static final long serialVersionUID = 20120812L;

    @DtoField(value = "customerOrderDetId", readOnly = true)
    private long customerOrderDeliveryDetId;

    @DtoField(value = "productSkuCode", readOnly = true)
    private String skuCode;

    @DtoField(value = "productName", readOnly = true)
    private String skuName;


    @DtoField(value = "qty", readOnly = true)
    private BigDecimal qty;
    @DtoField(readOnly = true)
    private String supplierCode;
    @DtoField(readOnly = true)
    private String deliveryGroup;

    @DtoField(readOnly = true)
    private String deliveryRemarks;
    @DtoField(readOnly = true)
    private LocalDateTime deliveryEstimatedMin;
    @DtoField(readOnly = true)
    private LocalDateTime deliveryEstimatedMax;
    @DtoField(readOnly = true)
    private LocalDateTime deliveryGuaranteed;

    @DtoField(readOnly = true)
    private String b2bRemarks;

    @DtoField(value = "price", readOnly = true)
    private BigDecimal price;

    @DtoField(value = "listPrice", readOnly = true)
    private BigDecimal listPrice;
    @DtoField(value = "salePrice", readOnly = true)
    private BigDecimal salePrice;
    @DtoField(value = "gift", readOnly = true)
    private boolean gift;
    @DtoField(value = "promoApplied", readOnly = true)
    private boolean promoApplied;
    @DtoField(value = "appliedPromo", readOnly = true)
    private String appliedPromo;

    @DtoField(value = "netPrice", readOnly = true)
    private BigDecimal netPrice;
    @DtoField(value = "grossPrice", readOnly = true)
    private BigDecimal grossPrice;
    @DtoField(value = "taxRate", readOnly = true)
    private BigDecimal taxRate;
    @DtoField(value = "taxCode", readOnly = true)
    private String taxCode;
    @DtoField(value = "taxExclusiveOfPrice", readOnly = true)
    private boolean taxExclusiveOfPrice;

    private BigDecimal lineTotal;
    private BigDecimal lineTotalGross;
    private BigDecimal lineTotalNet;

    private BigDecimal lineTax;


    private String deliveryNum;

    private String deliveryStatusLabel;

    @DtoField(value = "allValues", readOnly = true)
    private Map<String, Pair<String, String>> allValues;

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(final BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public BigDecimal getLineTax() {
        return lineTax;
    }

    public void setLineTax(final BigDecimal lineTax) {
        this.lineTax = lineTax;
    }
    public BigDecimal getLineTotalGross() {
        return lineTotalGross;
    }

    public void setLineTotalGross(final BigDecimal lineTotalGross) {
        this.lineTotalGross = lineTotalGross;
    }

    public BigDecimal getLineTotalNet() {
        return lineTotalNet;
    }

    public void setLineTotalNet(final BigDecimal lineTotalNet) {
        this.lineTotalNet = lineTotalNet;
    }

    private void calculateLineTotal() {

        if (qty != null && netPrice != null && grossPrice != null) {
            lineTotal = qty.multiply(grossPrice).setScale(Constants.MONEY_SCALE, RoundingMode.HALF_UP);
            lineTotalGross = qty.multiply(grossPrice).setScale(Constants.MONEY_SCALE, RoundingMode.HALF_UP);
            lineTotalNet = qty.multiply(netPrice).setScale(Constants.MONEY_SCALE, RoundingMode.HALF_UP);
            lineTax = lineTotalGross.subtract(lineTotalNet);
        }

    }

    public long getId() {
        return customerOrderDeliveryDetId;
    }

    public long getCustomerOrderDeliveryDetId() {
        return customerOrderDeliveryDetId;
    }

    public void setCustomerOrderDeliveryDetId(final long customerOrderDeliveryDetId) {
        this.customerOrderDeliveryDetId = customerOrderDeliveryDetId;
    }

    public long getCustomerOrderDetId() {
        return customerOrderDeliveryDetId;
    }

    public void setCustomerOrderDetId(final long customerOrderDetId) {
        this.customerOrderDeliveryDetId = customerOrderDetId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(final String skuName) {
        this.skuName = skuName;
    }


    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getDeliveryGroup() {
        return deliveryGroup;
    }

    public void setDeliveryGroup(final String deliveryGroup) {
        this.deliveryGroup = deliveryGroup;
    }

    public String getDeliveryRemarks() {
        return deliveryRemarks;
    }

    public void setDeliveryRemarks(final String deliveryRemarks) {
        this.deliveryRemarks = deliveryRemarks;
    }

    public LocalDateTime getDeliveryEstimatedMin() {
        return deliveryEstimatedMin;
    }

    public void setDeliveryEstimatedMin(final LocalDateTime deliveryEstimatedMin) {
        this.deliveryEstimatedMin = deliveryEstimatedMin;
    }

    public LocalDateTime getDeliveryEstimatedMax() {
        return deliveryEstimatedMax;
    }

    public void setDeliveryEstimatedMax(final LocalDateTime deliveryEstimatedMax) {
        this.deliveryEstimatedMax = deliveryEstimatedMax;
    }

    public LocalDateTime getDeliveryGuaranteed() {
        return deliveryGuaranteed;
    }

    public void setDeliveryGuaranteed(final LocalDateTime deliveryGuaranteed) {
        this.deliveryGuaranteed = deliveryGuaranteed;
    }

    public LocalDateTime getDeliveryConfirmed() {
        return null;
    }

    public void setDeliveryConfirmed(final LocalDateTime delivered) {

    }

    public BigDecimal getDeliveredQuantity() {
        return null;
    }

    public void setDeliveredQuantity(final BigDecimal deliveredQuantity) {

    }

    public boolean isDeliveryRejected() {
        return false;
    }

    public void setDeliveryRejected(final boolean deliveryRejected) {

    }

    public boolean isDeliveryDifferent() {
        return false;
    }

    public void setDeliveryDifferent(final boolean deliveryDifferent) {

    }

    public String getB2bRemarks() {
        return b2bRemarks;
    }

    public void setB2bRemarks(final String b2bRemarks) {
        this.b2bRemarks = b2bRemarks;
    }

    public String getSupplierInvoiceNo() {
        return null;
    }

    public void setSupplierInvoiceNo(final String invoiceNo) {

    }

    public LocalDate getSupplierInvoiceDate() {
        return null;
    }

    public void setSupplierInvoiceDate(final LocalDate supplierInvoiceDate) {

    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(final BigDecimal qty) {
        this.qty = qty;
        calculateLineTotal();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(final BigDecimal netPrice) {
        this.netPrice = netPrice;
        calculateLineTotal();
    }

    public BigDecimal getGrossPrice() {
        return grossPrice;
    }

    public void setGrossPrice(final BigDecimal grossPrice) {
        this.grossPrice = grossPrice;
        calculateLineTotal();
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(final BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(final String taxCode) {
        this.taxCode = taxCode;
    }

    public boolean isTaxExclusiveOfPrice() {
        return taxExclusiveOfPrice;
    }

    public void setTaxExclusiveOfPrice(final boolean taxExclusiveOfPrice) {
        this.taxExclusiveOfPrice = taxExclusiveOfPrice;
    }

    public BigDecimal getListPrice() {
        return listPrice;
    }

    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(final BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public boolean isGift() {
        return gift;
    }

    public void setGift(final boolean gift) {
        this.gift = gift;
    }

    public boolean isPromoApplied() {
        return promoApplied;
    }

    public void setPromoApplied(final boolean promoApplied) {
        this.promoApplied = promoApplied;
    }

    public String getAppliedPromo() {
        return appliedPromo;
    }

    public void setAppliedPromo(final String appliedPromo) {
        this.appliedPromo = appliedPromo;
    }

    public String getDeliveryNum() {
        return deliveryNum;
    }

    public void setDeliveryNum(final String deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    public String getDeliveryStatusLabel() {
        return deliveryStatusLabel;
    }

    public void setDeliveryStatusLabel(final String deliveryStatusLabel) {
        this.deliveryStatusLabel = deliveryStatusLabel;
    }

    public Map<String, Pair<String, String>> getAllValues() {
        return allValues;
    }

    public void setAllValues(final Map<String, Pair<String, String>> allValues) {
        this.allValues = allValues;
    }

    public String toString() {
        return "CustomerOrderDeliveryDetailDTOImpl{" +
                "customerorderdeliveryId=" + customerOrderDeliveryDetId +
                ", skuCode='" + skuCode + '\'' +
                ", skuName='" + skuName + '\'' +
                ", qty=" + qty +
                ", price=" + price +
                ", netPrice=" + netPrice +
                ", grossPrice=" + grossPrice +
                ", listPrice=" + listPrice +
                ", deliveryNum='" + deliveryNum + '\'' +
                ", deliveryStatusLabel='" + deliveryStatusLabel + '\'' +
                '}';
    }
}
