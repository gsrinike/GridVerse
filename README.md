# GridVerse

GridVerse is a modular Java 21/Spring Boot and Vue implementation of the CGMES reliability and security-analysis workflow:

`CGMES upload → load flow → security analysis → sensitivity analysis → remedial-action optimisation → apply actions → rerun → compare results`

The original Python/Streamlit prototype has been decomposed into neutral contracts (`data.cnm`), infrastructure ports (`com.infra`), a single PowSyBl boundary (`com.powsbl`), an open-source solver boundary (`com.solver`), production/mock REST services, and two Vue applications. Legacy commercial engine and solver integrations are intentionally not carried forward.

## Build

```bash
mvn -Dmaven.repo.local=work/m2 -Ddocker.skip.build=true -Ddocker.skip.push=true clean package
docker compose -f docker/docker-compose.yml config
docker compose -f docker/docker-compose.yml up --build
```

Frontend builds are available per GUI module with `npm ci && npm run build`.

See [docs/architecture.md](docs/architecture.md) and each module README for ownership and boundaries.
