# srv.cnm.services

Production CNM ingestion: safe recursive ZIP expansion, RDF filtering, filename metadata extraction, object/document persistence, completion events, and PowSyBl network-store import. Runtime adapters are injected through `com.infra`; local fallback adapters keep the service runnable when external clients are not configured. Configuration is under `base/` and `local/`; the OpenAPI contract is under `openapi/`. Run `mvn -pl srv.cnm.services -am test`.
