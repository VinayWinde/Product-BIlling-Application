package FeignCalls;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "CUSTOMER-SERVICE", url="http://localhost:2002/api/clients")
public interface ClientClient {

    @GetMapping("/{id}")
    JsonNode getCustomerById(@PathVariable("id") Long id);


}