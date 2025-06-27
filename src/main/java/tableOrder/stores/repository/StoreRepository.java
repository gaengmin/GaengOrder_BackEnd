package tableOrder.stores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tableOrder.stores.entity.Stores;

@Repository
public interface StoreRepository extends JpaRepository<Stores,Long> {

}
