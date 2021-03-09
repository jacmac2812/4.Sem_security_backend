/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.ProductDTO;
import entities.Favorit;
import entities.Role;
import entities.User;
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
public class FavoritFacadeTest {
    private static EntityManagerFactory emf;
    private static FavoritFacade facade;
    User user;
    Role userRole;
    Favorit fav1;
    Favorit fav2;    
        
    public FavoritFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = FavoritFacade.getFavoritFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Favorit.deleteAllRows").executeUpdate();
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
    public void testGetAllFavorites() {
        assertEquals(2, facade.getAllFavorites(user.getUserName()).getAll().size(), "Expect two favorites");
    }
    
    @Test
    public void testAddFavorit() {
        Favorit fav3 = new Favorit("3", "ovn", "hardgood", "200", "200", "tvs.dk/oven", "oven.jpg", "false");
        ProductDTO pDTO = new ProductDTO(fav3);
        ProductDTO pDTOadded = facade.addFavorit(pDTO, user.getUserName());
        assertEquals(pDTO.getName(), pDTOadded.getName(), "Expect the same name");
        assertEquals(3, facade.getAllFavorites(user.getUserName()).getAll().size(), "Excepts three favorites");
    }
    
    @Test
    public void testDeletePerson() {
        String returnMsg = facade.deleteFavorit(fav1.getSku(), user.getUserName());
        assertEquals("Deleted " + fav1.getSku(), returnMsg, "Expect the message");
        assertEquals(1, facade.getAllFavorites(user.getUserName()).getAll().size(), "Excepts one favorit");
    }
}
