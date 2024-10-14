package com.commerce.application.services;

import com.commerce.common.enums.CommonErrorCodes;
import com.commerce.common.exceptions.FallbackPriceException;
import com.commerce.common.exceptions.PriceException;
import com.commerce.application.ports.driven.PriceRepositoryPort;
import com.commerce.domain.entities.Price;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * The type Price service use case test.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PriceServiceUseCaseTest {
    @Autowired
    private PriceServiceUseCase priceServiceUseCase;

    @MockBean
    private PriceRepositoryPort priceRepositoryPort;

    /**
     * Test get applicable price selects price with highest priority.
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
                .thenReturn(CompletableFuture.completedFuture(List.of(price1, price2)));

        var result = priceServiceUseCase.getApplicablePrice(35455L, 1L, applicationDate).join();

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
                .thenReturn(CompletableFuture.completedFuture(List.of(price)));

        var result = priceServiceUseCase.getApplicablePrice(35455L, 1L, applicationDate).join();

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
                .thenReturn(CompletableFuture.completedFuture(List.of()));

        var exception = assertThrows(CompletionException.class, () ->
            priceServiceUseCase.getApplicablePrice(35455L, 1L, applicationDate).join());

        Throwable cause = exception.getCause();
        assertTrue(cause instanceof PriceException);
        assertEquals(CommonErrorCodes.NOT_FOUND.getCode(), ((PriceException) cause).getErrorCode());
    }


    /**
     * Test circuit breaker fallback.
     */
    @Test
    void testCircuitBreakerFallback() {
        Long productId = 35455L;
        Long brandId = 1L;
        var applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        when(priceRepositoryPort.getPrices(anyLong(), anyLong(), any()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Database connection error")));

        var exception = assertThrows(CompletionException.class, () ->
            priceServiceUseCase.getApplicablePrice(productId, brandId, applicationDate).join());

        Throwable cause = exception.getCause();
        assertTrue(cause instanceof FallbackPriceException);
        assertEquals(CommonErrorCodes.FALLBACK.getCode(), ((FallbackPriceException) cause).getErrorCode());
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
