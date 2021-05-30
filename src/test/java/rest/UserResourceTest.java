/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import dto.UserDTO;
import entities.Role;
import entities.User;
import facades.UserFacade;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author Acer
 */
public class UserResourceTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;
    User user;
    User admin;
    User mod;
    User both;
    Role userRole;
    Role adminRole;
    Role modRole;

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
            user = new User("user", "hello", "user@mail.dk", "20", "hello.jpg");
            admin = new User("admin", "with", "admin@mail.dk", "30", "hej.jpg");
            mod = new User("mod", "witthh", "mod@mail.dk", "30", "heeeej.jpg");
            both = new User("user_admin", "you", "both@mail.dk", "40", "hi.jpg");

            em.getTransaction().begin();
            userRole = new Role("user");
            modRole = new Role("mod");
            adminRole = new Role("admin");
            user.addRole(userRole);
            admin.addRole(adminRole);
            mod.addRole(modRole);
            both.addRole(userRole);
            both.addRole(adminRole);

            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();

            em.persist(userRole);
            em.persist(adminRole);
            em.persist(modRole);
            em.persist(user);
            em.persist(admin);
            em.persist(mod);
            em.persist(both);

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
    public void testAddUser() throws Exception {

        given()
                .contentType("application/json")
                .body(new UserDTO("test", "1234", "test@mail.dk", "14", "hej.jpg"))
                .when()
                .post("users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("name", equalTo("test"))
                .body("email", equalTo("test@mail.dk"))
                .body("age", equalTo("14"))
                .body("profilePicPath", equalTo("hej.jpg"));
    }

    @Test
    public void testEditUser() throws Exception {
        login("user", "hello");
        UserDTO uDTO = new UserDTO("user", "1234", "test@mail.dk", "22", "hva.jpg");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(uDTO)
                .when()
                .put("users/" + user.getUserName())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("name", equalTo("user"))
                .body("email", equalTo("test@mail.dk"))
                .body("age", equalTo("22"))
                .body("profilePicPath", equalTo("hva.jpg"));

    }

    @Test
    public void testDeleteUser() throws Exception {
        login("admin", "with");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .delete("users/" + user.getUserName())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
        List<UserDTO> usersDTO;
        usersDTO = given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/users/all").then()
                .extract().body().jsonPath().getList("all", UserDTO.class);

        assertThat(usersDTO, iterableWithSize(3));
    }

    @Test
    public void testGetAllUsers() throws Exception {

        login("admin", "with");
        List<UserDTO> usersDTO;
        usersDTO = given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/users/all").then()
                .extract().body().jsonPath().getList("all", UserDTO.class);

        assertThat(usersDTO, iterableWithSize(4));
    }
}
