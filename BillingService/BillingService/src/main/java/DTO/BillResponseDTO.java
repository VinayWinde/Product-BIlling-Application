package DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillResponseDTO {
    private Long billid;
    private Long clientId;

    // 🔥 Changed from String to Object/JsonNode
    // This can hold name, email, phone, etc., dynamically
    private Object clientDetails;
    private String clientName;
    private BigDecimal totalAmount;
    private BigDecimal gstAmount;
    private LocalDateTime billDate;
    private List<BillItemResponseDTO> items;
}