package eu.gridverse.auth;

import java.util.Set;

public interface AuthorizationService { boolean isAllowed(String subject, String action, Set<String> roles); }
