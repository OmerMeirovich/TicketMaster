/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import database.UserQueries;
import exceptions.SqlInjectionException;
import exceptions.UserNotFoundException;
import java.io.Serializable;
import java.sql.SQLException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import static uitilities.ControllerUtilities.redirectToPage;

/**
 * This Class is for controlling the Login Page
 * @author nohav
 */
@ManagedBean(name = "login")
@SessionScoped
public class LoginController {
    
    private String email;
    private String password;

    // This function chacks the logining process, whether all the inputs were inserted correctly
    public void login (){
        UserQueries uq = new UserQueries();
        try{
           uq.loginUser(email,password);
        }catch(UserNotFoundException e){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "The user does not exist, please try again.", null));
            return;
        }catch(SQLException e){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "There is a problem with the server, please try later.", null));
            return;
        }
        redirectToHomePage();
    }
    
    // This function redirect the user to the registration page
    public void redirectToRegistration(){
        redirectToPage("registration.xhtml");
    }
    
    // This function redirect the user to the home page
    public void redirectToHomePage(){
        redirectToPage("home.xhtml");
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    } 
}
