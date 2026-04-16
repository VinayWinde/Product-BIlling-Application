package FeignCalls;

import DTO.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "PRODUCT-SERVICE",   // MUST match Eureka service name
        path = "http://localhost:2000/api/products"      // base path of controller
)
public interface ProductClient {

    @GetMapping("/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);

    @GetMapping
    List<ProductDTO> getAllProducts();
}