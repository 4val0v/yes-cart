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

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.service.domain.ShoppingCartStateService;

import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 22/08/2014
 * Time: 14:02
 */
public class TestShoppingCartStateServiceImpl extends BaseCoreDBTestCase {

    private ShoppingCartStateService shoppingCartStateService;


    @Before
    public void setUp()  {
        shoppingCartStateService = (ShoppingCartStateService) ctx().getBean(ServiceSpringKeys.SHOPPING_CART_STATE_SERVICE);
        super.setUp();
    }


    @Test
    public void testFindByGuidOrEmail() {
        List<ShoppingCartState> carts;
        assertNull(shoppingCartStateService.findByGuid("CART-001"));
        carts = shoppingCartStateService.findByCustomerEmail("bob@doe.com");
        assertNotNull(carts);
        assertTrue(carts.isEmpty());

        final ShoppingCartState scs = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        scs.setGuid("CART-001");
        scs.setCustomerEmail("bob@doe.com");
        scs.setState("State".getBytes());
        shoppingCartStateService.create(scs);

        assertNotNull(shoppingCartStateService.findByGuid("CART-001"));
        carts = shoppingCartStateService.findByCustomerEmail("bob@doe.com");
        assertNotNull(carts);
        assertEquals(carts.size(), 1);

        shoppingCartStateService.delete(scs);

        assertNull(shoppingCartStateService.findByGuid("CART-001"));
        carts = shoppingCartStateService.findByCustomerEmail("bob@doe.com");
        assertNotNull(carts);
        assertTrue(carts.isEmpty());
    }

    @Test
    public void testFindByModificationPrior() {

        final ShoppingCartState scs = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        scs.setGuid("CART-001");
        scs.setCustomerEmail("bob@doe.com");
        scs.setEmpty(false);
        scs.setState("State".getBytes());
        shoppingCartStateService.create(scs);

        final Instant tenSecondsAfterCreation = Instant.now().plusSeconds(10L);
        final Instant tenSecondsBeforeCreation = tenSecondsAfterCreation.plusSeconds(-20L);

        getTxReadOnly().execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(final TransactionStatus status) {
                ResultsIterator<ShoppingCartState> carts = shoppingCartStateService.findByModificationPrior(tenSecondsBeforeCreation);
                assertNotNull(carts);
                assertFalse(carts.hasNext());
                carts.close();
                return null;
            }
        });

        getTxReadOnly().execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(final TransactionStatus status) {
                ResultsIterator<ShoppingCartState> carts = shoppingCartStateService.findByModificationPrior(tenSecondsAfterCreation);
                assertNotNull(carts);
                assertTrue(carts.hasNext()); // one
                assertFalse(carts.hasNext()); // no second
                carts.close();
                return null;
            }
        });

        shoppingCartStateService.delete(shoppingCartStateService.findByGuid("CART-001"));

    }

    @Test
    public void testFindByModificationPriorEmptyAnonymousFalse() {

        final ShoppingCartState scs = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        scs.setGuid("CART-001");
        scs.setCustomerEmail("bob@doe.com");
        scs.setEmpty(true);
        scs.setState("State".getBytes());
        shoppingCartStateService.create(scs);

        final Instant tenSecondsAfterCreation = Instant.now().plusSeconds(10L);
        final Instant tenSecondsBeforeCreation = tenSecondsAfterCreation.plusSeconds(-20L);

        getTxReadOnly().execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(final TransactionStatus status) {
                ResultsIterator<ShoppingCartState> carts = shoppingCartStateService.findByModificationPrior(tenSecondsBeforeCreation, true);
                assertNotNull(carts);
                assertFalse(carts.hasNext());
                carts.close();
                return null;
            }
        });

        getTxReadOnly().execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(final TransactionStatus status) {
                ResultsIterator<ShoppingCartState> carts = shoppingCartStateService.findByModificationPrior(tenSecondsAfterCreation, true);
                assertNotNull(carts);
                assertFalse(carts.hasNext());
                carts.close();
                return null;
            }
        });

        shoppingCartStateService.delete(shoppingCartStateService.findByGuid("CART-001"));

    }

    @Test
    public void testFindByModificationPriorEmptyAnonymousTrue() {

        final ShoppingCartState scs = shoppingCartStateService.getGenericDao().getEntityFactory().getByIface(ShoppingCartState.class);
        scs.setGuid("CART-001");
        scs.setState("State".getBytes());
        shoppingCartStateService.create(scs);

        final Instant tenSecondsAfterCreation = Instant.now().plusSeconds(10L);
        final Instant tenSecondsBeforeCreation = tenSecondsAfterCreation.plusSeconds(-20L);

        getTxReadOnly().execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(final TransactionStatus status) {
                ResultsIterator<ShoppingCartState> carts = shoppingCartStateService.findByModificationPrior(tenSecondsBeforeCreation, true);
                assertNotNull(carts);
                assertFalse(carts.hasNext());
                carts.close();
                return null;
            }
        });

        getTxReadOnly().execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(final TransactionStatus status) {
                ResultsIterator<ShoppingCartState> carts = shoppingCartStateService.findByModificationPrior(tenSecondsAfterCreation, true);
                assertNotNull(carts);
                assertTrue(carts.hasNext()); // one
                assertFalse(carts.hasNext()); // no second
                carts.close();
                return null;
            }
        });

        shoppingCartStateService.delete(shoppingCartStateService.findByGuid("CART-001"));

    }

}
