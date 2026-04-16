package Controller;


import DTO.ClientRequestDTO;
import DTO.ClientResponseDTO;
import Service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ClientController {

    private final ClientService clientService;

    // ✅ CREATE CUSTOMER
    @PostMapping
    public ResponseEntity<ClientResponseDTO> createCustomer(@Valid @RequestBody ClientRequestDTO request) {
        ClientResponseDTO response = clientService.createCustomer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ✅ GET ALL CUSTOMERS
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllCustomers() {
        return ResponseEntity.ok(clientService.getCustomers());
    }

    // ✅ GET CUSTOMER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getCustomerById(id));
    }

    // ✅ SEARCH CUSTOMERS BY NAME (Prefix Search)
    @GetMapping("/search")
    public ResponseEntity<List<ClientResponseDTO>> getCustomerByName(@RequestParam String name) {
        return ResponseEntity.ok(clientService.getCustomerByName(name));
    }

    // ✅ UPDATE CUSTOMER
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDTO request) {
        return ResponseEntity.ok(clientService.updateCustomer(id, request));
    }

    // ✅ DELETE CUSTOMER
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.deleteCustomer(id));
    }

    // 🚀 EXCEL UPLOAD
    @PostMapping("/upload-excel")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            clientService.saveFromExcel(file);
            return ResponseEntity.ok("Excel data imported successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading excel: " + e.getMessage());
        }
    }

    // 🚀 EXCEL DOWNLOAD
    @GetMapping("/download-excel")
    public ResponseEntity<byte[]> downloadExcel() {
        try {
            byte[] data = clientService.generateExcel();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clients_report.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/gethistory")
    public ResponseEntity<List<Object>> purchasehistory(Long customerID){
        List<Object> ph=clientService.getPurchaseHistory(customerID);
        return ResponseEntity.ok(ph);
    }

}