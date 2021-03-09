/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Favorit;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jacobsimonsen
 */
public class ProductsDTO {
     List<ProductDTO> all = new ArrayList();

    public ProductsDTO(List<Favorit> favoritEntities) {
        favoritEntities.forEach((f) -> {
            all.add(new ProductDTO(f));
        });
    }

    public List<ProductDTO> getAll() {
        return all;
    }
}
