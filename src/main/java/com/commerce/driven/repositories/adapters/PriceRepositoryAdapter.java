package com.commerce.driven.repositories.adapters;

import com.commerce.application.exceptions.PriceException;
import com.commerce.application.ports.driven.PriceRepositoryPort;
import com.commerce.domain.api.PriceApi;
import com.commerce.domain.model.PriceConstants;
import com.commerce.driven.repositories.PriceMORepository;
import com.commerce.driven.repositories.mappers.PriceApiMapper;
import com.commerce.driven.repositories.models.PriceMO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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
                findApplicablePrices(brandId, productId, applicationDate);

        Optional<PriceMO> optionalPriceMO = pricesMO.stream().findFirst();

        return optionalPriceMO.map(priceApiMapper::mapPriceMOToPriceApi).orElseThrow(() ->
                new PriceException(PriceConstants.ERROR_NOT_FOUND, PriceConstants.ERROR_CODE_NOT_FOUND));

    }
}
