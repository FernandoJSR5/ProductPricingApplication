package com.commerce.driving.controllers.adapters;

import com.commerce.common.enums.CommonErrorCodes;
import com.commerce.common.exceptions.PriceException;
import com.commerce.application.ports.driving.PriceServicePort;
import com.commerce.domain.entities.Price;
import com.commerce.domain.model.PriceResponse;
import com.commerce.driving.controllers.mappers.PriceResponseMapper;
import com.commerce.driving.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * The type Price controller adapter integration test.
 */
@WebMvcTest(PriceControllerAdapter.class)
@Import(GlobalExceptionHandler.class)
class PriceControllerAdapterIntegrationTest {

    private final MockMvc mockMvc;

    @MockBean
    private PriceServicePort priceService;

    @MockBean
    private PriceResponseMapper priceResponseMapper;

    /**
     * Instantiates a new Price controller adapter integration test.
     *
     * @param mockMvc the mock mvc
     */
    @Autowired
    public PriceControllerAdapterIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    private void performTest(OffsetDateTime dateTime, Price expectedPrice) throws Exception {
        Long productId = 35455L;
        Long brandId = 1L;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        var expectedPriceResponse = getPriceResponse(expectedPrice);

        given(priceService.getApplicablePrice(35455L, 1L, LocalDateTime.from(dateTime)))
                .willReturn(CompletableFuture.completedFuture(expectedPrice));
        given(priceResponseMapper.mapListPriceToPriceResponse(expectedPrice)).willReturn(expectedPriceResponse);

        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prices/applicable")
                        .param("applicationDate", dateTime.toString())
                        .param("productId", productId.toString())
                        .param("brandId", brandId.toString()))
                .andExpect(status().isOk())
                .andReturn();

        var asyncResult = result.getAsyncResult();
        assertTrue(asyncResult instanceof ResponseEntity<?>);

        var responseEntity = (ResponseEntity<?>) asyncResult;
        var responseBody = responseEntity.getBody();

        assertTrue(responseBody instanceof PriceResponse);
        var priceResponse = (PriceResponse) responseBody;

        var price = priceResponse.getPrice();
        assertNotNull(price);
        assertEquals(expectedPrice.getProductId(), price.getProductId());
        assertEquals(expectedPrice.getPrice(), price.getPrice());
        assertEquals(expectedPrice.getCurrency(), price.getCurrency());
        assertEquals(expectedPrice.getPriceList(), price.getPriceList());
        assertEquals(expectedPrice.getBrandId(), price.getBrandId());
        assertEquals(expectedPrice.getStartDate().format(formatter), price.getStartDate().format(formatter));
        assertEquals(expectedPrice.getEndDate().format(formatter), price.getEndDate().format(formatter));
    }

    /**
     * Test get price at 10 h on 2020 06 14.
     *
     * @throws Exception the exception
     */
    @Test
    void testGetPriceAt10hOn2020_06_14() throws Exception {
        var dateTime = OffsetDateTime.parse("2020-06-14T10:00:00Z");
        var expectedPrice = createPrice("2020-06-14T00:00:00",
                "2020-12-31T23:59:59", 1L, 35.5);

        performTest(dateTime, expectedPrice);
    }

    /**
     * Test get price at 16 h on 2020 06 14.
     *
     * @throws Exception the exception
     */
    @Test
    void testGetPriceAt16hOn2020_06_14() throws Exception {
        var dateTime = OffsetDateTime.parse("2020-06-14T16:00:00Z");
        var expectedPrice = createPrice("2020-06-14T15:00:00",
                "2020-06-14T18:30:00", 2L, 25.45);

        performTest(dateTime, expectedPrice);
    }

    /**
     * Test get price at 21 h on 2020 06 14.
     *
     * @throws Exception the exception
     */
    @Test
    void testGetPriceAt21hOn2020_06_14() throws Exception {
        var dateTime = OffsetDateTime.parse("2020-06-14T21:00:00Z");
        var expectedPrice = createPrice("2020-06-14T15:00:00",
                "2020-06-14T18:30:00", 2L, 25.45);

        performTest(dateTime, expectedPrice);
    }

    /**
     * Test get price at 10 h on 2020 06 15.
     *
     * @throws Exception the exception
     */
    @Test
    void testGetPriceAt10hOn2020_06_15() throws Exception {
        var dateTime = OffsetDateTime.parse("2020-06-15T10:00:00Z");
        var expectedPrice = createPrice("2020-06-15T00:00:00",
                "2020-06-15T11:00:00", 3L, 30.5);

        performTest(dateTime, expectedPrice);
    }

    /**
     * Test get price at 21 h on 2020 06 16.
     *
     * @throws Exception the exception
     */
    @Test
    void testGetPriceAt21hOn2020_06_16() throws Exception {
        var dateTime = OffsetDateTime.parse("2020-06-16T21:00:00Z");
        var expectedPrice = createPrice("2020-06-15T16:00:00",
                "2020-12-31T23:59:59", 4L, 38.95);

        performTest(dateTime, expectedPrice);
    }

    /**
     * Test get price not found.
     *
     * @throws Exception the exception
     */
    @Test
    void testGetPriceNotFound() throws Exception {
        var dateTime = OffsetDateTime.parse("2020-06-14T10:00:00Z");

        given(priceService.getApplicablePrice(35455L, 3L, LocalDateTime.from(dateTime)))
                .willThrow(new PriceException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prices/applicable")
                        .param("applicationDate", dateTime.toString())
                        .param("productId", "35455")
                        .param("brandId", "3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(CommonErrorCodes.NOT_FOUND.getMessage()));
    }

    /**
     * Test get price bad request due to invalid parameter.
     *
     * @throws Exception the exception
     */
    @Test
    void testGetPriceBadRequestDueToInvalidParameter() throws Exception {
        var dateTime = OffsetDateTime.parse("2020-06-14T10:00:00Z");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prices/applicable")
                        .param("applicationDate", dateTime.toString())
                        .param("productId", "35455")
                        .param("brandId", "a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Parameter 'brandId' should be of type 'Long'"));
    }

    /**
     * Test get price bad request due to missing parameter.
     *
     * @throws Exception the exception
     */
    @Test
    void testGetPriceBadRequestDueToMissingParameter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prices/applicable")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Missing required parameter: 'applicationDate'"));
    }

    private Price createPrice(String startDate, String endDate, Long priceList,
                              double price) {
        return Price.builder()
                .productId(35455L)
                .brandId(1L)
                .startDate(LocalDateTime.parse(startDate))
                .endDate(LocalDateTime.parse(endDate))
                .priceList(priceList)
                .price(price)
                .currency("EUR")
                .build();
    }

    private PriceResponse getPriceResponse(Price price) {

        return PriceResponse.builder()
                .price(com.commerce.domain.model.Price.builder()
                        .productId(price.getProductId())
                        .priceList(price.getPriceList())
                        .startDate(OffsetDateTime.of(price.getStartDate(), ZoneOffset.UTC))
                        .endDate(OffsetDateTime.of(price.getEndDate(), ZoneOffset.UTC))
                        .price(price.getPrice())
                        .currency(price.getCurrency())
                        .brandId(price.getBrandId())
                        .build())
                .build();

    }

}
