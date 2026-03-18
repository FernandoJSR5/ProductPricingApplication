package com.commerce.application.services;

import com.commerce.application.ports.driven.PriceRepositoryPort;
import com.commerce.common.enums.CommonErrorCodes;
import com.commerce.common.exceptions.PriceException;
import com.commerce.domain.entities.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceUseCaseTest {

    private static final Long PRODUCT_ID = 35455L;
    private static final Long BRAND_ID = 1L;
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2020, 6, 14, 10, 0);
    private static final LocalDateTime START_DATE = LocalDateTime.parse("2020-06-14T00:00:00");
    private static final LocalDateTime END_DATE = LocalDateTime.parse("2020-12-31T23:59:59");
    private static final Long PRICE_LIST = 1L;
    private static final double PRICE_VALUE = 35.5;
    private static final int PRIORITY = 0;
    private static final String CURRENCY = "EUR";

    @Mock
    private PriceRepositoryPort priceRepositoryPort;

    @InjectMocks
    private PriceServiceUseCase priceServiceUseCase;

    @Test
    @DisplayName("Given multiple prices when getting applicable price then should return price with highest priority")
    void givenMultiplePricesWhenGetApplicablePriceThenShouldReturnPriceWithHighestPriority() {
        var lowerPriorityPrice = buildPrice();
        var higherPriorityPrice = buildPrice();

        higherPriorityPrice.setStartDate(LocalDateTime.parse("2020-06-14T15:00:00"));
        higherPriorityPrice.setEndDate(LocalDateTime.parse("2020-06-14T18:30:00"));
        higherPriorityPrice.setPriceList(2L);
        higherPriorityPrice.setPriority(1);
        higherPriorityPrice.setPrice(25.45);

        when(priceRepositoryPort.getPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE))
                .thenReturn(CompletableFuture.completedFuture(List.of(lowerPriorityPrice, higherPriorityPrice)));

        var result = priceServiceUseCase.getApplicablePrice(PRODUCT_ID, BRAND_ID, APPLICATION_DATE).join();

        assertNotNull(result);
        assertEquals(higherPriorityPrice, result);
        verify(priceRepositoryPort, times(1)).getPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("Given single price when getting applicable price then should return that price")
    void givenSinglePriceWhenGetApplicablePriceThenShouldReturnThatPrice() {
        var price = buildPrice();

        when(priceRepositoryPort.getPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE))
                .thenReturn(CompletableFuture.completedFuture(List.of(price)));

        var result = priceServiceUseCase.getApplicablePrice(PRODUCT_ID, BRAND_ID, APPLICATION_DATE).join();

        assertNotNull(result);
        assertEquals(price, result);
        verify(priceRepositoryPort, times(1)).getPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }

    @Test
    @DisplayName("Given no prices when getting applicable price then should throw price exception")
    void givenNoPricesWhenGetApplicablePriceThenShouldThrowPriceException() {
        when(priceRepositoryPort.getPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE))
                .thenReturn(CompletableFuture.completedFuture(List.of()));

        var exception = assertThrows(CompletionException.class,
                () -> priceServiceUseCase.getApplicablePrice(PRODUCT_ID, BRAND_ID, APPLICATION_DATE).join());

        assertTrue(exception.getCause() instanceof PriceException);
        assertEquals(CommonErrorCodes.NOT_FOUND.getCode(),
                ((PriceException) exception.getCause()).getErrorCode());
        verify(priceRepositoryPort, times(1)).getPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }

    private Price buildPrice() {
        return Price.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(PRICE_LIST)
                .price(PRICE_VALUE)
                .currency(CURRENCY)
                .priority(PRIORITY)
                .build();
    }
}