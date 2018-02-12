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

package org.yes.cart.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.yes.cart.service.domain.ShopTopSellerService;

import java.time.LocalDateTime;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TopSellerCounterJobBeanImpl extends QuartzJobBean {

    private static final Logger LOG = LoggerFactory.getLogger(TopSellerCounterJobBeanImpl.class);

    private ShopTopSellerService shopTopSellerService;

    private int calculationPeriodInDays = 10;

    /**
     * {@inheritDoc}
     */
    protected void executeInternal(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOG.info("TopSellerCounterJobBeanImpl start at {}", LocalDateTime.now());
        shopTopSellerService.updateTopSellers (calculationPeriodInDays);
        LOG.info("TopSellerCounterJobBeanImpl   end at {}", LocalDateTime.now());
    }

    /**
     * Set  service to use.
     * @param shopTopSellerService   service to use.
     */
    public void setShopTopSellerService(final ShopTopSellerService shopTopSellerService) {
        this.shopTopSellerService = shopTopSellerService;
    }


    /**
     * Set period for calclate top sellers.
     *
     * @param calculationPeriodInDays period for calclate top sellers.
     */
    public void setCalculationPeriodInDays(final int calculationPeriodInDays) {
        this.calculationPeriodInDays = calculationPeriodInDays;
    }
}
