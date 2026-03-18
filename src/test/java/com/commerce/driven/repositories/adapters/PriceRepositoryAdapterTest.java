package com.commerce.driven.repositories.adapters;

import com.commerce.common.enums.CommonErrorCodes;
import com.commerce.common.exceptions.FallbackPriceException;
import com.commerce.domain.entities.Price;
import com.commerce.driven.repositories.PriceMORepository;
import com.commerce.driven.repositories.mappers.PriceMapper;
import com.commerce.driven.repositories.models.BrandMO;
import com.commerce.driven.repositories.models.PriceMO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PriceRepositoryAdapterTest {

    private static final Long PRODUCT_ID = 35455L;
    private static final Long BRAND_ID = 1L;
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2020, 6, 14, 10, 0);
    private static final LocalDateTime START_DATE = LocalDateTime.of(2020, 6, 14, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2020, 12, 31, 23, 59, 59);
    private static final Long PRICE_LIST = 1L;
    private static final double PRICE_VALUE = 35.5;
    private static final int PRIORITY = 0;
    private static final String CURRENCY = "EUR";
    private static final String BRAND_NAME = "ZARA";
    private static final String REPOSITORY_ERROR_MESSAGE = "Database connection error";

    @Autowired
    private PriceRepositoryAdapter priceRepositoryAdapter;

    @MockBean
    private PriceMORepository priceMORepository;

    @MockBean
    private PriceMapper priceMapper;

    @Test
    @DisplayName("Given repository failure when getting prices then should retry three times and throw fallback price exception")
    void givenRepositoryFailureWhenGettingPricesThenShouldRetryThreeTimesAndThrowFallbackPriceException() {
        when(priceMORepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyLong(), anyLong(), any(), any()))
                .thenThrow(new RuntimeException(REPOSITORY_ERROR_MESSAGE));

        var exception = assertThrows(
                FallbackPriceException.class,
                () -> priceRepositoryAdapter.getPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE)
        );

        verify(priceMORepository, times(3))
                .findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        BRAND_ID, PRODUCT_ID, APPLICATION_DATE, APPLICATION_DATE
                );

        assertEquals(CommonErrorCodes.FALLBACK.getCode(), exception.getErrorCode());
    }

    @Test
    @DisplayName("Given prices found in repository when getting prices then should return mapped prices")
    void givenPricesFoundInRepositoryWhenGettingPricesThenShouldReturnMappedPrices() {
        var priceMO = buildPriceMO();
        var expectedPrice = buildPrice();

        when(priceMORepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                BRAND_ID, PRODUCT_ID, APPLICATION_DATE, APPLICATION_DATE))
                .thenReturn(List.of(priceMO));
        when(priceMapper.mapPriceMOToPrice(priceMO)).thenReturn(expectedPrice);

        var result = priceRepositoryAdapter.getPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE).join();

        assertEquals(1, result.size());
        assertEquals(expectedPrice, result.get(0));

        verify(priceMORepository, times(1))
                .findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        BRAND_ID, PRODUCT_ID, APPLICATION_DATE, APPLICATION_DATE
                );
        verify(priceMapper, times(1)).mapPriceMOToPrice(priceMO);
    }

    @Test
    @DisplayName("Given cached prices when getting prices twice then should query repository only once")
    void givenCachedPricesWhenGettingPricesTwiceThenShouldQueryRepositoryOnlyOnce() {
        var priceMO = buildPriceMO();
        var expectedPrice = buildPrice();

        when(priceMORepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                BRAND_ID, PRODUCT_ID, APPLICATION_DATE, APPLICATION_DATE))
                .thenReturn(List.of(priceMO));
        when(priceMapper.mapPriceMOToPrice(priceMO)).thenReturn(expectedPrice);

        var firstResult = priceRepositoryAdapter.getPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE).join();
        var secondResult = priceRepositoryAdapter.getPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE).join();

        assertEquals(1, firstResult.size());
        assertEquals(expectedPrice, firstResult.get(0));
        assertEquals(1, secondResult.size());
        assertEquals(expectedPrice, secondResult.get(0));

        verify(priceMORepository, times(1))
                .findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        BRAND_ID, PRODUCT_ID, APPLICATION_DATE, APPLICATION_DATE
                );
        verify(priceMapper, times(1)).mapPriceMOToPrice(priceMO);
    }

    @Test
    @DisplayName("Given repository returns empty list when getting prices then should return empty list")
    void givenRepositoryReturnsEmptyListWhenGettingPricesThenShouldReturnEmptyList() {
        when(priceMORepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                BRAND_ID, PRODUCT_ID, APPLICATION_DATE, APPLICATION_DATE))
                .thenReturn(List.of());

        var result = priceRepositoryAdapter.getPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE).join();

        assertTrue(result.isEmpty());

        verify(priceMORepository, times(1))
                .findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        BRAND_ID, PRODUCT_ID, APPLICATION_DATE, APPLICATION_DATE
                );
        verify(priceMapper, never()).mapPriceMOToPrice(any());
    }

    private PriceMO buildPriceMO() {
        return PriceMO.builder()
                .productId(PRODUCT_ID)
                .brand(BrandMO.builder()
                        .id(BRAND_ID)
                        .brandName(BRAND_NAME)
                        .build())
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(PRICE_LIST)
                .price(PRICE_VALUE)
                .priority(PRIORITY)
                .currency(CURRENCY)
                .build();
    }

    private Price buildPrice() {
        return Price.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(PRICE_LIST)
                .price(PRICE_VALUE)
                .priority(PRIORITY)
                .currency(CURRENCY)
                .build();
    }
}