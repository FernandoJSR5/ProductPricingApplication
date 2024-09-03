package com.commerce.domain.api;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@Builder
public class PriceApi {

    private Integer brandId;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private Integer priceList;
    private Integer productId;
    private Integer priority;
    private Double price;
    private String currency;

}
