# Merge Guide for `FPMAPP-8908`

Each story folder contains only the methods/logic needed for that story.
Merge files into your codebase according to the **Action** column below.

| Action | Meaning |
|--------|---------|
| `CREATE` | New file — does not exist in the codebase. Copy it as-is. |
| `MERGE` | Partial code (methods/logic) — paste into an existing file at the right location. |

| Story | File | Action / Description |
|-------|------|----------------------|
| FPMAPP-8918 | `model/AuditLog.java` | CREATE: Entity representing immutable audit log entries for approval, delegation, and request changes |
| FPMAPP-8918 | `model/RequestVersion.java` | CREATE: Entity for versioning and tracking changes to requests after submission |
| FPMAPP-8918 | `repository/AuditLogRepository.java` | CREATE: Repository interface for querying immutable audit logs with filtering capabilities |
| FPMAPP-8918 | `service/AuditLogService.java` | CREATE: Service interface defining audit log operations and queries |
| FPMAPP-8918 | `service/impl/AuditLogServiceImpl.java` | CREATE: Implementation of AuditLogService with transactional immutable audit log saving and querying |
| FPMAPP-8917 | `controller/ApprovalNotificationWebSocketController.java` | Creates WebSocket controller to send real-time approval status updates to users (CREATE) |
| FPMAPP-8917 | `dto/ApprovalStatusUpdateDTO.java` | DTO for approval status update messages sent via WebSocket and email (CREATE) |
| FPMAPP-8917 | `service/NotificationService.java` | Notification service interface to send approval status update notifications (CREATE) |
| FPMAPP-8917 | `service/impl/NotificationServiceImpl.java` | Implementation of NotificationService sending email and in-app WebSocket notifications (CREATE) |
| FPMAPP-8916 | `model/CurrencyRate.java` | CREATE: Entity representing currency exchange rates with historical data and override audit fields |
| FPMAPP-8916 | `repository/CurrencyRateRepository.java` | CREATE: Repository interface for CurrencyRate entity with methods for latest and historical queries |
| FPMAPP-8916 | `service/CurrencyRateService.java` | CREATE: Service interface defining currency rate operations including retrieval, override, and sync |
| FPMAPP-8916 | `service/impl/CurrencyRateServiceImpl.java` | MERGE: Implementation of CurrencyRateService with scheduled sync, retrieval, and admin override logic |
| FPMAPP-8916 | `controller/CurrencyRateController.java` | CREATE: REST controller exposing APIs for latest, historical currency rates and admin override with security |
| FPMAPP-8915 | `model/ApprovalDelegation.java` | One-line summary: Entity class for approval delegation metadata - CREATE |
| FPMAPP-8915 | `model/AuditLog.java` | One-line summary: Entity class for immutable audit logs - CREATE |
| FPMAPP-8915 | `repository/ApprovalDelegationRepository.java` | One-line summary: Repository interface for ApprovalDelegation entity - CREATE |
| FPMAPP-8915 | `service/ApprovalDelegationService.java` | One-line summary: Service interface for approval delegation operations - CREATE |
| FPMAPP-8915 | `service/impl/ApprovalDelegationServiceImpl.java` | One-line summary: Implementation of approval delegation service with create, update, revoke, and permission check - CREATE |
| FPMAPP-8915 | `repository/AuditLogRepository.java` | One-line summary: Repository interface for AuditLog entity - CREATE |
| FPMAPP-8914 | `model/ApprovalRole.java` | One-line summary: Entity representing approval roles and their value thresholds - CREATE |
| FPMAPP-8914 | `model/ApprovalRequest.java` | One-line summary: Entity representing approval requests with status and role tracking - CREATE |
| FPMAPP-8914 | `repository/ApprovalRoleRepository.java` | One-line summary: Repository interface for ApprovalRole entity - CREATE |
| FPMAPP-8914 | `repository/ApprovalRequestRepository.java` | One-line summary: Repository interface for ApprovalRequest entity - CREATE |
| FPMAPP-8914 | `service/ApprovalWorkflowService.java` | One-line summary: Service interface defining approval workflow operations - CREATE |
| FPMAPP-8914 | `service/impl/ApprovalWorkflowServiceImpl.java` | One-line summary: Implementation of hierarchical role-based approval workflow service - CREATE |
| FPMAPP-8914 | `controller/ApprovalWorkflowController.java` | One-line summary: REST controller exposing endpoints for hierarchical role-based approval workflow - CREATE |
| FPMAPP-8913 | `controller/RealTimeStatusController.java` | RealTimeStatusController provides methods to push approval status and upload progress updates via WebSocket topics; MERGE (new class) |
| FPMAPP-8913 | `dto/ApprovalStatusUpdateDTO.java` | ApprovalStatusUpdateDTO represents real-time approval status update data; CREATE new DTO |
| FPMAPP-8913 | `dto/UploadProgressUpdateDTO.java` | UploadProgressUpdateDTO represents real-time upload progress update data; CREATE new DTO |
| FPMAPP-8913 | `config/WebSocketConfig.java` | WebSocketConfig sets up STOMP WebSocket endpoints and message broker; CREATE new config class |
| FPMAPP-8912 | `model/CurrencyRate.java` | CREATE model/CurrencyRate.java - Entity representing currency exchange rates with timestamp, historical flag, and admin override fields |
| FPMAPP-8912 | `repository/CurrencyRateRepository.java` | CREATE repository/CurrencyRateRepository.java - JPA repository for CurrencyRate entity with queries for latest, historical, and overridden rates |
| FPMAPP-8912 | `service/CurrencyService.java` | CREATE service/CurrencyService.java - Service interface defining currency rate operations including fetch, query, and admin override |
| FPMAPP-8912 | `service/impl/CurrencyServiceImpl.java` | MERGE service/impl/CurrencyServiceImpl.java - Implementation of CurrencyService with scheduled fetch, historical queries, and admin override logic |
| FPMAPP-8911 | `model/AuditLog.java` | CREATE AuditLog entity to store immutable audit trail entries for approval, rejection, delegation, and override actions |
| FPMAPP-8911 | `repository/AuditLogRepository.java` | CREATE AuditLogRepository interface for querying audit logs by approval request ID |
| FPMAPP-8911 | `service/ApprovalAuditService.java` | CREATE ApprovalAuditService interface to define audit log operations |
| FPMAPP-8911 | `service/impl/ApprovalAuditServiceImpl.java` | CREATE ApprovalAuditServiceImpl to implement audit log creation and retrieval logic |
| FPMAPP-8910 | `model/Delegation.java` | Create Delegation entity to represent approval delegation with permissions and timestamps |
| FPMAPP-8910 | `dto/DelegationRequestDTO.java` | Create DelegationRequestDTO for API input to delegate approval authority |
| FPMAPP-8910 | `repository/DelegationRepository.java` | Create DelegationRepository for CRUD and query operations on Delegation entity |
| FPMAPP-8910 | `service/impl/ApprovalServiceImpl.java` | Merge new methods into ApprovalServiceImpl to handle delegation creation, validation, and approval request delegation marking |
| FPMAPP-8910 | `controller/ApprovalController.java` | Merge new API endpoint into ApprovalController to accept delegation requests and create delegations |
| FPMAPP-8909 | `model/ApprovalRequest.java` | MERGE: Add approvalStatus and currentApproverRole fields to ApprovalRequest entity for role-based approval workflow |
| FPMAPP-8909 | `dto/ApprovalActionRequest.java` | CREATE: DTO for approval action requests including approver role and action |
| FPMAPP-8909 | `service/ApprovalService.java` | MERGE: Add methods to ApprovalService interface for role-based approval processing and retrieval |
| FPMAPP-8909 | `service/impl/ApprovalServiceImpl.java` | MERGE: Implement role-based approval logic in ApprovalServiceImpl with validation and status updates |
| FPMAPP-8909 | `controller/ApprovalController.java` | MERGE: Add REST endpoints in ApprovalController for processing approval actions and retrieving approval request details |