# Architecture

## Capability map

| Capability | Owner | Boundary |
|---|---|---|
| CGMES upload, nested archive expansion, metadata | `srv.cnm.services` | MinIO, Elasticsearch, PostgreSQL, RabbitMQ through `com.infra` |
| Load flow, security, sensitivity and LF/SA rerun | `srv.computation.services` | PowSyBl through `com.powsbl` |
| PST remedial-action optimisation | `srv.optimization.services` | SCIP command adapter through `com.solver` |
| Apply proposed actions, workflow state and comparisons | `srv.rsa.services` | PowSyBl update port, PostgreSQL metadata, artifact/document ports |
| Upload/search UI | `gui.cnm.manage` | CNM REST contract |
| Analysis workflow UI | `gui.rsa` | Computation, optimization, and RSA REST contracts |

Dependencies point inward toward `data.cnm` contracts. External client types never cross adapter APIs. Camunda can coordinate long-running work, but domain transitions remain in application services.

## Migration checklist

- [x] Java 21 Maven reactor and module inventory
- [x] Neutral DTO and adapter boundaries
- [x] Production and deterministic mock REST surfaces
- [x] OpenAPI documents
- [x] Vue/Vite UIs and shared components
- [x] Local infrastructure composition
- [x] JUnit/Mockito orchestration tests
- [x] PowSyBl 2026.0.0 adapter with network-store client 1.46.0
- [x] Environment-configured MinIO, Elasticsearch, RabbitMQ and PostgreSQL adapters
- [x] Reproducible SCIP and PowSyBl network-store image digests

## Naming decision

The requested `srv.computaion.services` name is normalized to `srv.computation.services` to avoid carrying a spelling error into Maven coordinates, Java packages, URLs, and operational dashboards.
