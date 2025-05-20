package tableOrder.tables.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tableOrder.common.enums.SoftDelete;
import tableOrder.tables.entity.Tables;

@Repository
public interface TablesRepository extends JpaRepository<Tables, Long> {

    boolean existsByTableCodeAndStoreNoAndSoftDelete(String tableCode, Long storeNo, SoftDelete softDelete);

    boolean existsByTableCodeAndStoreNoAndSoftDeleteAndTableNoNot(String tableCode, Long userStoreNo, SoftDelete softDelete, Long tableNo);
}
