package Feingclientcalls;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "billing-service", url = "http://localhost:8082/api/billing")
public interface BillingClient {

    @GetMapping("/customer/{customerId}")
    List<Object> getPurchaseHistory(@PathVariable("customerId") Long customerId);
}