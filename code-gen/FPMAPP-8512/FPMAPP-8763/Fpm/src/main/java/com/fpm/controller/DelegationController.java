package com.fpm.controller;

import com.fpm.exception.DelegationException;
import com.fpm.service.DelegationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/delegation")
public class DelegationController {

    @Autowired
    private DelegationService delegationService;

    // STORY: FPMAPP-8763 - Endpoint to delegate approval rights with error handling and no audit on failure
    @PostMapping("/delegate")
    public ResponseEntity<?> delegateApproval(@RequestBody Map<String, String> request) {
        String fromUserId = request.get("fromUserId");
        String toUserId = request.get("toUserId");

        try {
            delegationService.delegateApprovalRights(fromUserId, toUserId);
            return ResponseEntity.ok(Map.of("message", "Delegation successful."));
        } catch (DelegationException e) {
            // Return appropriate error message without creating audit log
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Generic error handling
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error."));
        }
    }

}