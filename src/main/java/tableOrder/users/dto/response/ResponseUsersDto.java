package tableOrder.users.dto.response;

import tableOrder.users.dto.enums.Role;

public class ResponseUsersDto {
    public static class StoresEmployeeDto{
        private Long userNo;
        private String storeName;
        private Long userId;
        private String name;
        private String phoneNumber;
        private Role role;
    }
}
