package Repository;

import Entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillingItemRepo extends JpaRepository<BillItem,Long> {

    List<BillItem> findByBillId(Long billid);

    List<BillItem> findByProductId(Long productId);

}
