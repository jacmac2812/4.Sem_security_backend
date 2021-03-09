/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CategoryDTO;
import dto.OnSaleDTO;
import dto.ProductDTO;
import dto.ProductsDTO;
import dto.SearchDTO;
import dto.UserDTO;
import facades.FavoritFacade;
import fetchers.ProductFetcher;
import java.io.IOException;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import utils.EMF_Creator;

/**
 *
 * @author jacobsimonsen
 */
@Path("products")
public class ProductResource {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    private static final FavoritFacade FACADE = FavoritFacade.getFavoritFacade(EMF);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{search}")
    public String getProduct(@PathParam("search") String search) throws IOException {
        ProductFetcher pf = new ProductFetcher();

        SearchDTO sDTO = pf.getProduct(search);

        return GSON.toJson(sDTO);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("categories/{category}")
    public String getProducts(@PathParam("category") String category) throws IOException {
        ProductFetcher pf = new ProductFetcher();

        CategoryDTO cDTO = pf.getProducts(category);

        return GSON.toJson(cDTO);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("offers")
    public String getProductsOnSale() throws IOException {
        ProductFetcher pf = new ProductFetcher();

        OnSaleDTO osDTO = pf.getProductsOnSale();

        return GSON.toJson(osDTO);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("favorites/{user}")
    public String addFavorit(@PathParam("user") String user, String product) {
        ProductDTO pDTO = GSON.fromJson(product, ProductDTO.class);
        ProductDTO pAdded = FACADE.addFavorit(pDTO, user);
        return GSON.toJson(pAdded);
    }

    @Path("favorites/{user}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllFavorites(@PathParam("user") String user) {
        ProductsDTO psDTO = FACADE.getAllFavorites(user);
        return GSON.toJson(psDTO);
    }

    @Path("favorites/{sku}/users/{user}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteFavorit(@PathParam("user") String user, @PathParam("sku") String sku) {
        String s = FACADE.deleteFavorit(sku, user);
        return GSON.toJson(s);
    }
}
