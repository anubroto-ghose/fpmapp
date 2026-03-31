package com.fpm.controller;

import com.fpm.model.AuditLog;
import com.fpm.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/fpm/audit-logs")
public class FpmAuditLogController {

    private final AuditLogService auditLogService;

    @Autowired
    public FpmAuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // STORY: FPMAPP-9040 - Expose audit logs with filtering by action_type, user_id, from_date, to_date
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @RequestParam(required = false) String action_type,
            @RequestParam(required = false) String user_id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from_date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to_date,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> auditLogsPage = auditLogService.getAuditLogsPaged(action_type, user_id, from_date, to_date, pageable);
        return ResponseEntity.ok(auditLogsPage.getContent());
    }
}
