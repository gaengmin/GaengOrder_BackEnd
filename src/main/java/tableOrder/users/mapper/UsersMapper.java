package tableOrder.users.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tableOrder.users.dto.response.ResponseUsersDto;

import java.util.List;

@Mapper
public interface UsersMapper {
    @Select("""
            SELECT
                 u.user_id as userId,
                 s.store_name AS storeName,
                 u.name as name,
                 u.phone_number as phoneNumber,
                 u.role as role
             FROM
                 users u
             INNER JOIN 
                 stores s ON s.store_no = u.store_no
             WHERE
                 u.user_id = #{userId}
            """)
    ResponseUsersDto.UsersDto getUserData(@Param("userId") String userId, @Param("userStoreNo") Long userStoreNo);

    @Select("""
            SELECT
                 u.user_id as userId,
                 s.store_name AS storeName,
                 u.name as name,
                 u.phone_number as phoneNumber,
                 u.role as role
             FROM
                 users u
             INNER JOIN 
                 stores s ON s.store_no = u.store_no
             WHERE
                u.store_no = #{userStoreNo}
            """)
    List<ResponseUsersDto.UsersDto> getUserList(@Param("userStoreNo")Long userStoreNo);
}
