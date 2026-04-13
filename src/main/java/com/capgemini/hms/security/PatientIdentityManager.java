package com.capgemini.hms.security;

import com.capgemini.hms.security.services.UserDetailsImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class PatientIdentityManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        Authentication auth = authentication.get();
        
        // 1. If user is Admin, Doctor, or Nurse, they have full access
        boolean isStaff = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                               a.getAuthority().equals("ROLE_DOCTOR") || 
                               a.getAuthority().equals("ROLE_NURSE"));
        
        if (isStaff) {
            return new AuthorizationDecision(true);
        }

        // 2. If user is a Patient, they can only access their own SSN
        String ssnFromUri = context.getVariables().get("ssn");
        if (ssnFromUri == null || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return new AuthorizationDecision(false);
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Integer userSsn = userDetails.getPatientSsn();

        boolean isMovingOwnData = userSsn != null && userSsn.toString().equals(ssnFromUri);
        return new AuthorizationDecision(isMovingOwnData);
    }
}
