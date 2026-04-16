package Entity_clients;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "clients",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_client_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_client_email", columnList = "email"),
                @Index(name = "idx_client_phone", columnList = "phone")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    // 🔹 Basic Info
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 15)
    private String phone;

    @Column(length = 255)
    private String address;

    // 🔹 Status (future use: active/inactive/block)
    @Column(nullable = false)
    private Boolean isActive = true;

    // 🔹 Purchase relationship (history)
    @OneToMany(
            mappedBy = "client",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<Object> purchases = new ArrayList<>();

    // 🔹 Audit fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 🔹 Lifecycle hooks
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}