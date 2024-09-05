package com.commerce.driving.controllers.adapters;

import com.commerce.application.ports.driving.PriceServicePort;
import com.commerce.domain.api.PriceApi;
import com.commerce.domain.model.Brand;
import com.commerce.domain.model.Price;
import com.commerce.domain.model.PriceResponse;
import com.commerce.driving.controllers.mappers.PriceResponseMapper;
import com.commerce.driving.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The type Price controller adapter test.
 */
@WebMvcTest(PriceControllerAdapter.class)
@Import(GlobalExceptionHandler.class)
public class PriceControllerAdapterIntegrationTest {

    private final MockMvc mockMvc;

    @MockBean
    private PriceServicePort priceService;

    @MockBean
    private PriceResponseMapper priceResponseMapper;

    /**
     * Instantiates a new Price controller adapter test.
     *
     * @param mockMvc the mock mvc
     */
    @Autowired
    public PriceControllerAdapterIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    /**
     * Test get price at 10 h on 2020 06 14.
     *
     * @throws Exception the exception
     */
    @Test
    public void testGetPriceAt10hOn2020_06_14() throws Exception {

        var dateTime = OffsetDateTime.parse("2020-06-14T10:00:00Z");

        var response = PriceApi.builder()
                .productId(35455L)
                .brand(Brand.builder().brandId(1L).brandName("ZARA").build())
                .startDate(LocalDateTime.parse("2020-06-14T00:00:00"))
                .endDate(LocalDateTime.parse("2020-12-31T23:59:59"))
                .priceList(1L)
                .price(35.5)
                .currency("EUR")
                .build();

        var priceResponse = getPriceResponse(response);

        given(priceService.getApplicablePrice(35455L, 1L, LocalDateTime.from(dateTime))).willReturn(response);
        given(priceResponseMapper.mapListPriceApiToPriceResponse(response)).willReturn(priceResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prices/applicable")
                        .param("applicationDate", dateTime.toString())
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.price.price").value(response.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price.currency").value(response.getCurrency()));
    }

    /**
     * Test get price at 16 h on 2020 06 14.
     *
     * @throws Exception the exception
     */
    @Test
    public void testGetPriceAt16hOn2020_06_14() throws Exception {
        var dateTime = OffsetDateTime.parse("2020-06-14T16:00:00Z");

        var response = PriceApi.builder()
                .productId(35455L)
                .brand(Brand.builder().brandId(1L).brandName("ZARA").build())
                .startDate(LocalDateTime.parse("2020-06-14T15:00:00"))
                .endDate(LocalDateTime.parse("2020-06-14T18:30:00"))
                .priceList(2L)
                .price(25.45)
                .currency("EUR")
                .build();

        var priceResponse = getPriceResponse(response);

        given(priceService.getApplicablePrice(35455L, 1L, LocalDateTime.from(dateTime))).willReturn(response);
        given(priceResponseMapper.mapListPriceApiToPriceResponse(response)).willReturn(priceResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prices/applicable")
                        .param("applicationDate", dateTime.toString())
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.price.price").value(response.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price.currency").value(response.getCurrency()));
    }

    /**
     * Test get price at 21 h on 2020 06 14.
     *
     * @throws Exception the exception
     */
    @Test
    public void testGetPriceAt21hOn2020_06_14() throws Exception {
        var dateTime = OffsetDateTime.parse("2020-06-14T21:00:00Z");

        var response = PriceApi.builder()
                .productId(35455L)
                .brand(Brand.builder().brandId(1L).brandName("ZARA").build())
                .startDate(LocalDateTime.parse("2020-06-14T15:00:00"))
                .endDate(LocalDateTime.parse("2020-06-14T18:30:00"))
                .priceList(2L)
                .price(25.45)
                .currency("EUR")
                .build();

        var priceResponse = getPriceResponse(response);

        given(priceService.getApplicablePrice(35455L, 1L, LocalDateTime.from(dateTime))).willReturn(response);
        given(priceResponseMapper.mapListPriceApiToPriceResponse(response)).willReturn(priceResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prices/applicable")
                        .param("applicationDate", dateTime.toString())
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.price.price").value(response.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price.currency").value(response.getCurrency()));
    }

    /**
     * Test get price at 10 h on 2020 06 15.
     *
     * @throws Exception the exception
     */
    @Test
    public void testGetPriceAt10hOn2020_06_15() throws Exception {
        var dateTime = OffsetDateTime.parse("2020-06-15T10:00:00Z");

        var response = PriceApi.builder()
                .productId(35455L)
                .brand(Brand.builder().brandId(1L).brandName("ZARA").build())
                .startDate(LocalDateTime.parse("2020-06-15T00:00:00"))
                .endDate(LocalDateTime.parse("2020-06-15T11:00:00"))
                .priceList(3L)
                .price(30.5)
                .currency("EUR")
                .build();

        var priceResponse = getPriceResponse(response);

        given(priceService.getApplicablePrice(35455L, 1L, LocalDateTime.from(dateTime))).willReturn(response);
        given(priceResponseMapper.mapListPriceApiToPriceResponse(response)).willReturn(priceResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prices/applicable")
                        .param("applicationDate", dateTime.toString())
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.price.price").value(response.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price.currency").value(response.getCurrency()));
    }

    /**
     * Test get price at 21 h on 2020 06 16.
     *
     * @throws Exception the exception
     */
    @Test
    public void testGetPriceAt21hOn2020_06_16() throws Exception {
        var dateTime = OffsetDateTime.parse("2020-06-16T21:00:00Z");

        var response = PriceApi.builder()
                .productId(35455L)
                .brand(Brand.builder().brandId(1L).brandName("ZARA").build())
                .startDate(LocalDateTime.parse("2020-06-15T16:00:00"))
                .endDate(LocalDateTime.parse("2020-12-31T23:59:59"))
                .priceList(4L)
                .price(38.95)
                .currency("EUR")
                .build();

        var priceResponse = getPriceResponse(response);

        given(priceService.getApplicablePrice(35455L, 1L, LocalDateTime.from(dateTime))).willReturn(response);
        given(priceResponseMapper.mapListPriceApiToPriceResponse(response)).willReturn(priceResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/prices/applicable")
                        .param("applicationDate", dateTime.toString())
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.price.price").value(response.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price.currency").value(response.getCurrency()));
    }

    /**
     * Gets price response.
     *
     * @param priceApi the price api
     * @return the price response
     */
    public PriceResponse getPriceResponse(PriceApi priceApi) {

        var price = Price.builder()
                .productId(priceApi.getProductId())
                .priceList(priceApi.getPriceList())
                .startDate(OffsetDateTime.of(priceApi.getStartDate(), ZoneOffset.UTC))
                .endDate(OffsetDateTime.of(priceApi.getEndDate(), ZoneOffset.UTC))
                .price(priceApi.getPrice())
                .currency(priceApi.getCurrency())
                .brandId(priceApi.getBrand().getBrandId())
                .build();

        return PriceResponse.builder()
                .brand(Brand.builder()
                        .brandId(priceApi.getBrand().getBrandId())
                        .brandName(priceApi.getBrand().getBrandName())
                        .build())
                .price(price)
                .build();

    }

}
