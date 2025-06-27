package tableOrder.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tableOrder.stores.dto.ResponseDto.ResponseStoresDto;
import tableOrder.stores.entity.Stores;
import tableOrder.users.dto.enums.Role;
import tableOrder.users.entity.Users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResponseUsersDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StoresEmployeeDto {
        private String userId;
        private String storeName;
        private String name;
        private String phoneNumber;
        private Role role;

        public static StoresEmployeeDto from(UsersDto user) {
            return StoresEmployeeDto.builder()
                    .userId(user.getUserId())
                    .storeName(user.getStoreName())       // 사업자 번호
                    .name(user.getName())           // 매장 전화번호
                    .phoneNumber(user.getPhoneNumber())   // 매장 주소
                    .role(user.getRole())
                    .build();
        }
        // List<UsersDto> -> List<StoresEmployeeDto> 변환
        public static List<StoresEmployeeDto> from(List<UsersDto> usersList) {
            if (usersList == null || usersList.isEmpty()) {
                return Collections.emptyList(); // 데이터가 없으면 빈 리스트 반환
            }

            List<StoresEmployeeDto> storesEmployeeList = new ArrayList<>();
            for (UsersDto user : usersList) {
                StoresEmployeeDto storeEmployee = StoresEmployeeDto.from(user); // 단일 변환
                storesEmployeeList.add(storeEmployee);  // 결과 리스트에 추가
            }

            return storesEmployeeList; // 변환된 리스트 반환
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UsersDto {
        private String userId;
        private String storeName;
        private String name;
        private String phoneNumber;
        private Role role;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UsersData {
        private String storeName;
        private String userId;
        private String name;
        private String phoneNumber;
        private Role role;

        public static ResponseUsersDto.UsersData from(UsersDto user) {
            return UsersData.builder()
                    .userId(user.getUserId())         // 매장 이름
                    .storeName(user.getStoreName())       // 사업자 번호
                    .name(user.getName())           // 매장 전화번호
                    .phoneNumber(user.getPhoneNumber())   // 매장 주소
                    .role(user.getRole())
                    .build();
        }
    }
}
