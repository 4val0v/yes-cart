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

import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Map;

/**
 * User: denis
 */
public class RestoreCartCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 2010522L;

    private final ShopService shopService;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     * @param shopService shop service
     */
    public RestoreCartCommandImpl(final ShoppingCartCommandRegistry registry,
                                  final ShopService shopService) {
        super(registry);
        this.shopService = shopService;
    }

    /**
     * @return command key
     */
    public String getCmdKey() {
        return CMD_RESTORE;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {

            setDefaultOrderInfoOptions(shoppingCart);
            setDefaultTaxOptions(shoppingCart);

            markDirty(shoppingCart);

        }
    }

    protected void setDefaultOrderInfoOptions(final MutableShoppingCart shoppingCart) {

        setCustomerOptions(shoppingCart);

    }

    protected void setDefaultTaxOptions(final MutableShoppingCart shoppingCart) {

        setTaxOptions(shoppingCart, null, null, null);

    }

}
