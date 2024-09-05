package com.commerce.driven.repositories.adapters;

import com.commerce.application.exceptions.PriceException;
import com.commerce.domain.api.PriceApi;
import com.commerce.domain.model.Brand;
import com.commerce.domain.model.PriceConstants;
import com.commerce.driven.repositories.PriceMORepository;
import com.commerce.driven.repositories.mappers.PriceApiMapper;
import com.commerce.driven.repositories.models.BrandMO;
import com.commerce.driven.repositories.models.PriceMO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The type Price repository adapter test.
 */
@ExtendWith(SpringExtension.class)
public class PriceRepositoryAdapterUnitTest {

    private PriceRepositoryAdapter priceRepositoryAdapter;

    @Mock
    private PriceMORepository priceMORepository;

    @Mock
    private PriceApiMapper priceApiMapper;

    private LocalDateTime applicationDate;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        priceRepositoryAdapter = new PriceRepositoryAdapter(priceMORepository, priceApiMapper);
        applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
    }

    /**
     * Test get price when price exists.
     */
    @Test
    public void testGetPriceWhenPriceExists() {

        var priceMO = PriceMO.builder()
                .productId(35455L)
                .brand(BrandMO.builder().id(1L).brandName("ZARA").build())
                .startDate(LocalDateTime.parse("2020-06-14T00:00:00"))
                .endDate(LocalDateTime.parse("2020-12-31T23:59:59"))
                .priceList(1L)
                .price(35.5)
                .currency("EUR")
                .build();

        var priceApi = PriceApi.builder()
                .productId(35455L)
                .brand(Brand.builder().brandId(1L).brandName("ZARA").build())
                .startDate(LocalDateTime.parse("2020-06-14T00:00:00"))
                .endDate(LocalDateTime.parse("2020-12-31T23:59:59"))
                .priceList(1L)
                .price(35.5)
                .currency("EUR")
                .build();

        when(priceMORepository.findApplicablePrices(1L, 35455L, applicationDate))
                .thenReturn(List.of(priceMO));
        when(priceApiMapper.mapPriceMOToPriceApi(priceMO))
                .thenReturn(priceApi);

        var result = priceRepositoryAdapter.getPrice(35455L, 1L, applicationDate);

        assertNotNull(result);
        assertEquals(priceApi, result);

        verify(priceMORepository, times(1)).findApplicablePrices(1L, 35455L, applicationDate);
        verify(priceApiMapper, times(1)).mapPriceMOToPriceApi(priceMO);
    }

    /**
     * Test get price when price does not exist.
     */
    @Test
    public void testGetPriceWhenPriceDoesNotExist() {

        when(priceMORepository.findApplicablePrices(1L, 35455L, applicationDate))
                .thenReturn(Collections.emptyList());

        PriceException exception = assertThrows(PriceException.class, () -> {
            priceRepositoryAdapter.getPrice(35455L, 1L, applicationDate);
        });

        assertEquals(PriceConstants.ERROR_NOT_FOUND, exception.getMessage());

        verify(priceMORepository, times(1)).findApplicablePrices(1L, 35455L, applicationDate);
        verify(priceApiMapper, never()).mapPriceMOToPriceApi(any());
    }
}
