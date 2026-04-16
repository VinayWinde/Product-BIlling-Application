package Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    private Long productId;

    private String productName;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;

    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        this.totalPrice = price.multiply(BigDecimal.valueOf(quantity));
    }
}