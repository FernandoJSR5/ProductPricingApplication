package com.commerce.application.services;

import com.commerce.application.ports.driven.PriceRepositoryPort;
import com.commerce.application.ports.driving.PriceServicePort;
import com.commerce.domain.api.PriceApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * The type Price service use case.
 */
@Slf4j
@Service
@AllArgsConstructor
public class PriceServiceUseCase implements PriceServicePort {

    private final PriceRepositoryPort priceRepositoryPort;

    @Override
    public PriceApi getApplicablePrice(Long productId, Long brandId, LocalDateTime applicationDate) {
        return priceRepositoryPort.getPrice(productId, brandId, applicationDate);
    }
}
