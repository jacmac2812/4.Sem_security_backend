/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fetchers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CategoryDTO;
import dto.OnSaleDTO;
import dto.ProductDTO;
import dto.SearchDTO;
import java.io.IOException;
import utils.HttpUtils;

/**
 *
 * @author jacobsimonsen
 */
public class ProductFetcher {

    private static final String api = "QOyoGlU9mCnZjZJ31CKHO18N";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public SearchDTO getProduct(String search) throws IOException {
        String product = HttpUtils.fetchData("https://api.bestbuy.com/v1/products(search=" + search + ")?format=json&pageSize=100&apiKey=" + api);

        SearchDTO sDTO = GSON.fromJson(product, SearchDTO.class);

        return sDTO;
    }

    public CategoryDTO getProducts(String category) throws IOException {
        String products = HttpUtils.fetchData("https://api.bestbuy.com/v1/products(categoryPath.id%20=" + category + ")?format=json&pageSize=100&apiKey=" + api);

        CategoryDTO categoryDTO = GSON.fromJson(products, CategoryDTO.class);

        return categoryDTO;
    }
    
    public OnSaleDTO getProductsOnSale() throws IOException {
        String products = HttpUtils.fetchData("https://api.bestbuy.com/v1/products(onSale=true)?format=json&pageSize=100&apiKey=" + api);

        OnSaleDTO onSaleDTO = GSON.fromJson(products, OnSaleDTO.class);

        return onSaleDTO;
    }
}
