package com.commerce.application.services;

import com.commerce.application.exceptions.PriceException;
import com.commerce.application.ports.driven.PriceRepositoryPort;
import com.commerce.application.ports.driving.PriceServicePort;
import com.commerce.domain.entities.Price;
import com.commerce.domain.model.PriceConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * The type Price service use case test.
 */
@ExtendWith(SpringExtension.class)
class PriceServiceUseCaseTest {
    private PriceServicePort priceServicePort;

    @Mock
    private PriceRepositoryPort priceRepositoryPort;

    /**
     * Before.
     */
    @BeforeEach
    public void before() {
        priceServicePort = new PriceServiceUseCase(priceRepositoryPort);
    }

    /**
     * Test get applicable price selects price with the highest priority.
     */
    @Test
    void testGetApplicablePrice_SelectsPriceWithHighestPriority() {
        var applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        var price1 = getPrice();
        var price2 = getPrice();

        price2.setStartDate(LocalDateTime.parse("2020-06-14T15:00:00"));
        price2.setEndDate(LocalDateTime.parse("2020-06-14T18:30:00"));
        price2.setPriceList(2L);
        price2.setPriority(1);
        price2.setPrice(25.45);

        when(priceRepositoryPort.getPrices(35455L, 1L, applicationDate))
                .thenReturn(List.of(price1, price2));

        var result = priceServicePort.getApplicablePrice(35455L, 1L, applicationDate);

        assertNotNull(result);
        assertEquals(price2, result);
    }

    /**
     * Test get applicable price selects only price.
     */
    @Test
    void testGetApplicablePrice_SelectsOnlyPrice() {
        var applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        var price = getPrice();

        when(priceRepositoryPort.getPrices(35455L, 1L, applicationDate))
                .thenReturn(List.of(price));

        var result = priceServicePort.getApplicablePrice(35455L, 1L, applicationDate);

        assertNotNull(result);
        assertEquals(price, result);
    }

    /**
     * Test get applicable price throws exception when no prices found.
     */
    @Test
    void testGetApplicablePrice_ThrowsExceptionWhenNoPricesFound() {
        var applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        when(priceRepositoryPort.getPrices(35455L, 1L, applicationDate))
                .thenReturn(List.of());

        var exception = assertThrows(PriceException.class, () -> {
            priceServicePort.getApplicablePrice(35455L, 1L, applicationDate);
        });

        assertEquals(PriceConstants.ERROR_NOT_FOUND, exception.getMessage());
    }

    private Price getPrice() {
        return Price.builder()
                .productId(35455L)
                .brandId(1L)
                .startDate(LocalDateTime.parse("2020-06-14T00:00:00"))
                .endDate(LocalDateTime.parse("2020-12-31T23:59:59"))
                .priceList(1L)
                .price(35.5)
                .currency("EUR")
                .priority(0)
                .build();
    }

}
