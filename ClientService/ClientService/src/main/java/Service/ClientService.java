package Service;

import DTO.ClientRequestDTO;
import DTO.ClientResponseDTO;
import Entity_clients.ClientEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    ClientResponseDTO createCustomer(ClientRequestDTO clientRequestDTO);

    List<ClientResponseDTO> getCustomerByName(String name);

    List<ClientResponseDTO> getCustomers();

    ClientResponseDTO updateCustomer(Long clientId, ClientRequestDTO dto);

    ClientResponseDTO getCustomerById(Long clientId);

    String deleteCustomer(Long clientId);

    void saveFromExcel(MultipartFile file) throws Exception;

    byte[] generateExcel() throws Exception;

    List<Object> getPurchaseHistory( Long customerId);
}
