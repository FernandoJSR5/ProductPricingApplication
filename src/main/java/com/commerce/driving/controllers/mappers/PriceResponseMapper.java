package com.commerce.driving.controllers.mappers;

import com.commerce.domain.api.PriceApi;
import com.commerce.domain.model.Brand;
import com.commerce.domain.model.Price;
import com.commerce.domain.model.PriceResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

@Component
public class PriceResponseMapper {

    public PriceResponse mapListPriceApiToPriceResponse(PriceApi priceApi) {
        var price = Price.builder()
                .priceList(priceApi.getPriceList())
                .startDate(priceApi.getStartDate())
                .endDate(priceApi.getEndDate())
                .price(priceApi.getPrice())
                .currency(priceApi.getCurrency())
                .priority(priceApi.getPriority())
                .brandId(priceApi.getBrandId())
                .build();

        var brand = getBrandById(priceApi.getBrandId());

        return PriceResponse.builder()
                .brand(brand)
                .price(price)
                .build();

    }

    private Brand getBrandById(Integer brandId) {
        return Brand.builder()
                .brandId(brandId)
                .brandName(getBrandNameById(brandId))
                .build();
    }

    private String getBrandNameById(Integer brandId) {
        switch (brandId) {
            case 1: return "ZARA";
            default: return "ZARA HOME";
        }
    }
}
