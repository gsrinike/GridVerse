# srv.computation.services

Owns load flow, security analysis, current sensitivity analysis, and post-action LF/SA reruns. All engine calls pass through `com.powsbl`; only neutral DTOs cross REST. Runtime configuration is externalized under `base/`. Run `mvn -pl srv.computation.services -am test`.
