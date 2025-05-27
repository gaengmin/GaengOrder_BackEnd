package tableOrder.users.repository;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tableOrder.users.dto.enums.Role;
import tableOrder.users.dto.request.RequestUsersDto;
import tableOrder.users.dto.response.ResponseUsersDto;
import tableOrder.users.entity.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Boolean existsByRoleAndStoreNo(Role role, Long storeNo);

    Boolean existsByUserId(String userId);

    Optional<Users> findByUserId(String userId);


}
