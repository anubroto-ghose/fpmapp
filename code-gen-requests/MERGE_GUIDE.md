# Merge Guide for `FPMAPP-9032`

Each story folder contains only the methods/logic needed for that story.
Merge files into your codebase according to the **Action** column below.

| Action | Meaning |
|--------|---------|
| `CREATE` | New file — does not exist in the codebase. Copy it as-is. |
| `MERGE` | Partial code (methods/logic) — paste into an existing file at the right location. |

| Story | File | Action / Description |
|-------|------|----------------------|
| FPMAPP-9049 | `dto/CurrencyOverrideDTO.java` | CREATE DTO for currency override data transfer between frontend and backend |
| FPMAPP-9049 | `controller/CurrencyOverrideController.java` | CREATE REST controller for currency override management with admin role restriction |
| FPMAPP-9049 | `service/CurrencyOverrideService.java` | CREATE service interface for currency override business logic |
| FPMAPP-9049 | `service/impl/CurrencyOverrideServiceImpl.java` | CREATE service implementation for currency override management with audit and alert integration |
| FPMAPP-9048 | `model/CurrencyRate.java` | CREATE model/CurrencyRate.java: Entity representing currency rates with historical timestamps and override flag |
| FPMAPP-9048 | `model/CurrencyRateOverride.java` | CREATE model/CurrencyRateOverride.java: Entity to store admin override records with audit info |
| FPMAPP-9048 | `repository/CurrencyRateRepository.java` | CREATE repository/CurrencyRateRepository.java: Repository for querying current and historical currency rates |
| FPMAPP-9048 | `controller/CurrencyConversionController.java` | MERGE controller/CurrencyConversionController.java: Add endpoints for sync, historical query, and admin override with alerting |
| FPMAPP-9048 | `dto/CurrencyRateOverrideRequest.java` | CREATE dto/CurrencyRateOverrideRequest.java: DTO for admin override API request body |
| FPMAPP-9048 | `service/CurrencyRateSyncService.java` | CREATE service/CurrencyRateSyncService.java: Service interface for currency rate sync and override alerting |
| FPMAPP-9048 | `service/impl/CurrencyRateSyncServiceImpl.java` | CREATE service/impl/CurrencyRateSyncServiceImpl.java: Implementation of sync service with scheduled job and email alert |
| FPMAPP-9048 | `repository/CurrencyRateOverrideRepository.java` | CREATE repository/CurrencyRateOverrideRepository.java: Repository for currency rate override records |
| FPMAPP-9047 | `model/DelegationLog.java` | CREATE model/DelegationLog.java: JPA entity for delegation_logs table to store delegation audit logs |
| FPMAPP-9047 | `repository/DelegationLogRepository.java` | CREATE repository/DelegationLogRepository.java: Spring Data JPA repository for DelegationLog entity |
| FPMAPP-9047 | `dto/DelegationRequestDTO.java` | CREATE dto/DelegationRequestDTO.java: DTO for delegation POST request payload |
| FPMAPP-9047 | `controller/FpmCamundaDelegationController.java` | CREATE controller/FpmCamundaDelegationController.java: REST controller for delegation endpoint with role validation, Camunda integration, and audit logging |
| FPMAPP-9046 | `dto/ApprovalStatusResponse.java` | CREATE DTO for approval status response with stages and current status |
| FPMAPP-9046 | `service/ApprovalStatusService.java` | CREATE service interface for approval status retrieval |
| FPMAPP-9046 | `service/impl/ApprovalStatusServiceImpl.java` | CREATE service implementation with dummy data for approval status retrieval |
| FPMAPP-9046 | `controller/FpmDetailController.java` | MERGE new GET endpoint in existing controller to provide approval status data |
| FPMAPP-9045 | `model/AuditTrail.java` | One-line summary: AuditTrail entity representing audit log entries - CREATE new file |
| FPMAPP-9045 | `repository/AuditTrailRepository.java` | One-line summary: JPA repository interface for AuditTrail entity - CREATE new file |
| FPMAPP-9045 | `service/AuditTrailService.java` | One-line summary: Service interface for audit trail logging and retrieval - CREATE new file |
| FPMAPP-9045 | `service/impl/AuditTrailServiceImpl.java` | One-line summary: Implementation of AuditTrailService for logging and retrieving audit entries - CREATE new file |
| FPMAPP-9044 | `controller/FpmcamundaController.java` | MERGE: Add new POST /route endpoint to FpmcamundaController for role-based approval routing |
| FPMAPP-9044 | `dto/ApprovalRequestDTO.java` | CREATE: DTO for approval request input payload |
| FPMAPP-9044 | `dto/ApprovalRoutingResponseDTO.java` | CREATE: DTO for approval routing response details |
| FPMAPP-9044 | `service/FpmcamundaService.java` | MERGE: Add routeApproval method to FpmcamundaService interface |
| FPMAPP-9044 | `service/impl/FpmcamundaServiceImpl.java` | MERGE: Implement routeApproval method and helper methods in FpmcamundaServiceImpl with Camunda integration and notification |
| FPMAPP-9043 | `dto/CurrencyOverrideRequest.java` | One-line summary: DTO for currency override request - CREATE |
| FPMAPP-9043 | `dto/CurrencyOverrideAuditLogDTO.java` | One-line summary: DTO for currency override audit log entries - CREATE |
| FPMAPP-9043 | `controller/CurrencyOverrideController.java` | One-line summary: Controller for currency override API endpoints - CREATE |
| FPMAPP-9043 | `service/CurrencyOverrideService.java` | One-line summary: Service interface for currency override operations - CREATE |
| FPMAPP-9043 | `service/impl/CurrencyOverrideServiceImpl.java` | One-line summary: Service implementation for currency override logic including persistence and email notification - CREATE |
| FPMAPP-9043 | `model/CurrencyOverrideAuditLog.java` | One-line summary: JPA entity for currency override audit log entries - CREATE |
| FPMAPP-9043 | `repository/CurrencyOverrideAuditLogRepository.java` | One-line summary: Repository interface for currency override audit log persistence - CREATE |
| FPMAPP-9042 | `model/CurrencyRate.java` | Entity model for currency_rates table with override and timestamp fields - CREATE |
| FPMAPP-9042 | `repository/CurrencyRateRepository.java` | Repository interface for CurrencyRate entity with queries for current and historical rates - CREATE |
| FPMAPP-9042 | `service/CurrencyRateService.java` | Service interface for currency rate operations including get, override, synchronize - CREATE |
| FPMAPP-9042 | `service/impl/CurrencyRateServiceImpl.java` | Service implementation for currency rate operations including synchronization, override, and queries - CREATE |
| FPMAPP-9042 | `controller/CurrencyConversionController.java` | Controller exposing currency rate GET and admin override POST endpoints - MERGE |
| FPMAPP-9041 | `controller/ApprovalStatusController.java` | controller/ApprovalStatusController.java - MERGE: Add REST and WebSocket endpoints for real-time approval status updates |
| FPMAPP-9041 | `service/ApprovalStatusService.java` | service/ApprovalStatusService.java - MERGE: Add service interface methods for approval status retrieval and notification |
| FPMAPP-9041 | `service/impl/ApprovalStatusServiceImpl.java` | service/impl/ApprovalStatusServiceImpl.java - MERGE: Implement service methods for approval status retrieval and WebSocket notification |
| FPMAPP-9041 | `dto/ApprovalStatusDTO.java` | dto/ApprovalStatusDTO.java - CREATE: Data Transfer Object for approval status details |
| FPMAPP-9040 | `model/AuditLog.java` | CREATE: AuditLog entity representing immutable audit trail entries with detailed metadata |
| FPMAPP-9040 | `repository/AuditLogRepository.java` | CREATE: Repository interface for AuditLog with JpaSpecificationExecutor for flexible querying |
| FPMAPP-9040 | `service/AuditLogService.java` | CREATE: Service interface defining audit log saving and retrieval with filtering and pagination |
| FPMAPP-9040 | `service/impl/AuditLogServiceImpl.java` | CREATE: Service implementation for AuditLogService with filtering and pagination support |
| FPMAPP-9040 | `controller/FpmAuditLogController.java` | CREATE: REST controller exposing audit log retrieval API with filtering and pagination |
| FPMAPP-9039 | `model/Approval.java` | Model class Approval with new columns for approval_role, delegation_flag, and financial_threshold - CREATE |
| FPMAPP-9039 | `dto/ApprovalRequestDTO.java` | DTO for approval API request with user role, amount, and delegation info - CREATE |
| FPMAPP-9039 | `controller/FpmcamundaController.java` | Controller method for enhanced approval API supporting role-based routing and delegation - MERGE |
| FPMAPP-9039 | `service/FpmcamundaService.java` | Service interface method for processing approval with role-based routing and delegation - MERGE |
| FPMAPP-9039 | `service/impl/FpmcamundaServiceImpl.java` | Service implementation for processing approval with role-based routing, delegation checks, notifications, and audit logging - MERGE |
| FPMAPP-9039 | `repository/ApprovalRepository.java` | Repository interface for Approval entity - CREATE |
| FPMAPP-9039 | `util/NotificationUtil.java` | Utility class for sending notifications with role, amount, and delegation info - CREATE |
| FPMAPP-9038 | `controller/FpmcamundaController.java` | controller/FpmcamundaController.java - MERGE: Add modified approval endpoint to enforce delegation rules and trigger notifications |
| FPMAPP-9038 | `dto/ApprovalRequestDTO.java` | dto/ApprovalRequestDTO.java - CREATE: DTO for approval request with delegation info |
| FPMAPP-9038 | `dto/ApprovalResponseDTO.java` | dto/ApprovalResponseDTO.java - CREATE: DTO for approval response with notification and delegation status |
| FPMAPP-9038 | `service/impl/DelegationServiceImpl.java` | service/impl/DelegationServiceImpl.java - CREATE: Implementation of delegation rules enforcement |
| FPMAPP-9037 | `model/CurrencyRateOverrideRequest.java` | CREATE DTO for currency rate override request payload |
| FPMAPP-9037 | `dto/CurrencyRateOverrideResponse.java` | CREATE DTO for currency rate override API response |
| FPMAPP-9037 | `controller/CurrencyConversionController.java` | MERGE new POST /override endpoint into existing CurrencyConversionController |
| FPMAPP-9037 | `service/CurrencyRateOverrideService.java` | CREATE service interface for currency rate override |
| FPMAPP-9037 | `service/impl/CurrencyRateOverrideServiceImpl.java` | CREATE implementation of CurrencyRateOverrideService with DB update, audit logging, and SMTP alerting |
| FPMAPP-9036 | `model/CurrencyExchangeRate.java` | model/CurrencyExchangeRate.java - CREATE entity with new columns for currency rates and overrides |
| FPMAPP-9036 | `repository/CurrencyExchangeRateRepository.java` | repository/CurrencyExchangeRateRepository.java - CREATE repository with queries for historical and latest rates |
| FPMAPP-9036 | `service/CurrencyExchangeRateService.java` | service/CurrencyExchangeRateService.java - CREATE service interface for currency rate operations |
| FPMAPP-9036 | `service/impl/CurrencyExchangeRateServiceImpl.java` | service/impl/CurrencyExchangeRateServiceImpl.java - CREATE service implementation with override, query, and scheduled sync |
| FPMAPP-9036 | `controller/CurrencyConversionController.java` | controller/CurrencyConversionController.java - MERGE add GET and POST endpoints for currency rates and overrides |
| FPMAPP-9036 | `dto/CurrencyRateResponse.java` | dto/CurrencyRateResponse.java - CREATE DTO for currency rate response with override info |
| FPMAPP-9036 | `dto/CurrencyRateOverrideRequest.java` | dto/CurrencyRateOverrideRequest.java - CREATE DTO for override request payload |
| FPMAPP-9036 | `util/EmailUtil.java` | util/EmailUtil.java - CREATE utility for sending SMTP alert emails |
| FPMAPP-9035 | `dto/ApprovalStatusResponse.java` | DTO class for approval status response - CREATE |
| FPMAPP-9035 | `service/ApprovalStatusService.java` | Service interface for approval status retrieval - CREATE |
| FPMAPP-9035 | `service/impl/ApprovalStatusServiceImpl.java` | Service implementation for approval status retrieval - CREATE |
| FPMAPP-9035 | `controller/FpmApprovalStatusController.java` | Controller exposing GET /fpm/approval/status API - CREATE |
| FPMAPP-9034 | `model/AuditTrailLog.java` | CREATE - AuditTrailLog entity representing immutable audit trail records with new columns and indexes |
| FPMAPP-9034 | `repository/AuditTrailLogRepository.java` | CREATE - Repository interface for AuditTrailLog entity to support audit trail queries |
| FPMAPP-9034 | `service/AuditTrailLogService.java` | CREATE - Service interface defining audit trail logging and retrieval methods |
| FPMAPP-9034 | `service/impl/AuditTrailLogServiceImpl.java` | CREATE - Service implementation for audit trail logging and retrieval |
| FPMAPP-9033 | `model/ApprovalActionType.java` | CREATE enum for approval action types used in approval records |
| FPMAPP-9033 | `model/ApprovalRecord.java` | MERGE entity ApprovalRecord with new columns for role, threshold, delegation, timestamp, and action type |
| FPMAPP-9033 | `dto/ApprovalRequestDTO.java` | CREATE DTO for approval request payload |
| FPMAPP-9033 | `controller/FpmCamundaController.java` | MERGE controller method to handle approval requests with role, amount, and delegation info |
| FPMAPP-9033 | `service/ApprovalService.java` | CREATE service interface for approval workflow processing |
| FPMAPP-9033 | `service/impl/ApprovalServiceImpl.java` | MERGE service implementation with hierarchical approval logic, delegation enforcement, logging, and notification triggers |
| FPMAPP-9033 | `repository/ApprovalRecordRepository.java` | CREATE repository interface for ApprovalRecord persistence |
| FPMAPP-9033 | `util/NotificationUtil.java` | CREATE utility component for sending email and in-app notifications |