/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import dto.PostDTO;
import dto.UserDTO;
import entities.Post;
import entities.Role;
import entities.User;
import facades.PostFacade;
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
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author jacobsimonsen
 */
public class PostResourceTest {

    private static EntityManagerFactory emf;
    private static PostFacade facade;
    User user;
    User admin;
    User mod;
    User both;
    Role userRole;
    Role adminRole;
    Role modRole;
    Post post1;
    Post post2;
    Post post3;

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
            em.createNamedQuery("Post.deleteAllRows").executeUpdate();
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

            post1 = new Post("fuck hvor jeg hader tests");
            post2 = new Post("hej med dig");
            post3 = new Post("hva laver du?");

            em.getTransaction().begin();
            userRole = new Role("user");
            modRole = new Role("mod");
            adminRole = new Role("admin");
            user.addRole(userRole);
            admin.addRole(adminRole);
            mod.addRole(modRole);
            both.addRole(userRole);
            both.addRole(adminRole);
            user.addPost(post1);
            mod.addPost(post2);
            admin.addPost(post3);

            em.createNamedQuery("Post.deleteAllRows").executeUpdate();
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
    public void testAddPost() throws Exception {
        login("user", "hello");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body("{\n"
                        + "   \"content\" : \"heeej\"\n"
                        + "}")
                .when()
                .post("posts/user")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("content", equalTo("heeej"));
    }
 @Test
    public void testEditPost() throws Exception {
        login("user", "hello");
        PostDTO pDTO = new PostDTO(post1);
        pDTO.setContent("hejjj");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(pDTO)
                .when()
                .put("posts/" + user.getUserName())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("content", equalTo("hejjj" + " (<i>edited</i>)"));

    }
@Test
    public void testDeletePost() throws Exception {
        login("user", "hello");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .delete("posts/" + post1.getId() + "/users/" + user.getUserName() )
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode());
        List<PostDTO> postsDTO;
        postsDTO = given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/posts/all").then()
                .extract().body().jsonPath().getList("all", PostDTO.class);

        assertThat(postsDTO, iterableWithSize(2));
    }
  @Test
    public void testGetAllUsers() throws Exception {

        login("user", "hello");
        List<PostDTO> postsDTO;
        postsDTO = given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/posts/all").then()
                .extract().body().jsonPath().getList("all", PostDTO.class);

        assertThat(postsDTO, iterableWithSize(3));
    }
}
