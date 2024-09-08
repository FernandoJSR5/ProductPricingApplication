package com.commerce.driven.repositories;

import com.commerce.driven.repositories.models.PriceMO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The interface Price mo repository.
 */
public interface PriceMORepository extends JpaRepository<PriceMO, Long> {

    /**
     * Find by brand id and product id and start date less than equal and end date greater than equal list.
     *
     * @param brandId   the brand id
     * @param productId the product id
     * @param startDate the start date
     * @param endDate   the end date
     * @return the list
     */
    List<PriceMO> findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            @Param("brandId") Long brandId,
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find applicable prices list.
     *
     * @param brandId         the brand id
     * @param productId       the product id
     * @param applicationDate the application date
     * @return the list
     */
    default List<PriceMO> findApplicablePrices(
            Long brandId,
            Long productId,
            LocalDateTime applicationDate) {

        return findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                brandId,
                productId,
                applicationDate,
                applicationDate);
    }
}
