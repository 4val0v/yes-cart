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

package org.yes.cart.payment.persistence.entity;


import java.io.Serializable;

/**
 * Payment gateway configuration parameters like merchantId, url, supported currencies, is it enabled etc.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public interface PaymentGatewayParameter extends Serializable, Descriptor, Auditable {

    /**
     * @return pk value
     */
    long getPaymentGatewayParameterId();

    /**
     * @param paymentGatewayParameterId pk value
     */
    void setPaymentGatewayParameterId(long paymentGatewayParameterId);


    /**
     * Get name.
     *
     * @return name
     */
    String getName();

    /**
     * Set name.
     *
     * @param name name to set.
     */
    void setName(String name);

    /**
     * Get value.
     *
     * @return value
     */
    String getValue();

    /**
     * Set value.
     *
     * @param value value to set.
     */
    void setValue(String value);

    /**
     * GEt payment gateway label.
     *
     * @return get pg label.
     */
    String getPgLabel();


    /**
     * Set pg label.
     *
     * @param pgLabel label.
     */
    void setPgLabel(String pgLabel);


    /**
     * Secure flag.
     *
     * @return true if value has sensitive data and needs special access.
     */
    boolean isSecure();

    /**
     * Set secure flag.
     *
     * @param secure flag value
     */
    void setSecure(boolean secure);


    /**
     * High level business type
     *
     * @return business type
     */
    String getBusinesstype();

    /**
     * Set business type.
     *
     * @param businesstype business type.
     */
    void setBusinesstype(String businesstype);


    /**
     * GUID is an auto generated identified that can help in entity synchronisation
     * and global identification.
     *
     * @return guid that uniquely identifies this object.
     */
    String getGuid();

    /**
     * @param guid guid that uniquely identifies this object.
     */
    void setGuid(String guid);

}
