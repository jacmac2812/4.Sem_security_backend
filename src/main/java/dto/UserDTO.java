/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.User;

/**
 *
 * @author jacobsimonsen
 */
public class UserDTO {

    String name;
    String email;
    String password;
    String profilePicPath;

    public UserDTO(String name, String password, String email, String profilePicPath) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.profilePicPath = profilePicPath;
    }

    public UserDTO(User user) {
        this.name = user.getUserName();
        this.email = user.getEmail();
        this.profilePicPath = user.getProfilePicPath();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
