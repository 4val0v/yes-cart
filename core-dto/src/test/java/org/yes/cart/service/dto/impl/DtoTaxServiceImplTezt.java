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

package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.DtoServiceSpringKeys;
import org.yes.cart.domain.dto.TaxDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.dto.DtoTaxService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 31/10/2014
 * Time: 13:10
 */
public class DtoTaxServiceImplTezt extends BaseCoreDBTestCase {

    private DtoTaxService dtoTaxService;
    private DtoFactory dtoFactory;

    @Before
    public void setUp() {
        dtoTaxService = (DtoTaxService) ctx().getBean(DtoServiceSpringKeys.DTO_TAX_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(DtoServiceSpringKeys.DTO_FACTORY);
        super.setUp();
    }


    @Test
    public void testFindByParameters() throws Exception {
        TaxDTO taxDTO = getDto();
        taxDTO = dtoTaxService.create(taxDTO);

        // retrieve specific
        List<TaxDTO> taxes = dtoTaxService.findByParameters(
                taxDTO.getCode(),
                taxDTO.getShopCode(),
                taxDTO.getCurrency());

        assertNotNull(taxes);
        assertEquals(1, taxes.size());

        // retrieve all
        taxes = dtoTaxService.findByParameters(
                null,
                null,
                null);

        assertNotNull(taxes);
        assertEquals(9, taxes.size());

        // retrieve non-existent
        taxes = dtoTaxService.findByParameters(
                "zzzz",
                null,
                null);

        assertNotNull(taxes);
        assertEquals(0, taxes.size());

        dtoTaxService.remove(taxDTO.getTaxId());
    }


    @Test
    public void testFindBy() throws Exception {
        TaxDTO taxDTO = getDto();
        taxDTO = dtoTaxService.create(taxDTO);

        // retrieve specific
        List<TaxDTO> taxes = dtoTaxService.findBy(
                taxDTO.getShopCode(),
                taxDTO.getCurrency(),
                taxDTO.getCode(),
                0,
                10);

        assertNotNull(taxes);
        assertEquals(1, taxes.size());

        // retrieve all EUR
        taxes = dtoTaxService.findBy(
                "SHOIP1",
                "EUR",
                null,
                0,
                10);

        assertNotNull(taxes);
        assertEquals(7, taxes.size());


        // retrieve exclusive EUR
        taxes = dtoTaxService.findBy(
                "SHOIP1",
                "EUR",
                "--",
                0,
                10);

        assertNotNull(taxes);
        assertEquals(6, taxes.size());

        // retrieve exclusive EUR
        taxes = dtoTaxService.findBy(
                "SHOIP1",
                "EUR",
                "++",
                0,
                10);

        assertNotNull(taxes);
        assertEquals(1, taxes.size());


        // retrieve exclusive EUR 10%
        taxes = dtoTaxService.findBy(
                "SHOIP1",
                "EUR",
                "-%10",
                0,
                10);

        assertNotNull(taxes);
        assertEquals(2, taxes.size());
        assertEquals(10, taxes.get(0).getTaxRate().setScale(0, RoundingMode.HALF_UP).intValue());


        // retrieve EUR 5%
        taxes = dtoTaxService.findBy(
                "SHOIP1",
                "EUR",
                "%5",
                0,
                10);

        assertNotNull(taxes);
        assertEquals(3, taxes.size());
        assertEquals(5, taxes.get(0).getTaxRate().setScale(0, RoundingMode.HALF_UP).intValue());


        dtoTaxService.remove(taxDTO.getTaxId());
    }

    @Test
    public void testCreate() throws Exception {
        TaxDTO taxDTO = getDto();
        taxDTO = dtoTaxService.create(taxDTO);
        assertTrue(taxDTO.getTaxId() > 0);
        dtoTaxService.remove(taxDTO.getTaxId());
    }

    @Test
    public void testUpdate() throws Exception {
        TaxDTO taxDTO = getDto();
        taxDTO = dtoTaxService.create(taxDTO);
        assertTrue(taxDTO.getTaxId() > 0);
        taxDTO.setDescription("other desc");
        taxDTO = dtoTaxService.update(taxDTO);
        assertEquals("other desc", taxDTO.getDescription());
        dtoTaxService.remove(taxDTO.getTaxId());
    }

    @Test
    public void testRemove() throws Exception {
        TaxDTO taxDTO = getDto();
        taxDTO = dtoTaxService.create(taxDTO);
        assertTrue(taxDTO.getTaxId() > 0);
        long id = taxDTO.getTaxId();
        dtoTaxService.remove(id);
        taxDTO = dtoTaxService.getById(id);
        assertNull(taxDTO);
    }


    private TaxDTO getDto() {

        TaxDTO taxDTO = dtoFactory.getByIface(TaxDTO.class);
        taxDTO.setShopCode("SHOIP1");
        taxDTO.setCurrency("EUR");
        taxDTO.setCode("TESTCODE");
        taxDTO.setTaxRate(new BigDecimal("20.00"));
        taxDTO.setExclusiveOfPrice(true);
        taxDTO.setDescription("Test description");

        return taxDTO;
    }
}
