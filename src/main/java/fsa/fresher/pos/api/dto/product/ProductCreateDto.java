package fsa.fresher.pos.api.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductCreateDto {
    private String code;
    private String name;
    private Double price;
    private String description;
}
