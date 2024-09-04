package com.commerce.driven.repositories;

import com.commerce.driven.repositories.models.PriceMO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceMORepository extends JpaRepository<PriceMO, Long> {

    List<PriceMO> findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
            @Param("brandId") Long brandId,
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    default List<PriceMO> findApplicablePrices(
            Long brandId,
            Long productId,
            LocalDateTime applicationDate) {

        return findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                brandId,
                productId,
                applicationDate,
                applicationDate);
    }
}
