package tableOrder.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ResponseTokenDto {

    // 토큰 쌍 DTO
    @Getter
    @AllArgsConstructor
    public static class TokenPair {
        private String accessToken;
        private String refreshToken;
    }
}
