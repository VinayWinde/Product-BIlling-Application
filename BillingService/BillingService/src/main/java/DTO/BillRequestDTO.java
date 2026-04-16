package DTO;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillRequestDTO {

    private Long clientId;
    private String clientName;

    private List<BillItemDTO> items;
}