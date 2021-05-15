/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.PostDTO;
import dto.PostsDTO;
import dto.UserDTO;
import dto.UsersDTO;
import entities.Post;
import entities.Role;
import entities.User;
import errorhandling.MissingInputException;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;
import security.errorhandling.AuthenticationException;

/**
 *
 * @author jacobsimonsen
 */
public class PostFacade {

    private static EntityManagerFactory emf;

    private static PostFacade instance;

    private PostFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static PostFacade getPostFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PostFacade();
        }
        return instance;
    }

    public PostDTO createPost(String content, String userName) throws MissingInputException {

        if (content.length() == 0) {
            throw new MissingInputException("Content missing");
        }

        EntityManager em = emf.createEntityManager();

        try {
            User user = em.find(User.class, userName);

            Post post = new Post(content);

            em.getTransaction().begin();

            user.addPost(post);

            em.persist(user);

            em.getTransaction().commit();

            return new PostDTO(post);
        } finally {
            em.close();
        }
    }

    public PostDTO deletePost(String userName, String[] tokenSplit, int id) throws MissingInputException {
        EntityManager em = emf.createEntityManager();
        if (userName.equals(tokenSplit[0]) || "mod".equals(tokenSplit[1])) {

            try {
                User user = em.find(User.class, userName);

                Post post = em.find(Post.class, id);

                if (user.getPosts().contains(post)) {
                    em.getTransaction().begin();

                    em.remove(post);

                    em.getTransaction().commit();
                } else {

                    throw new MissingInputException("Post not found");
                }

                PostDTO pDTO = new PostDTO(post);

                return pDTO;

            } finally {
                em.close();
            }
        } else {
            throw new MissingInputException("Not authorized to delete post");
        }
    }

    public PostDTO editPost(PostDTO p, String[] tokenSplit, String userName) throws MissingInputException {
        EntityManager em = emf.createEntityManager();
        if (userName.equals(tokenSplit[0]) || "mod".equals(tokenSplit[1])) {
            if (p.getContent().length() == 0) {
                throw new MissingInputException("Content missing");
            }
            try {
                User user = em.find(User.class, userName);

                Post post = em.find(Post.class, p.getId());

                if (user.getPosts().contains(post)) {
                    em.getTransaction().begin();

                    post.setContent(p.getContent() + " (<i>edited</i>)");

                    em.getTransaction().commit();
                } else {

                    throw new MissingInputException("Post not found");
                }

                PostDTO pDTO = new PostDTO(post);

                return pDTO;
            } finally {
                em.close();
            }
        } else {
            throw new MissingInputException("Not authorized to edit post");
        }
    }

    public PostsDTO getAllPosts() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Post> query = em.createQuery("SELECT p FROM Post p", entities.Post.class);
            List<Post> posts = query.getResultList();
            PostsDTO all = new PostsDTO(posts);
            return all;
        } finally {
            em.close();
        }
    }

    public PostsDTO getAllPostsUser(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Post> query = em.createQuery("SELECT p FROM Post p JOIN p.user u WHERE u.userName =:name", Post.class);
            query.setParameter("name", name);
            List<Post> posts = query.getResultList();
            PostsDTO all = new PostsDTO(posts);
            return all;
        } finally {
            em.close();
        }
    }
}
