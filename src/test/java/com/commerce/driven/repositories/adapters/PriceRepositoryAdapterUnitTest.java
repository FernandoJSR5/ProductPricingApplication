package com.commerce.driven.repositories.adapters;

import com.commerce.domain.entities.Price;
import com.commerce.driven.repositories.PriceMORepository;
import com.commerce.driven.repositories.mappers.PriceMapper;
import com.commerce.driven.repositories.models.BrandMO;
import com.commerce.driven.repositories.models.PriceMO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The type Price repository adapter unit test.
 */
@ExtendWith(SpringExtension.class)
class PriceRepositoryAdapterUnitTest {

    private PriceRepositoryAdapter priceRepositoryAdapter;

    @Mock
    private PriceMORepository priceMORepository;

    @Mock
    private PriceMapper priceMapper;

    private LocalDateTime applicationDate;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        priceRepositoryAdapter = new PriceRepositoryAdapter(priceMORepository, priceMapper);
        applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
    }

    /**
     * Test get prices from repository returns mapped prices.
     */
    @Test
    void testGetPricesFromRepositoryReturnsMappedPrices() {

        var priceMO = PriceMO.builder()
                .productId(35455L)
                .brand(BrandMO.builder().id(1L).brandName("ZARA").build())
                .startDate(LocalDateTime.parse("2020-06-14T00:00:00"))
                .endDate(LocalDateTime.parse("2020-12-31T23:59:59"))
                .priceList(1L)
                .price(35.5)
                .priority(0)
                .currency("EUR")
                .build();

        var price = Price.builder()
                .productId(35455L)
                .brandId(1L)
                .startDate(LocalDateTime.parse("2020-06-14T00:00:00"))
                .endDate(LocalDateTime.parse("2020-12-31T23:59:59"))
                .priceList(1L)
                .price(35.5)
                .priority(0)
                .currency("EUR")
                .build();

        when(priceMORepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                1L, 35455L, applicationDate, applicationDate))
                .thenReturn(List.of(priceMO));
        when(priceMapper.mapPriceMOToPrice(priceMO))
                .thenReturn(price);

        var result = priceRepositoryAdapter.getPrices(35455L, 1L, applicationDate);

        assertEquals(1, result.size());
        assertEquals(price, result.get(0));
        verify(priceMORepository, times(1)).
                findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        1L, 35455L, applicationDate, applicationDate);
        verify(priceMapper, times(1)).mapPriceMOToPrice(priceMO);
    }
}
