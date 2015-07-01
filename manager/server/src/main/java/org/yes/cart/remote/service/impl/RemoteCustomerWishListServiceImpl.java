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

package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.CustomerWishListDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteCustomerWishListService;
import org.yes.cart.service.dto.DtoCustomerWishListService;
import org.yes.cart.service.dto.GenericDTOService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteCustomerWishListServiceImpl
        extends AbstractRemoteService<CustomerWishListDTO>
        implements RemoteCustomerWishListService {

    /**
     * Construct service.
     *
     * @param customerWishListDTOGenericDTOService
     *         dto service to use.
     */
    public RemoteCustomerWishListServiceImpl(final GenericDTOService<CustomerWishListDTO> customerWishListDTOGenericDTOService) {
        super(customerWishListDTOGenericDTOService);
    }

    /**
     * {@inheritDoc}
     */
    public List<CustomerWishListDTO> getByCustomerId(final long customerId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return ((DtoCustomerWishListService) getGenericDTOService()).getByCustomerId(customerId);
    }
}
