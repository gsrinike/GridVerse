# com.infra

Ports and production adapters for Elasticsearch document search, MinIO object storage,
RabbitMQ events, Camunda processes, and PostgreSQL metadata persistence. Adapter
configuration is environment-driven; technology clients never leak into service
contracts. Run `mvn -pl com.infra -am test`.
