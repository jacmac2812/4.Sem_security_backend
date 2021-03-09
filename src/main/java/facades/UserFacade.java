package facades;

import dto.UserDTO;
import dto.UsersDTO;
import entities.Role;
import entities.User;
import errorhandling.MissingInputException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;
import security.errorhandling.AuthenticationException;

/**
 * @author lam@cphbusiness.dk
 */
public class UserFacade {

    private static EntityManagerFactory emf;

    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public UserDTO createUser(String name, String password, String email, String phoneNumber) throws MissingInputException {
        
        if (name.length() == 0 || password.length() == 0) {
            throw new MissingInputException("Name and/or password is missing");
        }
        if (email.length() == 0 || email.contains("@") == false) {
            throw new MissingInputException("Email missing and/or does not contain @");
        }
        if (phoneNumber.length() != 8) {
            throw new MissingInputException("Phonenumber is the wrong length");
        }
        
        EntityManager em = emf.createEntityManager();

        try {

            User u = new User(name, password, email, phoneNumber);
            Role userRole = new Role("user");

            u.addRole(userRole);
            em.getTransaction().begin();

            em.persist(u);

            em.getTransaction().commit();

            return new UserDTO(u);
        } finally {
            em.close();
        }
    }

    public UserDTO deleteUser(String name) {
        EntityManager em = emf.createEntityManager();

        try {
            User user = em.find(User.class, name);

            em.getTransaction().begin();

            em.remove(user);

            em.getTransaction().commit();

            UserDTO uDTO = new UserDTO(user);

            return uDTO;

        } finally {
            em.close();
        }
    }

    public UserDTO editUser(UserDTO u, String name) {
        EntityManager em = emf.createEntityManager();

        try {
            User user = em.find(User.class, name);
            
            if (u.getPassword().length() != 0) {
                user.setUserPass(BCrypt.hashpw(u.getPassword(), BCrypt.gensalt(5)));
            }
            
            if (u.getEmail().length() != 0 || u.getEmail().contains("@") == true) {
                user.setEmail(u.getEmail());
            }
            
            if (u.getPhoneNumber().length() != 0) {
                user.setPhoneNumber(u.getPhoneNumber());
            }            

            em.getTransaction().begin();

            em.persist(user);

            em.getTransaction().commit();

            UserDTO uDTO = new UserDTO(user);
            return uDTO;
        } finally {
            em.close();
        }
    }

    public UsersDTO getAllUsers() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u", entities.User.class);
            List<User> users = query.getResultList();
            UsersDTO all = new UsersDTO(users);
            return all;
        } finally {
            em.close();
        }
    }
}
