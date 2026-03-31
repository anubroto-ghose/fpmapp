package com.fpm.service;

import com.fpm.dto.CurrencyOverrideDTO;

import java.util.List;

public interface CurrencyOverrideService {

    // STORY: FPMAPP-9049 - Retrieve all currency overrides (current and historical)
    List<CurrencyOverrideDTO> getAllOverrides();

    // STORY: FPMAPP-9049 - Create a new currency override with audit logging and alerting
    CurrencyOverrideDTO createOverride(CurrencyOverrideDTO overrideDTO, String createdBy);

    // STORY: FPMAPP-9049 - Retrieve a currency override by its ID
    CurrencyOverrideDTO getOverrideById(Long id);

}
