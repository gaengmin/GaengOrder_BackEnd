package tableOrder.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tableOrder.users.dto.enums.Role;
import tableOrder.users.entity.Users;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Boolean existsByRoleAndStoreNo(Role role, Long storeNo);
    Boolean existsByUserId(String userId);
    Optional<Users> findByUserId(String userId);



    Users save(Users users);

}
