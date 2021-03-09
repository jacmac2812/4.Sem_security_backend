/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import dto.ProductDTO;
import dto.UserDTO;
import entities.Favorit;
import entities.Role;
import entities.User;
import facades.FavoritFacade;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author Acer
 */
public class ProductResourceTest {

    private static EntityManagerFactory emf;
    private static FavoritFacade facade;
    User user;
    User admin;
    User both;
    Role userRole;
    Role adminRole;
    Favorit fav1;
    Favorit fav2;

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    //Utility method to login and set the returned securityToken
    private static void login(String role, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", role, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/login")
                .then()
                .extract().path("token");
        //System.out.println("TOKEN ---> " + securityToken);
    }

    private void logOut() {
        securityToken = null;
    }

    @BeforeAll
    public static void setUpClass() throws IOException {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        httpServer.start();
        while (!httpServer.isStarted()) {
        }
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.createNamedQuery("Favorit.deleteAllRows").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        //System.in.read();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            user = new User("user", "hello", "user@mail.dk", "12345678");

            em.getTransaction().begin();
            userRole = new Role("user");
            user.addRole(userRole);
            fav1 = new Favorit("1", "tv", "hardgood", "50", "5", "tvs.dk", "tv.jpg", "very true");
            user.addFavorit(fav1);
            fav2 = new Favorit("2", "radio", "hardgood", "36", "7", "tvs.dk/radio", "radio.jpg", "true");
            user.addFavorit(fav2);

            em.createNamedQuery("Favorit.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();

            em.persist(userRole);
            em.persist(user);
            em.persist(fav1);
            em.persist(fav2);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    @Test
    public void testGetAllFavorites() throws Exception {

        List<ProductDTO> productsDTO;
        productsDTO = given()
                .contentType("application/json")
                .when()
                .get("/products/favorites/" + user.getUserName()).then()
                .extract().body().jsonPath().getList("all", ProductDTO.class);

        assertThat(productsDTO, iterableWithSize(2));
    }

    @Test
    public void testAddFavorit() throws Exception {

        Favorit fav3 = new Favorit("3", "ovn", "hardgood", "200", "200", "tvs.dk/oven", "oven.jpg", "false");
        ProductDTO pDTO = new ProductDTO(fav3);
        given()
                .contentType("application/json")
                .body(pDTO)
                .when()
                .post("/products/favorites/" + user.getUserName())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("sku", equalTo("3"))
                .body("name", equalTo("ovn"))
                .body("type", equalTo("hardgood"));
        
        List<ProductDTO> productsDTO;
        productsDTO = given()
                .contentType("application/json")
                .when()
                .get("/products/favorites/" + user.getUserName()).then()
                .extract().body().jsonPath().getList("all", ProductDTO.class);

        assertThat(productsDTO, iterableWithSize(3));
    }
    
     @Test
    public void testDeleteFavorit() throws Exception {
        given()
                .contentType("application/json")
                .when()
                .delete("/products/favorites/" + fav1.getSku() + "/users/" + user.getUserName())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
        
        List<ProductDTO> productsDTO;
        productsDTO = given()
                .contentType("application/json")
                .when()
                .get("/products/favorites/" + user.getUserName()).then()
                .extract().body().jsonPath().getList("all", ProductDTO.class);


        assertThat(productsDTO, iterableWithSize(1));
    }
}
