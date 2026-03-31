package com.fpm.service.impl;

import com.fpm.dto.CurrencyOverrideRequest;
import com.fpm.dto.CurrencyOverrideAuditLogDTO;
import com.fpm.model.CurrencyOverrideAuditLog;
import com.fpm.repository.CurrencyOverrideAuditLogRepository;
import com.fpm.service.CurrencyOverrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CurrencyOverrideServiceImpl implements CurrencyOverrideService {

    @Autowired
    private CurrencyOverrideAuditLogRepository auditLogRepository;

    @Autowired
    private JavaMailSender mailSender;

    // STORY: FPMAPP-9043 - Submit currency rate override and return confirmation with audit logs
    @Override
    @Transactional
    public Map<String, Object> submitOverride(CurrencyOverrideRequest request) {
        // TODO: Add validation for request fields (currencyPair, overrideRate, reason, effectiveDate)

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        CurrencyOverrideAuditLog logEntry = new CurrencyOverrideAuditLog();
        logEntry.setCurrencyPair(request.getCurrencyPair());
        logEntry.setOverrideRate(request.getOverrideRate());
        logEntry.setReason(request.getReason());
        logEntry.setTimestamp(LocalDateTime.now());
        logEntry.setUsername(username);
        logEntry.setEffectiveDate(request.getEffectiveDate());

        auditLogRepository.save(logEntry);

        // TODO: Persist override rate in the system (e.g. cache or DB) for actual currency conversion usage

        sendOverrideNotificationEmail(logEntry);

        List<CurrencyOverrideAuditLogDTO> logs = getOverrideAuditLogs();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Override submitted successfully.");
        response.put("auditLogs", logs);
        return response;
    }

    // STORY: FPMAPP-9043 - Retrieve audit logs of currency rate overrides
    @Override
    public List<CurrencyOverrideAuditLogDTO> getOverrideAuditLogs() {
        List<CurrencyOverrideAuditLog> logs = auditLogRepository.findAllByOrderByTimestampDesc();
        return logs.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private CurrencyOverrideAuditLogDTO toDTO(CurrencyOverrideAuditLog log) {
        CurrencyOverrideAuditLogDTO dto = new CurrencyOverrideAuditLogDTO();
        dto.setCurrencyPair(log.getCurrencyPair());
        dto.setOverrideRate(log.getOverrideRate());
        dto.setReason(log.getReason());
        dto.setTimestamp(log.getTimestamp());
        dto.setUsername(log.getUsername());
        dto.setEffectiveDate(log.getEffectiveDate().toString());
        return dto;
    }

    private void sendOverrideNotificationEmail(CurrencyOverrideAuditLog logEntry) {
        // STORY: FPMAPP-9043 - Send alert notifications via SMTP on override submission
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            // TODO: Configure these email addresses and subject appropriately
            message.setTo("fpm-admin-alerts@example.com");
            message.setSubject("Currency Override Submitted: " + logEntry.getCurrencyPair());
            StringBuilder sb = new StringBuilder();
            sb.append("A new currency override has been submitted:\n");
            sb.append("Currency Pair: ").append(logEntry.getCurrencyPair()).append("\n");
            sb.append("Override Rate: ").append(logEntry.getOverrideRate()).append("\n");
            sb.append("Reason: ").append(logEntry.getReason()).append("\n");
            sb.append("Effective Date: ").append(logEntry.getEffectiveDate()).append("\n");
            sb.append("Submitted By: ").append(logEntry.getUsername()).append("\n");
            sb.append("Timestamp: ").append(logEntry.getTimestamp()).append("\n");
            message.setText(sb.toString());
            mailSender.send(message);
        } catch (Exception e) {
            // TODO: Log error sending email but do not fail the override submission
            e.printStackTrace();
        }
    }
}
