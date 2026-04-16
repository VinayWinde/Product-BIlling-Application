package Service;

import DTO.*;
import Entity.Bill;
import Entity.BillItem;
import FeignCalls.ClientClient;
import FeignCalls.ProductClient;
import Mapper.BillingMapper;
import Repository.BillRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BillServiceImpl implements BillService {

    private final BillRepo billingRepository;
    private final ProductClient productClient;
    private final ClientClient clientClient;
    private final BillingMapper billingMapper;

    // In-memory cart storage
    private final Map<Long, List<CartItemDTO>> carts = new HashMap<>();

    @Override
    public BillResponseDTO createBill(BillRequestDTO request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty");
        }

        // 1. Get Client Info via Feign
        var clientInfo = clientClient.getCustomerById(request.getClientId());

        // 2. Map to Entity
        Bill billing = BillingMapper.toEntity(request);
        billing.setClientName(clientInfo.get("name").asText());

        // 3. Process Items & Totals
        processBillItems(billing, request.getItems());

        // 4. Save and Return
        Bill saved = billingRepository.save(billing);
        return billingMapper.toDTO(saved, clientInfo);
    }

    @Override
    public BillResponseDTO checkout(Long customerId, CheckoutDTO dto) {
        List<CartItemDTO> cartItems = carts.get(customerId);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Convert CartItemDTO to BillItemDTO
        List<BillItemDTO> billItems = cartItems.stream().map(cartItem -> {
            BillItemDTO item = new BillItemDTO();
            item.setProductId(cartItem.getProductId());
            item.setQuantity(cartItem.getQuantity());
            return item;
        }).toList();

        BillRequestDTO request = new BillRequestDTO();
        request.setClientId(customerId);
        request.setItems(billItems);

        BillResponseDTO response = createBill(request);
        clearCart(customerId);

        return response;
    }
    @Override
    public List<BillResponseDTO> getBillsByCustomer(Long customerId) {
        // 1. Fetch all bill entities for this customer
        List<Bill> bills = billingRepository.findByClientId(customerId);

        // 2. Map them to DTOs
        // Note: Since this is a list, we fetch the clientInfo once to avoid
        // calling the Feign client inside a loop (which is slow).
        var clientInfo = clientClient.getCustomerById(customerId);

        return bills.stream()
                .map(bill -> billingMapper.toDTO(bill, clientInfo))
                .toList();
    }

    @Override
    public List<BillResponseDTO> getBillsByDateRange(LocalDateTime from, LocalDateTime to) {
        // 1. Fetch bills within the date range
        List<Bill> bills = billingRepository.findByBillDateBetween(from, to);

        // 2. Map to DTOs
        return bills.stream()
                .map(bill -> {
                    // Fetch client info for each bill since they might be different customers
                    var clientInfo = clientClient.getCustomerById(bill.getClientId());
                    return billingMapper.toDTO(bill, clientInfo);
                })
                .toList();
    }

    private void processBillItems(Bill bill, List<BillItemDTO> dtoList) {
        List<BillItem> items = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;

        for (BillItemDTO dto : dtoList) {
            var product = productClient.getProductById(dto.getProductId());
            if (product == null) throw new RuntimeException("Product not found");

            BillItem item = new BillItem();
            item.setProductId(product.getId());
            item.setProductName(product.getProductName());
            item.setPrice(product.getPrice());
            item.setQuantity(dto.getQuantity());
            item.setBill(bill);

            subTotal = subTotal.add(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
            items.add(item);
        }

        bill.setItems(items);
        BigDecimal gst = calculateGST(subTotal);
        bill.setGstAmount(gst);
        bill.setTotalAmount(subTotal.add(gst));
    }

    @Override
    public BillResponseDTO getBillById(Long id) {
        Bill bill = billingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
        var clientInfo = clientClient.getCustomerById(bill.getClientId());
        return billingMapper.toDTO(bill, clientInfo);
    }

    private BigDecimal calculateGST(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(0.18));
    }

    // ================= CART METHODS =================

    @Override
    public void addToCart(Long customerId, CartItemDTO dto) {
        carts.putIfAbsent(customerId, new ArrayList<>());
        List<CartItemDTO> cart = carts.get(customerId);

        Optional<CartItemDTO> existing = cart.stream()
                .filter(p -> p.getProductId().equals(dto.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            CartItemDTO item = existing.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
        } else {
            cart.add(dto);
        }
    }

    @Override
    public void updateCart(Long customerId, CartItemDTO dto) {
        List<CartItemDTO> cart = carts.get(customerId);
        if (cart != null) {
            cart.stream()
                    .filter(p -> p.getProductId().equals(dto.getProductId()))
                    .findFirst()
                    .ifPresent(item -> item.setQuantity(dto.getQuantity()));
        }
    }

    @Override
    public void removeFromCart(Long customerId, Long productId) {
        List<CartItemDTO> cart = carts.get(customerId);
        if (cart != null) {
            cart.removeIf(p -> p.getProductId().equals(productId));
        }
    }

    @Override
    public List<CartItemDTO> getCart(Long customerId) {
        return carts.getOrDefault(customerId, new ArrayList<>());
    }

    @Override
    public void clearCart(Long customerId) {
        carts.remove(customerId);
    }
} // <--- Only one final closing bracket for the class!