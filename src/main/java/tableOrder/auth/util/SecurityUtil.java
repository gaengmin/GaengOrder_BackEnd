package tableOrder.auth.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import tableOrder.users.dto.security.CustomUserDetails;

@Component
public class SecurityUtil {

    /**
     * 현재 로그인한 사용자의 매장번호 (storeNo) 반환
     */

    public static Long getCurrentUsersStoreNo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인 정보 없음");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getStoreNo();
        }
        throw new IllegalArgumentException("인증 정보에 storeNo가 없습니다.");
    }

    /**
     * 현재 로그인한 사용자의 userId 반환
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getUsername();
        }
        throw new IllegalStateException("인증 정보에 userId가 없습니다.");
    }

    /**
     * 현재 로그인한 사용자의 권한(ROLE) 반환 (예: ADMIN, SUPERADMIN)
     */
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .orElseThrow(() -> new IllegalStateException("권한 정보가 없습니다."));
    }

    /**
     * 현재 로그인한 사용자가 SUPERADMIN 권한인지 여부 반환
     */
    public static boolean isSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SUPERADMIN"));
    }
}
