package DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRequestDTO {

    @NotBlank(message = "Client name is mandatory")
    private String name;

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email is mandatory")
    private String email;

    private String phone;
    private String address;

    @Builder.Default
    private Boolean isActive = true;
}
