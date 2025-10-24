package fsa.fresher.pos.product;

import fsa.fresher.pos.api.dto.product.ProductCreateDto;
import fsa.fresher.pos.api.dto.product.ProductDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    public ProductController() {}

    @PostMapping("")
    public ProductDto createProduct(@RequestBody ProductCreateDto dto) {
        return null;
    }
}
