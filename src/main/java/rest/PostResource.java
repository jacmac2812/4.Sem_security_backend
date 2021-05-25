/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ContentDTO;
import dto.PostDTO;
import dto.PostsDTO;
import errorhandling.MissingInputException;
import facades.PostFacade;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.EMF_Creator;
import utils.HttpUtils;

/**
 *
 * @author jacobsimonsen
 */
@Path("posts")
public class PostResource {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    private static final Logger logger = LogManager.getLogger(PostResource.class);

    private static final PostFacade FACADE = PostFacade.getPostFacade(EMF);

    private static final HttpUtils utils = new HttpUtils();

    @Path("/{username}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "mod"})
    public String createPost(@PathParam("username") String userName, String content) throws MissingInputException {
        logger.info("POST: /posts/{username}");
        ContentDTO cDTO = GSON.fromJson(content, ContentDTO.class);
        PostDTO pAdded = FACADE.createPost(cDTO.getContent(), userName);
        return GSON.toJson(pAdded);
    }

    @Path("/{id}/users/{username}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "mod"})
    public String deletePost(@HeaderParam("x-access-token") String token, @PathParam("id") int id, @PathParam("username") String userName) throws MissingInputException {
        logger.warn("DELETE: /posts/{id}/users/{username}");
        String[] splitArray = utils.decodeToken(token);
        PostDTO pDeleted = FACADE.deletePost(userName, splitArray, id);
        return GSON.toJson(pDeleted);
    }

    @Path("/{username}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"user", "mod"})
    public String editPost(@HeaderParam("x-access-token") String token, @PathParam("username") String userName, String post) throws MissingInputException {
        logger.warn("PUT: /posts/{username}");
        PostDTO pDTO = GSON.fromJson(post, PostDTO.class);
        String[] splitArray = utils.decodeToken(token);
        PostDTO uEdited = FACADE.editPost(pDTO, splitArray, userName);
        return GSON.toJson(uEdited);
    }

    @Path("/all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"user", "mod", "admin"})
    public String getAllPosts() {
        PostsDTO psDTO = FACADE.getAllPosts();
        return GSON.toJson(psDTO);
    }

    @Path("/all/{username}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"user", "mod"})
    public String getAllPostsUser(@PathParam("username") String userName) {
        logger.info("GET: /posts/all/{username}");
        PostsDTO psDTO = FACADE.getAllPostsUser(userName);
        return GSON.toJson(psDTO);
    }
}
