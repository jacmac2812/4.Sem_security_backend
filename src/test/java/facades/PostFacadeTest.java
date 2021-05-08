/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.PostDTO;
import dto.UserDTO;
import entities.Post;
import entities.Role;
import entities.User;
import errorhandling.MissingInputException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author jacobsimonsen
 */
public class PostFacadeTest {

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

    public PostFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PostFacade.getPostFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
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
    public void testGetAllPosts() {
        assertEquals(3, facade.getAllPosts().getAll().size(), "Expect three posts");
    }

    @Test
    public void testGetAllUserPosts() {
        assertEquals(1, facade.getAllPostsUser(user.getUserName()).getAll().size(), "Expect one post from user");
    }

    @Test
    public void testAddPost() throws MissingInputException {
        PostDTO pDTO = facade.createPost("heeeeeeeeeeeeeeej", admin.getUserName());
        assertEquals("heeeeeeeeeeeeeeej", pDTO.getContent(), "Expect the same name");
        assertEquals(4, facade.getAllPosts().getAll().size(), "Excepts four persons");
    }

    @Test
    public void testAddPostException() {
        try {
            PostDTO pDTO = facade.createPost("", admin.getUserName());
        } catch (MissingInputException ex) {
            assertEquals("Content missing", ex.getMessage(), "Except the same error message");
        }
    }

    @Test
    public void testEditPost() throws MissingInputException {
        PostDTO pDTO = new PostDTO(post2);
        pDTO.setContent("newmail@mail.dk");
        PostDTO pDTOedited = facade.editPost(pDTO, mod.getUserName());
        assertEquals(pDTOedited.getContent(), pDTO.getContent() + " (<i>edited</i>)", "Except the same post");
        assertEquals(pDTOedited.getDate(), pDTO.getDate(), "Except the same date");
    }

    @Test
    public void testDeletePost() throws MissingInputException {
        PostDTO pDTO = facade.deletePost(mod.getUserName(), post2.getId());
        assertEquals(2, facade.getAllPosts().getAll().size(), "Excepts three persons");
    }

}
