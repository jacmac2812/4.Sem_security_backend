/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.ProductDTO;
import dto.ProductsDTO;
import entities.Favorit;
import entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * @author jacobsimonsen
 */
public class FavoritFacade {

    private static EntityManagerFactory emf;

    private static FavoritFacade instance;

    private FavoritFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static FavoritFacade getFavoritFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new FavoritFacade();
        }
        return instance;
    }

    public ProductsDTO getAllFavorites(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Favorit> query = em.createQuery("SELECT f FROM Favorit f JOIN f.users u WHERE u.userName =:name", Favorit.class);
            query.setParameter("name", name);
            List<Favorit> favorites = query.getResultList();
            ProductsDTO all = new ProductsDTO(favorites);
            return all;
        } finally {
            em.close();
        }
    }

    public ProductDTO addFavorit(ProductDTO product, String name) {

        EntityManager em = emf.createEntityManager();

        try {

            Favorit f = new Favorit(product.getSku(), product.getName(), product.getType(), product.getRegularPrice(), product.getSalePrice(), product.getUrl(), product.getImage(), product.getOnSale());

            User user = em.find(User.class, name);
            user.addFavorit(f);
            em.getTransaction().begin();

            em.persist(f);

            em.getTransaction().commit();

            return new ProductDTO(f);
        } finally {
            em.close();
        }
    }

    public String deleteFavorit(String sku, String name) {
        EntityManager em = emf.createEntityManager();

        try {

            em.getTransaction().begin();

            User user = em.find(User.class, name);

            Favorit f = em.find(Favorit.class, sku);

            user.removeFavorit(f);
            em.persist(user);
            em.persist(f);

            em.getTransaction().commit();

            String returnMsg = "Deleted " + sku;

            return returnMsg;

        } finally {
            em.close();
        }
    }
}
