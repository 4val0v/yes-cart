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

package org.yes.cart.domain.entity;

import java.time.LocalDateTime;


/**
 * Rule , that responsible to show particular content in named adv. place.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */

public interface ShopAdvPlaceRule extends Auditable, Rankable, Nameable {

    /**
     */
    long getShopadvrulesId();

    void setShopadvrulesId(long shopadvrulesId);

    /**
     */
    int getRank();

    void setRank(int rank);

    /**
     */
    String getName();

    void setName(String name);

    /**
     */
    String getDescription();

    void setDescription(String description);

    /**
     */
    LocalDateTime getAvailablefrom();

    void setAvailablefrom(LocalDateTime availablefrom);

    /**
     */
    LocalDateTime getAvailableto();

    void setAvailableto(LocalDateTime availableto);

    /**
     */
    String getRule();

    void setRule(String rule);

    /**
     */
    ShopAdvPlace getShopAdvPlace();

    void setShopAdvPlace(ShopAdvPlace shopAdvPlace);

}


