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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.entity.ShopWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.WarehouseService;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 19:06
 */
public class WarehouseServiceCachedImpl implements WarehouseService {

    private final WarehouseService warehouseService;

    public WarehouseServiceCachedImpl(final WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    /** {@inheritDoc} */
    @Cacheable(value = "shopService-shopWarehouses")
    public List<Warehouse> getByShopId(final long shopId, final boolean includeDisabled) {
        return warehouseService.getByShopId(shopId, includeDisabled);
    }

    /** {@inheritDoc} */
    @Cacheable(value = "shopService-shopWarehousesMap")
    public Map<String, Warehouse> getByShopIdMapped(final long shopId, final boolean includeDisabled) {
        return warehouseService.getByShopIdMapped(shopId, includeDisabled);
    }



    /** {@inheritDoc} */
    public void updateShopWarehouseRank(final long shopWarehouseId, final int newRank) {
        warehouseService.updateShopWarehouseRank(shopWarehouseId, newRank);
    }

    /** {@inheritDoc} */
    public ShopWarehouse findShopWarehouseById(final long shopWarehouseId) {
        return warehouseService.findShopWarehouseById(shopWarehouseId);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "shopService-shopWarehouses",
            "shopService-shopWarehousesMap",
            "shopService-shopWarehousesIds",
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public void assignWarehouse(final long warehouseId, final long shopId, final boolean soft) {
        warehouseService.assignWarehouse(warehouseId, shopId, soft);
    }


    /** {@inheritDoc} */
    @CacheEvict(value = {
            "shopService-shopWarehouses",
            "shopService-shopWarehousesMap",
            "shopService-shopWarehousesIds",
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public void unassignWarehouse(final long warehouseId, final long shopId, final boolean soft) {
        warehouseService.unassignWarehouse(warehouseId, shopId, soft);
    }

    /** {@inheritDoc} */
    public List<Warehouse> findAll() {
        return warehouseService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public void findAllIterator(final ResultsIteratorCallback<Warehouse> callback) {
        warehouseService.findAllIterator(callback);
    }

    /** {@inheritDoc} */
    public Warehouse findById(final long pk) {
        return warehouseService.findById(pk);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "shopService-shopWarehouses",
            "shopService-shopWarehousesMap",
            "shopService-shopWarehousesIds",
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public Warehouse create(final Warehouse instance) {
        return warehouseService.create(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "shopService-shopWarehouses",
            "shopService-shopWarehousesMap",
            "shopService-shopWarehousesIds",
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public Warehouse update(final Warehouse instance) {
        return warehouseService.update(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value = {
            "shopService-shopWarehouses",
            "shopService-shopWarehousesMap",
            "shopService-shopWarehousesIds",
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public void delete(final Warehouse instance) {
        warehouseService.delete(instance);
    }

    /** {@inheritDoc} */
    public List<Warehouse> findByCriteria(final String eCriteria, final Object... parameters) {
        return warehouseService.findByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return warehouseService.findCountByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    public GenericDAO<Warehouse, Long> getGenericDao() {
        return warehouseService.getGenericDao();
    }

    /** {@inheritDoc} */
    public Warehouse findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return warehouseService.findSingleByCriteria(eCriteria, parameters);
    }
}
