package com.commerce.driven.repositories.adapters;

import com.commerce.application.ports.driven.PriceRepositoryPort;
import com.commerce.domain.entities.Price;
import com.commerce.driven.repositories.PriceMORepository;
import com.commerce.driven.repositories.mappers.PriceMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * The type Price repository adapter.
 */
@Slf4j
@Service
@AllArgsConstructor
public class PriceRepositoryAdapter implements PriceRepositoryPort {

    private final PriceMORepository priceMORepository;
    private final PriceMapper priceMapper;

    @Override
    public List<Price> getPrices(Long productId, Long brandId, LocalDateTime applicationDate) {
        var pricesMO = priceMORepository.
                findApplicablePrices(brandId, productId, applicationDate);

        return pricesMO.stream().map(priceMapper::mapPriceMOToPrice).collect(toList());

    }
}
