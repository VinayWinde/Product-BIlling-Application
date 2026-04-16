package Controller;

import DTO.*;
import Service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BillingController {

    private final BillService billingService;

    @PostMapping
    public ResponseEntity<BillResponseDTO> createBill(@RequestBody @Valid BillRequestDTO request) {
        BillResponseDTO response = billingService.createBill(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillResponseDTO> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getBillById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BillResponseDTO>> getCustomerBills(@PathVariable Long customerId) {
        return ResponseEntity.ok(billingService.getBillsByCustomer(customerId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<BillResponseDTO>> getBillsByDateRange(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(billingService.getBillsByDateRange(from, to));
    }

    // ================= CART OPERATIONS =================

    @PostMapping("/cart/add")
    public ResponseEntity<String> addToCart(
            @RequestParam Long customerId,
            @RequestBody CartItemDTO dto) {
        billingService.addToCart(customerId, dto);
        return ResponseEntity.ok("Product added to cart");
    }

    @PutMapping("/cart/update")
    public ResponseEntity<String> updateCart(
            @RequestParam Long customerId,
            @RequestBody CartItemDTO dto) {
        billingService.updateCart(customerId, dto);
        return ResponseEntity.ok("Cart updated");
    }

    @DeleteMapping("/cart/remove/{productId}")
    public ResponseEntity<String> removeFromCart(
            @RequestParam Long customerId,
            @PathVariable Long productId) {
        billingService.removeFromCart(customerId, productId);
        return ResponseEntity.ok("Item removed");
    }

    @GetMapping("/cart")
    public ResponseEntity<List<CartItemDTO>> getCart(@RequestParam Long customerId) {
        return ResponseEntity.ok(billingService.getCart(customerId));
    }

    @DeleteMapping("/cart/clear")
    public ResponseEntity<String> clearCart(@RequestParam Long customerId) {
        billingService.clearCart(customerId);
        return ResponseEntity.ok("Cart cleared");
    }

    @PostMapping("/checkout")
    public ResponseEntity<BillResponseDTO> checkout(
            @RequestParam Long customerId,
            @RequestBody CheckoutDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(billingService.checkout(customerId, dto));
    }
} // Final closing brace for the class