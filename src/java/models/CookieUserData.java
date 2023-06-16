/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author OmerMeirovich
 * This class has the data relevant for save on the users machine.
 * We will use this data whenever we want to query anything with the users creds.
 */
public class CookieUserData{
    private String email;
    private String token;

    public CookieUserData(String email, String token) {
        this.email = email;
        this.token = token;
    }
    
    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    
}
