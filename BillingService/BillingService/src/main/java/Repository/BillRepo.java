package Repository;

import Entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BillRepo extends JpaRepository<Bill,Long> {

    List<Bill> findByCustomerId(Long clientId);

    List<Bill> findByClientIdAndBillDateBetween(
            Long clientId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<Bill> findByClientIdOrderByBillDateDesc(Long clientId);

    List<Bill> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    List<Bill> findByClientId(Long customerId);

    List<Bill> findByBillDateBetween(LocalDateTime from, LocalDateTime to);
}
