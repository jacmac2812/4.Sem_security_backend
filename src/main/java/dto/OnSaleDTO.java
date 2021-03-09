package dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Acer
 */
public class OnSaleDTO {

    List<ProductDTO> products = new ArrayList();

    public OnSaleDTO(List<ProductDTO> productsDTO) {
        this.products = productsDTO;
    }

    public List<ProductDTO> getAll() {
        return products;
    }

}
