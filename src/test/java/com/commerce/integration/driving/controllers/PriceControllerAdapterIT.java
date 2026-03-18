package com.commerce.integration.driving.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerAdapterIT {

    private static final String APPLICABLE_PRICE_PATH = "/api/v1/prices/applicable";
    private static final String PRODUCT_ID = "35455";
    private static final String BRAND_ID = "1";
    private static final String CURRENCY = "EUR";

    @Autowired
    private MockMvc mockMvc;

    private ResultActions performGetApplicablePrice(String applicationDate, String productId, String brandId) throws Exception {
        var mvcResult = mockMvc.perform(get(APPLICABLE_PRICE_PATH)
                        .param("applicationDate", applicationDate)
                        .param("productId", productId)
                        .param("brandId", brandId))
                .andExpect(request().asyncStarted())
                .andReturn();

        return mockMvc.perform(asyncDispatch(mvcResult));
    }

    @Test
    @DisplayName("Given application date at 10:00 on 2020-06-14 when getting applicable price then should return price list 1")
    void givenApplicationDateAt10hOn20200614WhenGetApplicablePriceThenShouldReturnPriceList1() throws Exception {
        performGetApplicablePrice("2020-06-14T10:00:00Z", PRODUCT_ID, BRAND_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price.productId").value(35455))
                .andExpect(jsonPath("$.price.brandId").value(1))
                .andExpect(jsonPath("$.price.priceList").value(1))
                .andExpect(jsonPath("$.price.price").value(35.5))
                .andExpect(jsonPath("$.price.currency").value(CURRENCY))
                .andExpect(jsonPath("$.price.startDate").value("2020-06-14T00:00:00Z"))
                .andExpect(jsonPath("$.price.endDate").value("2020-12-31T23:59:59Z"));
    }

    @Test
    @DisplayName("Given application date at 16:00 on 2020-06-14 when getting applicable price then should return price list 2")
    void givenApplicationDateAt16hOn20200614WhenGetApplicablePriceThenShouldReturnPriceList2() throws Exception {
        performGetApplicablePrice("2020-06-14T16:00:00Z", PRODUCT_ID, BRAND_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price.productId").value(35455))
                .andExpect(jsonPath("$.price.brandId").value(1))
                .andExpect(jsonPath("$.price.priceList").value(2))
                .andExpect(jsonPath("$.price.price").value(25.45))
                .andExpect(jsonPath("$.price.currency").value(CURRENCY))
                .andExpect(jsonPath("$.price.startDate").value("2020-06-14T15:00:00Z"))
                .andExpect(jsonPath("$.price.endDate").value("2020-06-14T18:30:00Z"));
    }

    @Test
    @DisplayName("Given application date at 21:00 on 2020-06-14 when getting applicable price then should return price list 1")
    void givenApplicationDateAt21hOn20200614WhenGetApplicablePriceThenShouldReturnPriceList1() throws Exception {
        performGetApplicablePrice("2020-06-14T21:00:00Z", PRODUCT_ID, BRAND_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price.priceList").value(1))
                .andExpect(jsonPath("$.price.price").value(35.5))
                .andExpect(jsonPath("$.price.currency").value(CURRENCY));
    }

    @Test
    @DisplayName("Given application date at 10:00 on 2020-06-15 when getting applicable price then should return price list 3")
    void givenApplicationDateAt10hOn20200615WhenGetApplicablePriceThenShouldReturnPriceList3() throws Exception {
        performGetApplicablePrice("2020-06-15T10:00:00Z", PRODUCT_ID, BRAND_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price.priceList").value(3))
                .andExpect(jsonPath("$.price.price").value(30.5))
                .andExpect(jsonPath("$.price.currency").value(CURRENCY));
    }

    @Test
    @DisplayName("Given application date at 21:00 on 2020-06-16 when getting applicable price then should return price list 4")
    void givenApplicationDateAt21hOn20200616WhenGetApplicablePriceThenShouldReturnPriceList4() throws Exception {
        performGetApplicablePrice("2020-06-16T21:00:00Z", PRODUCT_ID, BRAND_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price.priceList").value(4))
                .andExpect(jsonPath("$.price.price").value(38.95))
                .andExpect(jsonPath("$.price.currency").value(CURRENCY));
    }

    @Test
    @DisplayName("Given not existing brand id when getting applicable price then should return not found")
    void givenNotExistingBrandIdWhenGetApplicablePriceThenShouldReturnNotFound() throws Exception {
        performGetApplicablePrice("2020-06-14T10:00:00Z", PRODUCT_ID, "999")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Given invalid brand id when getting applicable price then should return bad request")
    void givenInvalidBrandIdWhenGetApplicablePriceThenShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(APPLICABLE_PRICE_PATH)
                        .param("applicationDate", "2020-06-14T10:00:00Z")
                        .param("productId", PRODUCT_ID)
                        .param("brandId", "a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parameter 'brandId' should be of type 'Long'"));
    }
}