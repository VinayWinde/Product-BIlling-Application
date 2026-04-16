package Mapper;

import DTO.BillItemResponseDTO;
import DTO.BillRequestDTO;
import DTO.BillResponseDTO;
import Entity.Bill;
import Entity.BillItem;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BillingMapper {

    public static Bill toEntity(BillRequestDTO dto) {

        Bill bill = new Bill();
        bill.setClientId(dto.getClientId());
        bill.setClientName(dto.getClientName());

        List<BillItem> items = dto.getItems().stream().map(i -> {

            BillItem item = new BillItem();
            item.setProductId(i.getProductId());
            item.setProductName(i.getProductName());
            item.setPrice(i.getPrice());
            item.setQuantity(i.getQuantity());
            item.setBill(bill);

            return item;

        }).toList();

        bill.setItems(items);

        return bill;
    }

    public BillResponseDTO toDTO(Bill bill, Object clientDetails) {
        return BillResponseDTO.builder()
                .billid(bill.getBillId())
                .clientId(bill.getClientId())
                .clientName(bill.getClientName())
                .totalAmount(bill.getTotalAmount())
                .gstAmount(bill.getGstAmount())
                .billDate(bill.getBillDate())
                .items(bill.getItems().stream().map(this::mapItem).toList())
                .clientDetails(clientDetails) // 🔥 This is the key
                .build();
    }

    private BillItemResponseDTO mapItem(BillItem item) {
        return BillItemResponseDTO.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .build();
    }
}