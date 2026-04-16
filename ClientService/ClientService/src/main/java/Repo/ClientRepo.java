package Repo;

import Entity_clients.ClientEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepo extends JpaRepository<ClientEntity,Long> {

    boolean existsByEmail(String email);

    List<ClientEntity> findByNameStartingWithIgnoreCase(String name);
}
