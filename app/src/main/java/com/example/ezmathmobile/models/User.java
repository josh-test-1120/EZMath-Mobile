package com.example.ezmathmobile.models;

/**
 * This is the user class
 */
public class User {
    // Private variables
    public String id, first_name, last_name, image, email, password, userid;

    /**
     * Empty constructor for serialization
     */
    public User() {}

    /**
     * Getter for the user ID
     * @return this is the string of the user ID
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for the userID
     * @param id string of the userID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for the user's first name
     * @return string of the user's first name
     */
    public String getFirst_name() {
        return first_name;
    }

    /**
     * Setter for the user's first name
     * @param first_name string of the user's first name
     */
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    /**
     * Getter for the user's last name
     * @return string of the user's last name
     */
    public String getLast_name() {
        return last_name;
    }

    /**
     * Setter for the user's last name
     * @param last_name string of the user's last name
     */
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    /**
     * Getter for the user's profile image
     * @return string of the BASE64 encoded image
     */
    public String getImage() {
        return image;
    }

    /**
     * Setter for the user's profile image
     * @param image string of the BASE64 encoded image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Getter for the user's email
     * @return string of the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for the user's email
     * @param email string of the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for the user's password
     * @return string of the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for the user's password
     * @param password string of the user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for the user's ID
     * @return string of the user's ID
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Setter for the user's ID
     * @param userid string of the user's ID
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }
}
