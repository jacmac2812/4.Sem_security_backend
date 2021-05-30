/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import dto.UserDTO;
import dto.UsersDTO;
import errorhandling.MissingInputException;
import errorhandling.NotFoundException;
import facades.UserFacade;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.EMF_Creator;
import utils.HttpUtils;
import utils.Uploads;

/**
 *
 * @author jacobsimonsen
 */
@Path("users")
public class UserResource {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();

    private static final Logger logger = LogManager.getLogger(UserResource.class);

    private static final UserFacade FACADE = UserFacade.getUserFacade(EMF);

    private static final Uploads UPLOADS = new Uploads();
    
    private static final HttpUtils utils = new HttpUtils();

    private static final String UPLOAD_FOLDER = "/Users/jacobsimonsen/Desktop/Security/SecurityBackend/src/main/java/profilePictures/";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createUser(String user) throws MissingInputException {
        logger.info("POST: /users");
        UserDTO uDTO = GSON.fromJson(user, UserDTO.class);
        UserDTO uAdded = FACADE.createUser(uDTO.getName(), uDTO.getPassword(), uDTO.getEmail(), uDTO.getAge(), uDTO.getProfilePicPath());
        return GSON.toJson(uAdded);
    }

    @Path("/{name}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public String deleteUser(@PathParam("name") String name) {
        logger.warn("DELETE: /users/{name}");
        UserDTO uDeleted = FACADE.deleteUser(name);
        return GSON.toJson(uDeleted);
    }

    @Path("/{name}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"user", "admin", "mod"})
    public String editUser(@HeaderParam("x-access-token") String token, @PathParam("name") String name, String user) throws MissingInputException {
        logger.warn("PUT: /users/{name}");
        UserDTO uDTO = GSON.fromJson(user, UserDTO.class);
        String[] splitArray = utils.decodeToken(token);
        UserDTO uEdited = FACADE.editUser(uDTO, splitArray, name);
        return GSON.toJson(uEdited);
    }

    @Path("/all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("admin")
    public String getAllUsers() {
        logger.warn("GET: /users/all");
        UsersDTO usDTO = FACADE.getAllUsers();
        return GSON.toJson(usDTO);
    }

    @Path("/uploadpicture")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed({"user", "admin", "mod"})
    public Response uploadFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
        logger.warn("POST: /users/uploadpicture");
        if (uploadedInputStream == null || fileDetail == null) {
            return Response.status(400).entity("Invalid form data").build();
        }
        // create our destination folder, if it not exists
        try {
            UPLOADS.createFolderIfNotExists(UPLOAD_FOLDER);
        } catch (SecurityException se) {
            return Response.status(500)
                    .entity("Can not create destination folder on server")
                    .build();
        }
        String uploadedFileLocation = UPLOAD_FOLDER + fileDetail.getFileName();
        System.out.println(uploadedFileLocation);
        try {
            UPLOADS.saveToFile(uploadedInputStream, uploadedFileLocation);
        } catch (IOException e) {
            return Response.status(500).entity("Can not save file").build();
        }
        return Response.status(200)
                .entity(uploadedFileLocation).build();
    }
    
     @Path("/getpicture/{name}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    //@RolesAllowed({"user", "admin", "mod"})
    public Response getPicture(@PathParam("name") String name) throws NotFoundException {
        //logger.warn("GET: /users/getpicture");
        String picturePath = FACADE.getPicturePath(name);
        
         System.out.println(name);
        File file = new File(picturePath);
        return Response.ok(file, MediaType.APPLICATION_JSON)
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                .build();
    }
}
