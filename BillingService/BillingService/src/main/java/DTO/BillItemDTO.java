package DTO;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillItemDTO {

    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
}