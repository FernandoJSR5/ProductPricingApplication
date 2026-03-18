package com.commerce.driving.controllers.adapters;

import com.commerce.application.ports.driving.PriceServicePort;
import com.commerce.common.enums.CommonErrorCodes;
import com.commerce.common.exceptions.PriceException;
import com.commerce.domain.entities.Price;
import com.commerce.domain.model.PriceResponse;
import com.commerce.driving.controllers.mappers.PriceResponseMapper;
import com.commerce.driving.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceControllerAdapter.class)
@Import(GlobalExceptionHandler.class)
class PriceControllerAdapterTest {

    private static final DateTimeFormatter OFFSET_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");

    private static final String APPLICABLE_PRICE_PATH = "/api/v1/prices/applicable";
    private static final String INVALID_BRAND_ID_MESSAGE = "Parameter 'brandId' should be of type 'Long'";
    private static final String MISSING_APPLICATION_DATE_MESSAGE = "Missing required parameter: 'applicationDate'";
    private static final Long PRODUCT_ID = 35455L;
    private static final Long BRAND_ID = 1L;
    private static final String CURRENCY = "EUR";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceServicePort priceServicePort;

    @MockBean
    private PriceResponseMapper priceResponseMapper;

    @ParameterizedTest(name = "[{index}] applicationDate={0}, expectedPriceList={3}, expectedPrice={4}")
    @MethodSource("applicablePriceCases")
    @DisplayName("Given valid parameters when getting applicable price then should return expected price")
    void givenValidParametersWhenGetApplicablePriceThenShouldReturnExpectedPrice(
            String applicationDate,
            String startDate,
            String endDate,
            Long priceList,
            double price) throws Exception {

        var dateTime = OffsetDateTime.parse(applicationDate);
        var expectedPrice = buildPrice(startDate, endDate, priceList, price);
        var expectedResponse = buildExpectedPriceResponse(expectedPrice);

        stubApplicablePrice(dateTime, expectedPrice, expectedResponse);

        performApplicablePriceRequest(applicationDate, PRODUCT_ID.toString(), BRAND_ID.toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price.productId").value(PRODUCT_ID))
                .andExpect(jsonPath("$.price.brandId").value(BRAND_ID))
                .andExpect(jsonPath("$.price.priceList").value(priceList))
                .andExpect(jsonPath("$.price.price").value(price))
                .andExpect(jsonPath("$.price.currency").value(CURRENCY))
                .andExpect(jsonPath("$.price.startDate")
                        .value(expectedResponse.getPrice().getStartDate().format(OFFSET_DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.price.endDate")
                        .value(expectedResponse.getPrice().getEndDate().format(OFFSET_DATE_TIME_FORMATTER)));
    }

    @Test
    @DisplayName("Given not existing price when getting applicable price then should return not found")
    void givenNotExistingPriceWhenGetApplicablePriceThenShouldReturnNotFound() throws Exception {
        var applicationDate = "2020-06-14T10:00:00Z";
        var dateTime = OffsetDateTime.parse(applicationDate);
        var notExistingBrandId = 3L;

        given(priceServicePort.getApplicablePrice(PRODUCT_ID, notExistingBrandId, LocalDateTime.from(dateTime)))
                .willReturn(CompletableFuture.failedFuture(new PriceException()));

        performApplicablePriceRequest(applicationDate, PRODUCT_ID.toString(), String.valueOf(notExistingBrandId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(CommonErrorCodes.NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("Given invalid brand id when getting applicable price then should return bad request")
    void givenInvalidBrandIdWhenGetApplicablePriceThenShouldReturnBadRequest() throws Exception {
        performApplicablePriceRequestSync(PRODUCT_ID.toString())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_BRAND_ID_MESSAGE));
    }

    @Test
    @DisplayName("Given missing application date when getting applicable price then should return bad request")
    void givenMissingApplicationDateWhenGetApplicablePriceThenShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get(APPLICABLE_PRICE_PATH)
                        .param("productId", PRODUCT_ID.toString())
                        .param("brandId", BRAND_ID.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MISSING_APPLICATION_DATE_MESSAGE));
    }

    private static Stream<Arguments> applicablePriceCases() {
        return Stream.of(
                Arguments.of("2020-06-14T10:00:00Z", "2020-06-14T00:00:00", "2020-12-31T23:59:59", 1L, 35.5),
                Arguments.of("2020-06-14T16:00:00Z", "2020-06-14T15:00:00", "2020-06-14T18:30:00", 2L, 25.45),
                Arguments.of("2020-06-14T21:00:00Z", "2020-06-14T00:00:00", "2020-12-31T23:59:59", 1L, 35.5),
                Arguments.of("2020-06-15T10:00:00Z", "2020-06-15T00:00:00", "2020-06-15T11:00:00", 3L, 30.5),
                Arguments.of("2020-06-16T21:00:00Z", "2020-06-15T16:00:00", "2020-12-31T23:59:59", 4L, 38.95)
        );
    }

    private void stubApplicablePrice(OffsetDateTime dateTime,
                                     Price expectedPrice,
                                     PriceResponse expectedResponse) {
        given(priceServicePort.getApplicablePrice(PRODUCT_ID, BRAND_ID, LocalDateTime.from(dateTime)))
                .willReturn(CompletableFuture.completedFuture(expectedPrice));
        given(priceResponseMapper.toResponse(expectedPrice)).willReturn(expectedResponse);
    }

    private ResultActions performApplicablePriceRequest(String applicationDate,
                                                        String productId,
                                                        String brandId) throws Exception {
        var mvcResult = mockMvc.perform(buildApplicablePriceRequest(applicationDate, productId, brandId))
                .andExpect(request().asyncStarted())
                .andReturn();

        return mockMvc.perform(asyncDispatch(mvcResult));
    }

    private ResultActions performApplicablePriceRequestSync(String productId) throws Exception {
        return mockMvc.perform(buildApplicablePriceRequest("2020-06-14T10:00:00Z", productId, "a"));
    }

    private MockHttpServletRequestBuilder buildApplicablePriceRequest(String applicationDate,
                                                                      String productId,
                                                                      String brandId) {
        return get(APPLICABLE_PRICE_PATH)
                .param("applicationDate", applicationDate)
                .param("productId", productId)
                .param("brandId", brandId);
    }

    private Price buildPrice(String startDate, String endDate, Long priceList, double price) {
        return Price.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .startDate(LocalDateTime.parse(startDate))
                .endDate(LocalDateTime.parse(endDate))
                .priceList(priceList)
                .price(price)
                .currency(CURRENCY)
                .build();
    }

    private PriceResponse buildExpectedPriceResponse(Price price) {
        return PriceResponse.builder()
                .price(com.commerce.domain.model.Price.builder()
                        .productId(price.getProductId())
                        .brandId(price.getBrandId())
                        .priceList(price.getPriceList())
                        .price(price.getPrice())
                        .currency(price.getCurrency())
                        .startDate(OffsetDateTime.of(price.getStartDate(), ZoneOffset.UTC))
                        .endDate(OffsetDateTime.of(price.getEndDate(), ZoneOffset.UTC))
                        .build())
                .build();
    }
}