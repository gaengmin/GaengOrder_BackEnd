package tableOrder.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tableOrder.common.enums.SoftDelete;
import tableOrder.users.dto.enums.Role;

public class RequestUsersDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class updateUsersDto{
        @NotBlank
        @Size(min = 8, max = 30)
        private String pwd;
        @NotBlank
        @Pattern(
                regexp = "^01([0|1|6|7|8|9])[-]?(\\d{3,4})[-]?(\\d{4})$",
                message = "올바른 휴대폰 번호 형식(010-1234-5678, 01012345678 등)이어야 합니다."
        )
        private String phoneNumber;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class requestAdminJoinDto{
        @NotBlank
        private String userId;
        @NotBlank
        @Size(min = 8, max = 30)
        private String pwd;
        @NotBlank
        private String name;
        @NotBlank
        @Pattern(
                regexp = "^01([0|1|6|7|8|9])[-]?(\\d{3,4})[-]?(\\d{4})$",
                message = "올바른 휴대폰 번호 형식(010-1234-5678, 01012345678 등)이어야 합니다."
        )
        private String phoneNumber;
        @NotBlank
        private String businessNo; //사업자 번호
        private Role role;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class requestOrdersJoinDto{
        @NotBlank
        private String userId;
        @NotBlank
        @Size(min = 8, max = 30)
        private String pwd;
        @NotBlank
        private String name;
        @NotBlank
        @Pattern(
                regexp = "^01([0|1|6|7|8|9])[-]?(\\d{3,4})[-]?(\\d{4})$",
                message = "올바른 휴대폰 번호 형식(010-1234-5678, 01012345678 등)이어야 합니다."
        )
        private String phoneNumber;
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
