package Service;

import DTO.BillRequestDTO;
import DTO.BillResponseDTO;
import DTO.CartItemDTO;
import DTO.CheckoutDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface BillService {

    // ✅ Creation & Checkout
    BillResponseDTO createBill(BillRequestDTO request);

    BillResponseDTO checkout(Long customerId, CheckoutDTO dto);

    // ✅ Retrieval & History
    BillResponseDTO getBillById(Long id);

    List<BillResponseDTO> getBillsByCustomer(Long customerId);

    List<BillResponseDTO> getBillsByDateRange(
            LocalDateTime from,
            LocalDateTime to
    );

    // ✅ Cart Management
    void addToCart(Long customerId, CartItemDTO dto);

    void updateCart(Long customerId, CartItemDTO dto);

    void removeFromCart(Long customerId, Long productId);

    List<CartItemDTO> getCart(Long customerId);

    void clearCart(Long customerId);
}