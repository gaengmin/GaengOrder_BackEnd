package tableOrder.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tableOrder.users.dto.enums.Role;

public class RequestUsersDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class requestAdminJoinDto{
        @NotBlank
        private String userId;
        @NotBlank
        private String pwd;
        @NotBlank
        private String name;
        @NotBlank
        private String phoneNumber;
        @NotBlank
        private String businessNo;
        @NotNull
        private Role role;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class requestLoginDto{
        private String userId;
        private String pwd;
    }
}
