package com.example.barber_server.utils;

import com.example.barber_server.dto.dto_response.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Objects;

public class SecurityUtils {

    public static UserPrincipal getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal;
        }
        return null;
    }

    public static Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getId();
        }
        return null;
    }

    public static boolean isAuthorized(Integer ownerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return true;

        Integer currentUserId = getCurrentUserId();
        return Objects.equals(currentUserId, ownerId);
    }

    public static boolean isAdmin() {
        UserPrincipal principal = getPrincipal();
        return principal != null && principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public static boolean isBarber() {
        UserPrincipal principal = getPrincipal();
        return principal != null && principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_BARBER"));
    }

    public static boolean isCustomer() {
        UserPrincipal principal = getPrincipal();
        return principal != null && principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
    }

    public static void checkAuthority(Integer ownerId) {
        if (!isAuthorized(ownerId)) {
            throw new AccessDeniedException("Bạn không có quyền thực hiện hành động này!");
        }
    }
}