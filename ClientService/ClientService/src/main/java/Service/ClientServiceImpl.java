package Service;


import DTO.ClientRequestDTO;
import DTO.ClientResponseDTO;
import Entity_clients.ClientEntity;
import Feingclientcalls.BillingClient;
import Mapper.ClientMapper;
import Repo.ClientRepo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepo clientRepo;
    private final ClientMapper clientMapper;
    private final BillingClient billingClient;

    public ClientResponseDTO createCustomer(ClientRequestDTO clientRequestDTO) {
        // 1. Safety check
        if (clientRequestDTO == null) {
            throw new IllegalArgumentException("Client data cannot be empty");
        }

        // 2. Business Logic: Check if email already exists
        if (clientRepo.existsByEmail(clientRequestDTO.getEmail())) {
            throw new RuntimeException("Email " + clientRequestDTO.getEmail() + " is already registered");
        }

        // 3. Mapping and Saving
        ClientEntity e = clientMapper.toEntity(clientRequestDTO);
        ClientEntity returnClient = clientRepo.save(e);

        // 4. Return the Response
        return clientMapper.toDTO(returnClient);
    }

    @Override
    // 1. Changed return type to List<ClientResponseDTO>
    public List<ClientResponseDTO> getCustomerByName(String name) {
        // 2. Return an empty List instead of a new ArrayList (cleaner)
        if (name == null || name.trim().isEmpty()) {
            return List.of();
        }

        return clientRepo.findByNameStartingWithIgnoreCase(name)
                .stream()
                .map(clientMapper::toDTO)
                .toList(); // This returns a List, which matches the new signature
    }

    @Override
    public ClientResponseDTO getCustomerById(Long clientId) {
        return clientRepo.findById(clientId)
                .map(clientMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + clientId));
    }

    @Override
    public ClientResponseDTO updateCustomer(Long clientId, ClientRequestDTO dto) {
        ClientEntity existingClient = clientRepo.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Update fields from DTO to Entity
        existingClient.setName(dto.getName());
        existingClient.setEmail(dto.getEmail());
        existingClient.setPhone(dto.getPhone());
        existingClient.setAddress(dto.getAddress());
        existingClient.setIsActive(dto.getIsActive());

        ClientEntity updated = clientRepo.save(existingClient);
        return clientMapper.toDTO(updated);
    }

    @Override
    public String deleteCustomer(Long clientId) {
        if (!clientRepo.existsById(clientId)) {
            throw new RuntimeException("Customer not found");
        }
        clientRepo.deleteById(clientId);
        return "Customer deleted successfully with ID: " + clientId;
    }

    // 🔥 EXCEL UPLOAD LOGIC
    @Override
    public void saveFromExcel(MultipartFile file) throws Exception {
        List<ClientEntity> clients = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                ClientEntity client = ClientEntity.builder()
                        .name(getCellValue(row.getCell(0)))
                        .email(getCellValue(row.getCell(1)))
                        .phone(getCellValue(row.getCell(2)))
                        .address(getCellValue(row.getCell(3)))
                        .isActive(true)
                        .build();
                clients.add(client);
            }
            clientRepo.saveAll(clients);
        }
    }

    // 🔥 EXCEL DOWNLOAD LOGIC
    @Override
    public byte[] generateExcel() throws Exception {
        List<ClientEntity> clients = clientRepo.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Customers");
            String[] headers = {"ID", "Name", "Email", "Phone", "Address"};

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Create Data Rows
            int rowIdx = 1;
            for (ClientEntity client : clients) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(client.getClientId());
                row.createCell(1).setCellValue(client.getName());
                row.createCell(2).setCellValue(client.getEmail());
                row.createCell(3).setCellValue(client.getPhone());
                row.createCell(4).setCellValue(client.getAddress());
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // Helper method to handle cell types safely
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }

    public List<ClientResponseDTO> getCustomers() {
        List<ClientEntity> entities = clientRepo.findAll();

        // Map each entity to a DTO and collect them into a new list
        return entities.stream()
                .map(clientMapper::toDTO)
                .toList();
    }

    public List<Object> getPurchaseHistory(Long customerId){
        return billingClient.getPurchaseHistory(customerId);
    }


}
