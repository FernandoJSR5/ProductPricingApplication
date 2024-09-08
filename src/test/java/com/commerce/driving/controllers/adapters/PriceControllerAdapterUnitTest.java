package com.commerce.driving.controllers.adapters;

import com.commerce.application.ports.driving.PriceServicePort;
import com.commerce.domain.entities.Price;
import com.commerce.domain.model.PriceResponse;
import com.commerce.driving.controllers.mappers.PriceResponseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * The type Price controller adapter unit test.
 */
@ExtendWith(SpringExtension.class)
class PriceControllerAdapterUnitTest {

    private PriceControllerAdapter priceControllerAdapter;

    @Mock
    private PriceServicePort priceServicePort;

    @Mock
    private PriceResponseMapper priceResponseMapper;


    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        priceControllerAdapter = new PriceControllerAdapter(priceServicePort, priceResponseMapper);
    }

    /**
     * Test get applicable price.
     */
    @Test
    void testGetApplicablePrice() {
        OffsetDateTime applicationDate = OffsetDateTime.parse("2020-06-14T10:00:00Z");
        Long productId = 35455L;
        Long brandId = 1L;

        Price price = Price.builder()
                .productId(productId)
                .brandId(brandId)
                .startDate(LocalDateTime.parse("2020-06-14T00:00:00"))
                .endDate(LocalDateTime.parse("2020-12-31T23:59:59"))
                .priceList(1L)
                .price(35.50)
                .currency("EUR")
                .build();

        PriceResponse priceResponse = PriceResponse.builder()
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

        when(priceServicePort.getApplicablePrice(productId, brandId, applicationDate.toLocalDateTime())).thenReturn(price);
        when(priceResponseMapper.mapListPriceToPriceResponse(price)).thenReturn(priceResponse);

        ResponseEntity<PriceResponse> responseEntity = priceControllerAdapter.getApplicablePrice(applicationDate, productId, brandId);

        assertEquals(priceResponse, responseEntity.getBody());
    }
}
