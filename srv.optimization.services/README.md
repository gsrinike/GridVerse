# srv.optimization.services

Owns preventive PST RAO. It validates the nine-file PST CSV package, applies availability/group/limit/schedule/cost policies, validates current sensitivities, builds the single/multi-timeframe linearized current-and-tap model, invokes SCIP through `com.solver`, and returns neutral proposed actions. Run `mvn -pl srv.optimization.services -am test`.
