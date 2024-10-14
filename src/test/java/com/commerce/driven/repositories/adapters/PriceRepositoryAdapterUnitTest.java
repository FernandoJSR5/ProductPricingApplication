package com.commerce.driven.repositories.adapters;

import com.commerce.domain.entities.Price;
import com.commerce.driven.repositories.PriceMORepository;
import com.commerce.driven.repositories.mappers.PriceMapper;
import com.commerce.driven.repositories.models.BrandMO;
import com.commerce.driven.repositories.models.PriceMO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The type Price repository adapter unit test.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PriceRepositoryAdapterUnitTest {

    private static final Long PRODUCT_ID = 35455L;
    private static final Long BRAND_ID = 1L;
    private static final LocalDateTime START_DATE = LocalDateTime.parse("2020-06-14T00:00:00");
    private static final LocalDateTime END_DATE = LocalDateTime.parse("2020-12-31T23:59:59");
    private static final double PRICE_VALUE = 35.5;

    /**
     * The Price repository adapter.
     */
    @Autowired
    PriceRepositoryAdapter priceRepositoryAdapter;

    @MockBean
    private PriceMORepository priceMORepository;

    @MockBean
    private PriceMapper priceMapper;

    private LocalDateTime applicationDate;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
    }

    /**
     * Test retry mechanism.
     */
    @Test
    void testRetryMechanism() {
        Long productId = 35455L;
        Long brandId = 1L;
        var applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        when(priceMORepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyLong(), anyLong(), any(), any()))
                .thenThrow(new RuntimeException("Database connection error"));

        var exception = assertThrows(CompletionException.class, () ->
            priceRepositoryAdapter.getPrices(productId, brandId, applicationDate).join());

        verify(priceMORepository, times(3)).findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                brandId, productId, applicationDate, applicationDate);

        Throwable cause = exception.getCause();
        assertTrue(cause instanceof RuntimeException);
    }

    /**
     * Test get prices from repository returns mapped prices.
     */
    @Test
    void testGetPricesFromRepositoryReturnsMappedPrices() {

        var priceMO = createPriceMO();

        var expectedPrice = createExpectedPrice();

        when(priceMORepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                1L, 35455L, applicationDate, applicationDate))
                .thenReturn(List.of(priceMO));
        when(priceMapper.mapPriceMOToPrice(priceMO))
                .thenReturn(expectedPrice);

        var result = priceRepositoryAdapter.getPrices(35455L, 1L, applicationDate).join();

        assertEquals(1, result.size());
        assertEquals(expectedPrice, result.get(0));
        verify(priceMORepository, times(1)).
                findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        1L, 35455L, applicationDate, applicationDate);
        verify(priceMapper, times(1)).mapPriceMOToPrice(priceMO);
    }

    /**
     * Test cache stores prices.
     */
    @Test
    void testCacheStoresPrices() {
        Long productId = 35455L;
        Long brandId = 1L;
        var applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        var priceMO = createPriceMO();

        var expectedPrice = createExpectedPrice();

        when(priceMORepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                brandId, productId, applicationDate, applicationDate))
                .thenReturn(List.of(priceMO));
        when(priceMapper.mapPriceMOToPrice(priceMO))
                .thenReturn(expectedPrice);

        var result = priceRepositoryAdapter.getPrices(productId, brandId, applicationDate).join();
        assertEquals(1, result.size());
        assertEquals(expectedPrice, result.get(0));

        verify(priceMORepository, times(1)).findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                brandId, productId, applicationDate, applicationDate);

        when(priceMORepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                brandId, productId, applicationDate, applicationDate))
                .thenReturn(List.of(priceMO));

        var cachedResult = priceRepositoryAdapter.getPrices(productId, brandId, applicationDate).join();
        assertEquals(1, cachedResult.size());
        assertEquals(expectedPrice, cachedResult.get(0));

        verify(priceMORepository, times(1)).findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                brandId, productId, applicationDate, applicationDate);
    }

    private PriceMO createPriceMO() {
        return PriceMO.builder()
                .productId(PRODUCT_ID)
                .brand(BrandMO.builder().id(BRAND_ID).brandName("ZARA").build())
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(1L)
                .price(PRICE_VALUE)
                .priority(0)
                .currency("EUR")
                .build();
    }

    private Price createExpectedPrice() {
        return Price.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(1L)
                .price(PRICE_VALUE)
                .priority(0)
                .currency("EUR")
                .build();
    }
}
