# Merge Guide for `FPMAPP-8908`

Each story folder contains only the methods/logic needed for that story.
Merge files into your codebase according to the **Action** column below.

| Action | Meaning |
|--------|---------|
| `CREATE` | New file — does not exist in the codebase. Copy it as-is. |
| `MERGE` | Partial code (methods/logic) — paste into an existing file at the right location. |

| Story | File | Action / Description |
|-------|------|----------------------|
| FPMAPP-8918 | `model/AuditLog.java` | One-line summary: AuditLog entity to store immutable audit trail records - CREATE new file |
| FPMAPP-8918 | `repository/AuditLogRepository.java` | One-line summary: Repository interface for AuditLog entity with query methods - CREATE new file |
| FPMAPP-8918 | `service/AuditLogService.java` | One-line summary: Service interface for audit log operations - CREATE new file |
| FPMAPP-8918 | `service/impl/AuditLogServiceImpl.java` | One-line summary: Implementation of AuditLogService with immutable save and query methods - CREATE new file |
| FPMAPP-8917 | `controller/ApprovalNotificationWebSocketController.java` | controller/ApprovalNotificationWebSocketController.java - CREATE new WebSocket controller for real-time approval status updates |
| FPMAPP-8917 | `dto/ApprovalRequestStatusUpdateDTO.java` | dto/ApprovalRequestStatusUpdateDTO.java - CREATE DTO for approval request status updates with history |
| FPMAPP-8917 | `service/ApprovalNotificationService.java` | service/ApprovalNotificationService.java - CREATE service interface for approval notifications |
| FPMAPP-8917 | `service/impl/ApprovalNotificationServiceImpl.java` | service/impl/ApprovalNotificationServiceImpl.java - CREATE implementation for approval notification service with WebSocket and email |
| FPMAPP-8916 | `model/CurrencyRate.java` | CREATE: Entity model for currency exchange rates with historical data and override audit fields |
| FPMAPP-8916 | `repository/CurrencyRateRepository.java` | CREATE: Repository interface for CurrencyRate entity with queries for latest, historical, and date range retrieval |
| FPMAPP-8916 | `service/CurrencyRateService.java` | CREATE: Service interface defining currency rate operations including retrieval, override, and sync |
| FPMAPP-8916 | `service/impl/CurrencyRateServiceImpl.java` | CREATE: Service implementation for currency rate operations including scheduled sync, retrieval, and admin override with logging and alerting |
| FPMAPP-8916 | `controller/CurrencyRateController.java` | CREATE: REST controller exposing APIs for current and historical currency rates and admin override with security |
| FPMAPP-8915 | `model/ApprovalDelegation.java` | One-line summary: Entity class for approval delegation metadata - CREATE |
| FPMAPP-8915 | `model/ApprovalAuditLog.java` | One-line summary: Entity class for immutable audit logs of approval and delegation events - CREATE |
| FPMAPP-8915 | `repository/ApprovalDelegationRepository.java` | One-line summary: Repository interface for ApprovalDelegation entity - CREATE |
| FPMAPP-8915 | `service/ApprovalDelegationService.java` | One-line summary: Service interface for approval delegation management - CREATE |
| FPMAPP-8914 | `model/ApprovalRole.java` | CREATE entity to represent approval roles and their value thresholds |
| FPMAPP-8914 | `model/ApprovalRequest.java` | CREATE entity to represent approval requests with role and status metadata |
| FPMAPP-8914 | `repository/ApprovalRoleRepository.java` | CREATE repository interface for ApprovalRole entity |
| FPMAPP-8914 | `repository/ApprovalRequestRepository.java` | CREATE repository interface for ApprovalRequest entity |
| FPMAPP-8914 | `service/ApprovalService.java` | CREATE service interface for approval workflow |
| FPMAPP-8914 | `service/impl/ApprovalServiceImpl.java` | MERGE service implementation with hierarchical role-based approval logic |
| FPMAPP-8914 | `controller/ApprovalController.java` | CREATE REST controller exposing endpoints for hierarchical role-based approval workflow |
| FPMAPP-8913 | `controller/RealTimeStatusController.java` | RealTimeStatusController with WebSocket endpoints and push methods - CREATE |
| FPMAPP-8913 | `dto/ApprovalStatusUpdateDTO.java` | DTO for approval status updates - CREATE |
| FPMAPP-8913 | `dto/UploadProgressUpdateDTO.java` | DTO for upload progress updates - CREATE |
| FPMAPP-8913 | `service/impl/RealTimeStatusServiceImpl.java` | Service implementation for pushing real-time status updates - CREATE |
| FPMAPP-8913 | `service/RealTimeStatusService.java` | Service interface for real-time status updates - CREATE |
| FPMAPP-8912 | `model/CurrencyRate.java` | model/CurrencyRate.java - CREATE new entity with additional fields for real-time and historical currency rates, admin override flags and reasons |
| FPMAPP-8912 | `repository/CurrencyRateRepository.java` | repository/CurrencyRateRepository.java - CREATE new repository interface for CurrencyRate entity with methods for latest, historical and date range queries |
| FPMAPP-8912 | `service/CurrencyService.java` | service/CurrencyService.java - CREATE new service interface defining methods for real-time fetch, historical queries and admin overrides |
| FPMAPP-8912 | `service/impl/CurrencyServiceImpl.java` | service/impl/CurrencyServiceImpl.java - CREATE implementation of CurrencyService with scheduled real-time fetch, historical queries, and admin override logic |
| FPMAPP-8911 | `model/AuditLog.java` | CREATE model/AuditLog.java - Entity representing audit log entries for approval actions |
| FPMAPP-8911 | `repository/AuditLogRepository.java` | CREATE repository/AuditLogRepository.java - JPA repository for AuditLog entity |
| FPMAPP-8911 | `service/ApprovalAuditService.java` | CREATE service/ApprovalAuditService.java - Service interface for audit logging and retrieval |
| FPMAPP-8911 | `service/impl/ApprovalAuditServiceImpl.java` | CREATE service/impl/ApprovalAuditServiceImpl.java - Implementation of audit logging service |
| FPMAPP-8910 | `model/Delegation.java` | Create Delegation entity to represent approval delegation with permissions and validity period |
| FPMAPP-8910 | `repository/DelegationRepository.java` | Create DelegationRepository for CRUD and query operations on Delegation entity |
| FPMAPP-8910 | `service/ApprovalService.java` | Extend ApprovalService interface with delegation related methods |
| FPMAPP-8910 | `service/impl/ApprovalServiceImpl.java` | Implement delegation logic, validation, and audit logging in ApprovalServiceImpl |
| FPMAPP-8910 | `controller/ApprovalController.java` | Add ApprovalController endpoint to handle delegation requests |
| FPMAPP-8910 | `dto/DelegationRequestDTO.java` | Create DelegationRequestDTO to accept delegation parameters from API |
| FPMAPP-8909 | `model/ApprovalRequest.java` | MERGE - Add approvalStatus and currentApproverRole fields to ApprovalRequest entity for role-based approval workflow |
| FPMAPP-8909 | `dto/ApprovalActionRequest.java` | CREATE - DTO for approval action requests including user role for validation |
| FPMAPP-8909 | `service/ApprovalService.java` | MERGE - Add methods for processing approval actions and determining approver role based on request value |
| FPMAPP-8909 | `service/impl/ApprovalServiceImpl.java` | MERGE - Implement role-based approval logic and validation in ApprovalServiceImpl |
| FPMAPP-8909 | `controller/ApprovalController.java` | MERGE - Add REST endpoints for approval actions and fetching approval request details with role-based enforcement |
| FPMAPP-8909 | `service/impl/ApprovalServiceImpl.java` | MERGE - Add method to fetch approval request by id for controller GET endpoint |