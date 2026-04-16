package DTO;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseDTO {

    private Long clientId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Boolean isActive;

    // List of simplified purchase records
    private List<Object> purchases;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}