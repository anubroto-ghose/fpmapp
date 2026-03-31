package com.fpm.service.impl;

import com.fpm.dto.CurrencyOverrideDTO;
import com.fpm.model.CurrencyOverride;
import com.fpm.repository.CurrencyOverrideRepository;
import com.fpm.service.CurrencyOverrideService;
import com.fpm.util.AlertUtil;
import com.fpm.util.AuditLogUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyOverrideServiceImpl implements CurrencyOverrideService {

    @Autowired
    private CurrencyOverrideRepository currencyOverrideRepository;

    @Autowired
    private AuditLogUtil auditLogUtil;

    @Autowired
    private AlertUtil alertUtil;

    // STORY: FPMAPP-9049 - Retrieve all currency overrides (current and historical)
    @Override
    public List<CurrencyOverrideDTO> getAllOverrides() {
        List<CurrencyOverride> overrides = currencyOverrideRepository.findAllByOrderByCreatedAtDesc();
        return overrides.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // STORY: FPMAPP-9049 - Create a new currency override with audit logging and alerting
    @Override
    @Transactional
    public CurrencyOverrideDTO createOverride(CurrencyOverrideDTO overrideDTO, String createdBy) {
        CurrencyOverride entity = new CurrencyOverride();
        BeanUtils.copyProperties(overrideDTO, entity);
        entity.setCreatedBy(createdBy);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setStatus("ACTIVE");

        // TODO: Add business validation for overlapping overrides or invalid rates

        CurrencyOverride saved = currencyOverrideRepository.save(entity);

        // Audit log the override creation
        auditLogUtil.log("CurrencyOverride", saved.getId(), "CREATE", createdBy, "Created currency override for " + saved.getCurrencyCode());

        // Trigger alert for override creation
        alertUtil.sendAlert("Currency override created for " + saved.getCurrencyCode() + " with rate " + saved.getOverriddenRate());

        return toDTO(saved);
    }

    // STORY: FPMAPP-9049 - Retrieve a currency override by its ID
    @Override
    public CurrencyOverrideDTO getOverrideById(Long id) {
        CurrencyOverride entity = currencyOverrideRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Currency override not found for id: " + id));
        return toDTO(entity);
    }

    private CurrencyOverrideDTO toDTO(CurrencyOverride entity) {
        CurrencyOverrideDTO dto = new CurrencyOverrideDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

}
