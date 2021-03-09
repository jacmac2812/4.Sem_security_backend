/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.UserDTO;
import entities.Role;
import entities.User;
import errorhandling.MissingInputException;
import java.util.ArrayList;
import java.util.List;
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
 * @author Acer
 */
public class UserFacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;
    User user;
    User admin;
    User both;
    Role userRole;
    Role adminRole;
        
    public UserFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = UserFacade.getUserFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
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
            user = new User("user", "hello", "user@mail.dk", "12345678");
            admin = new User("admin", "with", "admin@mail.dk", "23456789");
            both = new User("user_admin", "you", "both@mail.dk", "34567890");

            em.getTransaction().begin();
            userRole = new Role("user");
            adminRole = new Role("admin");
            user.addRole(userRole);
            admin.addRole(adminRole);
            both.addRole(userRole);
            both.addRole(adminRole);

            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();

            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
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
    public void testGetAllUsers() {
        assertEquals(3, facade.getAllUsers().getAll().size(), "Expect three users");
    }

    @Test
    public void testAddPerson() throws MissingInputException {
        UserDTO uDTO = facade.createUser("test", "test", "test@mail.dk", "11223344");
        assertEquals("test", uDTO.getName(), "Expect the same name");
        assertEquals(4, facade.getAllUsers().getAll().size(), "Excepts four persons");
    }

    @Test
    public void testAddPersonException() {
        try {
            UserDTO uDTO = facade.createUser("test", "", "test@mail.dk", "11223344");
        } catch (MissingInputException ex) {
            assertEquals("Name and/or password is missing", ex.getMessage(), "Except the same error message");
        }
    }

    @Test
    public void testEditPerson() {
        UserDTO uDTO = new UserDTO("user", "hello", "user@mail.dk", "12345678");
        uDTO.setEmail("newmail@mail.dk");
        UserDTO uDTOedited = facade.editUser(uDTO, user.getUserName());
        assertEquals(uDTOedited.getEmail(), uDTO.getEmail(), "Except the same email");
    }

    @Test
    public void testDeletePerson() {
        UserDTO uDTO = facade.deleteUser(both.getUserName());
        assertEquals(2, facade.getAllUsers().getAll().size(), "Excepts two persons");
    }

}
