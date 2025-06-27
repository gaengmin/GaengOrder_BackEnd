package tableOrder.common.utils;

import jakarta.servlet.http.Cookie;

/**쿠키 관련 유틸 분리
 * 유틸을 분리하는 이유는 공통된 내용을 따로 뺴므로 코드의 간결화, 유지보수성 향사하기 위해
 * */
public class CookieUtil {

    /**
     * 쿠키 생성 메소드
    * [설명]
    * 전달받은 key와 value로 HttpOnly 쿠키를 생성하여 반환합니다.
    * - 쿠키의 유효기간은 24시간(86400초)으로 설정됩니다.
     * - HttpOnly 옵션을 활성화하여 클라이언트의 JavaScript에서 쿠키에 접근하지 못하도록 보안성을 높입니다.
    * - (주석처리) Secure 옵션을 활성화하면 HTTPS 환경에서만 쿠키가 전송됩니다.(HTTPS(443) 환경 → Secure 옵션 반드시 켜기 (cookie.setSecure(true)))
   * - (주석처리) Path를 "/"로 설정하면 모든 경로에서 쿠키가 유효합니다.
     **/
    public static Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        // cookie.setSecure(true); // HTTPS 환경에서만 전송 시 활성화
        return cookie;
    }

    // 쿠키에서 특정 이름의 값 추출
    public static String extractRefreshToken(Cookie[] cookies, String name) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}