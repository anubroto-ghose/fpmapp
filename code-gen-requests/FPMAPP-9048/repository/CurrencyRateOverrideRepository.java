package com.fpm.repository;

import com.fpm.model.CurrencyRateOverride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRateOverrideRepository extends JpaRepository<CurrencyRateOverride, Long> {

    // STORY: FPMAPP-9048 - Repository for admin override records

}
