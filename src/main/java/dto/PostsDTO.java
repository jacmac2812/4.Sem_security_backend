/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Post;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jacobsimonsen
 */
public class PostsDTO {
         List<PostDTO> all = new ArrayList();

    public PostsDTO(List<Post> postEntities) {
        postEntities.forEach((p) -> {
            all.add(new PostDTO(p));
        });
    }

    public List<PostDTO> getAll() {
        return all;
    }
}
