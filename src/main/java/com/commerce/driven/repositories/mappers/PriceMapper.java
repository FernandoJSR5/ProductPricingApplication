package com.commerce.driven.repositories.mappers;

import com.commerce.domain.entities.Price;
import com.commerce.driven.repositories.models.PriceMO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PriceMapper {
    @Mapping(source = "brand.id", target = "brandId")
    Price mapPriceMOToPrice(PriceMO priceMO);
}
