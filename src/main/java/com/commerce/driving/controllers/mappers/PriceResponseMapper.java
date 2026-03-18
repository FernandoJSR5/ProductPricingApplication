package com.commerce.driving.controllers.mappers;

import com.commerce.domain.entities.Price;
import com.commerce.domain.model.PriceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface PriceResponseMapper {

    @Mapping(target = "price", source = ".")
    PriceResponse toResponse(Price price);

    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "toOffsetDateTimeUtc")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "toOffsetDateTimeUtc")
    com.commerce.domain.model.Price toApiPrice(Price price);

    @Named("toOffsetDateTimeUtc")
    default OffsetDateTime toOffsetDateTimeUtc(LocalDateTime localDateTime) {
        return localDateTime == null ? null : OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
    }
}