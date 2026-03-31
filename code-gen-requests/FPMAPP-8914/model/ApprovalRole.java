package com.fpm.model;

import jakarta.persistence.*;

@Entity
@Table(name = "approval_roles")
public class ApprovalRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roleName; // e.g., DIRECTOR, MANAGER

    @Column(nullable = false)
    private Double approvalThreshold; // max amount this role can approve

    // STORY: FPMAPP-8914 - Entity to store role-based approval thresholds

    public ApprovalRole() {
    }

    public ApprovalRole(String roleName, Double approvalThreshold) {
        this.roleName = roleName;
        this.approvalThreshold = approvalThreshold;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Double getApprovalThreshold() {
        return approvalThreshold;
    }

    public void setApprovalThreshold(Double approvalThreshold) {
        this.approvalThreshold = approvalThreshold;
    }
}
