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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.search.dao.support.ShopCategoryRelationshipSupport;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.DomainApiUtils;
import org.yes.cart.util.TimeContext;
import org.yes.cart.util.log.Markers;

import java.time.LocalDateTime;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ShopServiceImpl extends BaseGenericServiceImpl<Shop> implements ShopService {

    private static final Logger LOG = LoggerFactory.getLogger(ShopServiceImpl.class);

    private final GenericDAO<Shop, Long> shopDao;

    private final AttributeService attributeService;

    private final CategoryService categoryService;
    private final ContentService contentService;

    private final ShopCategoryRelationshipSupport shopCategoryRelationshipSupport;


    /**
     * Construct shop service.
     * @param shopDao shop doa.
     * @param categoryService {@link CategoryService}
     * @param contentService {@link ContentService}
     * @param attributeService attribute service
     * @param shopCategoryRelationshipSupport support
     */
    public ShopServiceImpl(final GenericDAO<Shop, Long> shopDao,
                           final CategoryService categoryService,
                           final ContentService contentService,
                           final AttributeService attributeService,
                           final ShopCategoryRelationshipSupport shopCategoryRelationshipSupport) {
        super(shopDao);
        this.shopDao = shopDao;
        this.categoryService = categoryService;
        this.attributeService = attributeService;
        this.contentService = contentService;
        this.shopCategoryRelationshipSupport = shopCategoryRelationshipSupport;
    }

    /**
     * {@inheritDoc}
     */
    public Shop getShopByOrderGuid(final String orderGuid) {
        return shopDao.findSingleByNamedQuery("SHOP.BY.ORDER.GUID", orderGuid);
    }

    /**
     * {@inheritDoc}
     */
    public Shop getShopByCode(final String shopCode) {
        return shopDao.findSingleByNamedQuery("SHOP.BY.CODE", shopCode);
    }

    /**
     * {@inheritDoc}
     */
    public List<Shop> getNonSubShops() {
        return shopDao.findByNamedQuery("SHOP.NONSUB.ONLY");
    }


    /** {@inheritDoc} */
    public Map<Long, Set<Long>> getAllShopsAndSubs() {
        final List<Shop> all = this.proxy().getAll();
        final Map<Long, Set<Long>> shopsMap = new HashMap<Long, Set<Long>>();
        for (final Shop shop : all) {
            if (shop.getMaster() == null) {
                final Set<Long> subs = shopsMap.get(shop.getShopId());
                if (subs == null) {
                    shopsMap.put(shop.getShopId(), new HashSet<Long>());
                }
            } else {
                Set<Long> subs = shopsMap.get(shop.getMaster().getShopId());
                if (subs == null) {
                    subs = new HashSet<Long>();
                    shopsMap.put(shop.getMaster().getShopId(), subs);
                }
                subs.add(shop.getShopId());
            }
        }
        return shopsMap;
    }


    /**
     * {@inheritDoc}
     */
    public List<Shop> getSubShopsByMaster(final long masterId) {
        return shopDao.findByNamedQuery("SHOP.BY.MASTER.ID", masterId);
    }

    /**
     * {@inheritDoc}
     */
    public Shop getSubShopByNameAndMaster(final String shopName, final long masterId) {
        return shopDao.findSingleByNamedQuery("SHOP.BY.NAME.AND.MASTER.ID", shopName, masterId);
    }

    /**
     * {@inheritDoc}
     */
    public Shop getShopByOrderNum(final String orderNum) {
        return shopDao.findSingleByNamedQuery("SHOP.BY.ORDER.NUM", orderNum);
    }


    /**
     * {@inheritDoc}
     */
    public Shop getById(final long shopId) {
        return shopDao.findById(shopId);
    }

    /**
     * {@inheritDoc}
     */
    public List<Shop> getAll() {
        return super.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public Shop getShopByDomainName(final String serverName) {
        return shopDao.findSingleByNamedQuery("SHOP.BY.URL", serverName);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Long> getShopCategoriesIds(final long shopId) {
        return shopCategoryRelationshipSupport.getShopCategoriesIds(shopId);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Long> getShopContentIds(final long shopId) {
        return shopCategoryRelationshipSupport.getShopContentIds(shopId);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Long> getShopAllCategoriesIds(final long shopId) {
        final Set<Long> all = new HashSet<Long>();
        all.addAll(getShopCategoriesIds(shopId));
        all.addAll(getShopContentIds(shopId));
        return all;
    }

    public Set<Long> transform(final Collection<Category> categories) {
        final Set<Long> result = new LinkedHashSet<Long>(categories.size());
        for (Category category : categories) {
            result.add(category.getCategoryId());
        }
        return result;
    }


    /** {@inheritDoc} */
    public List<String> findAllSupportedCurrenciesByShops() {
        final List<Shop> shops = shopDao.findAll();
        final Set<String> currencies = new TreeSet<String>();

        for(Shop shop : shops) {
            final String shopCurrencies = shop.getSupportedCurrencies();
            if (StringUtils.isNotBlank(shopCurrencies)) {
                currencies.addAll(Arrays.asList(shopCurrencies.split(",")));
            }
        }

        return new ArrayList<>(currencies);
    }

    /**
     * Get the top level categories assigned to shop.
     *
     * @param shopId given shop
     * @return list of assigned top level categories
     */
    public Set<Category> getTopLevelCategories(final Long shopId) {

        final List<ShopCategory> top = (List) shopDao.findQueryObjectByNamedQuery("ALL.TOPCATEGORIES.BY.SHOPID", shopId);
        final Set<Category> cats = new HashSet<Category>();
        final LocalDateTime now = now();
        for (final ShopCategory shopCategory : top) {
            final Category category = categoryService.getById(shopCategory.getCategory().getCategoryId());
            if (DomainApiUtils.isObjectAvailableNow(true, category.getAvailablefrom(), category.getAvailableto(), now)) {
                cats.add(category);
            }
        }
        return cats;

    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Category> findAllByShopId(final long shopId) {

        final List<ShopCategory> top = (List) shopDao.findQueryObjectByNamedQuery("ALL.TOPCATEGORIES.BY.SHOPID", shopId);
        final Set<Category> cats = new HashSet<Category>();
        for (final ShopCategory shopCategory : top) {
            final Category category = categoryService.findById(shopCategory.getCategory().getCategoryId());
            cats.add(category);
        }
        return cats;

    }

    /**
     * {@inheritDoc}
     */
    public Category getDefaultNavigationCategory(final long shopId) {

        final Shop shop = proxy().getById(shopId);

        final String categoryGuid = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_DEFAULT_NAVIGATION_CATEGORY);

        if (StringUtils.isNotBlank(categoryGuid)) {

            final List<Category> categories = categoryService.findByCriteria(" where e.guid = ?1", categoryGuid);

            if (CollectionUtils.isNotEmpty(categories)) {

                return categories.get(0);

            }

            LOG.warn(Markers.alert(), "Invalid default navigation category setting for shop {}", shop.getCode());

        } else if (shop.getMaster() != null) {

            return proxy().getDefaultNavigationCategory(shop.getMaster().getShopId());

        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Long getShopCategoryParentId(final long shopId, final long categoryId) {

        final Set<Long> shopCatIds = getShopCategoriesIds(shopId);

        if (shopCatIds.contains(categoryId)) {

            final Category category = categoryService.getById(categoryId);
            if (category != null && !category.isRoot()) {

                final List<Long> links = categoryService.getCategoryLinks(category.getParentId());
                if (!links.isEmpty()) {

                    for (final Long linkId : links) {
                        if (shopCatIds.contains(linkId)) {
                            // We have a symlink for current shop
                            return linkId;
                        }
                    }
                }

                if (shopCatIds.contains(category.getParentId())) {
                    // Use master catalog parent
                    return category.getParentId();
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getShopCategoryTemplate(final long shopId, final long categoryId) {
        final Category category = categoryService.getById(categoryId);
        if (category != null && !category.isRoot()) {
            if (StringUtils.isBlank(category.getUitemplate())) {

                final Long parentId = proxy().getShopCategoryParentId(shopId, categoryId);
                if (parentId != null) {
                    return proxy().getShopCategoryTemplate(shopId, parentId);
                }
            } else {
                return category.getUitemplate();
            }
        }

        final Category defaultCategory = proxy().getDefaultNavigationCategory(shopId);
        if (defaultCategory != null && StringUtils.isNotBlank(defaultCategory.getUitemplate())) {

            return defaultCategory.getUitemplate();

        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getShopCategorySearchTemplate(final long shopId, final long categoryId) {
        final Category category = categoryService.getById(categoryId);
        if (category != null && !category.isRoot()) {

            final String template = category.getProductType() != null ? category.getProductType().getUisearchtemplate() : null;

            if (StringUtils.isBlank(template)) {
                final Long parentId = proxy().getShopCategoryParentId(shopId, categoryId);
                if (parentId != null) {
                    return proxy().getShopCategorySearchTemplate(shopId, parentId);
                }
            } else {
                return template;
            }
        }

        final Category defaultCategory = proxy().getDefaultNavigationCategory(shopId);
        if (defaultCategory != null && defaultCategory.getProductType() != null &&
                StringUtils.isNotBlank(defaultCategory.getProductType().getUisearchtemplate())) {

            return defaultCategory.getProductType().getUisearchtemplate();

        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Long getShopCategoryProductTypeId(final long shopId, final long categoryId) {
        final Category category = categoryService.getById(categoryId);
        if (category != null && !category.isRoot()) {
            if (category.getProductType() == null) {
                final Long parentId = proxy().getShopCategoryParentId(shopId, categoryId);
                if (parentId != null) {
                    return proxy().getShopCategoryProductTypeId(shopId, parentId);
                }
            } else {
                return category.getProductType().getProducttypeId();
            }
        }

        final Category defaultCategory = proxy().getDefaultNavigationCategory(shopId);
        if (defaultCategory != null && defaultCategory.getProductType() != null) {

            return defaultCategory.getProductType().getProducttypeId();

        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Long findShopIdByCode(final String code) {
        List<Object> list = shopDao.findQueryObjectByNamedQuery("SHOP.ID.BY.SHOPCODE", code);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        list = shopDao.findQueryObjectByNamedQuery("SHOP.ID.BY.SHOPALIAS", code);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }

        return null;
    }

    /**
     * Set attribute value. New attribute value will be created, if attribute has no value for given shop.
     *
     * @param shopId shop id
     * @param attributeKey attribute key
     * @param attributeValue attribute value.
     */
    public void updateAttributeValue(final long shopId, final String attributeKey, final String attributeValue) {
        final Shop shop = shopDao.findById(shopId);
        if (shop != null) {
            AttrValueShop attrValueShop = shop.getAttributeByCode(attributeKey);
            if (attrValueShop == null) {
                final Attribute attribute = attributeService.findByAttributeCode(attributeKey);
                if (attribute != null) {
                    attrValueShop = getGenericDao().getEntityFactory().getByIface(AttrValueShop.class);
                    attrValueShop.setVal(attributeValue);
                    attrValueShop.setAttributeCode(attribute.getCode());
                    attrValueShop.setShop(shop);
                    shop.getAttributes().add(attrValueShop);
                }
            } else {
                attrValueShop.setVal(attributeValue);
            }
            shopDao.update(shop);
        }
    }

    /** {@inheritDoc} */
    public Shop create(final Shop instance) {
        final Shop shop = super.create(instance);
        final Category category = contentService.getRootContent(shop.getShopId());
        if (category == null) {
            contentService.createRootContent(shop.getShopId());
        }
        return shop;
    }


    private ShopService proxy;

    private ShopService proxy() {
        if (proxy == null) {
            proxy = getSelf();
        }
        return proxy;
    }

    /**
     * @return self proxy to reuse AOP caching
     */
    public ShopService getSelf() {
        // Spring lookup method to get self proxy
        return null;
    }



}
