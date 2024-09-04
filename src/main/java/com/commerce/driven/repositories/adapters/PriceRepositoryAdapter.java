package com.commerce.driven.repositories.adapters;

import com.commerce.application.ports.driven.PriceRepositoryPort;
import com.commerce.domain.api.PriceApi;
import com.commerce.driven.repositories.PriceMORepository;
import com.commerce.driven.repositories.mappers.PriceApiMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;

/**
 * The type Price repository adapter.
 */
@Slf4j
@Service
@AllArgsConstructor
public class PriceRepositoryAdapter implements PriceRepositoryPort {

    private final PriceMORepository priceMORepository;
    private final PriceApiMapper priceApiMapper;

    @Override
    public PriceApi getPrice(Long productId, Long brandId, LocalDateTime applicationDate) {
        var pricesMO = priceMORepository.
                findApplicablePrices(
                        brandId, productId, applicationDate);
        if(CollectionUtils.isEmpty(pricesMO)) {
            return PriceApi.empty();
        }
        return priceApiMapper.mapPriceMOToPriceApi(pricesMO.get(0));
    }
}
