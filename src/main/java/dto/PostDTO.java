/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Post;
import entities.User;
import java.util.Date;

/**
 *
 * @author jacobsimonsen
 */
public class PostDTO {

    private int id;
    private String content;
    private Date date;
    private String userName;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.date = post.getDate();
        this.userName = post.getUser().getUserName();
    }

    public PostDTO(String content, Date date, String userName) {
        this.content = content;
        this.date = date;
        this.userName = userName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
