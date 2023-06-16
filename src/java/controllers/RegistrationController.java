/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import database.UserQueries;
import exceptions.UserExistsException;
import java.sql.SQLException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import uitilities.ControllerUtilities;
import static uitilities.ControllerUtilities.redirectToPage;
import uitilities.InternalStorageManager;

/**
 * This Class is for controlling the Registration Page
 * @author nohav
 */
@ManagedBean(name = "registration")
@SessionScoped
public class RegistrationController {
    private String email;
    private String password;
    
    // This function chacks the registering process, whether all the inputs were inserted correctly
    public void register(){
        UserQueries uq = new UserQueries();
        if(!ControllerUtilities.isEmailValid(email)){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Invalid email format, Please try again.", null));
            return;
        }
        if(password.length() < 8 || password.length() > 16){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Invalid password, Please eneter  pass word length in range of 8 to 16", null));
            return;
        }
        else{
            try{
                uq.insertUser(email,password);
            }catch(UserExistsException e){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "This email is already taken.", null));
                return;
            }catch(SQLException e){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "There is a problem with the server, please try later.", null));
                return;
            }
            InternalStorageManager ism = new InternalStorageManager();
            ism.clearUserCookie();
            redirectToLogin();
        }
    }
    
    // This function redirect the user to the login page
    public void redirectToLogin(){
        redirectToPage("login.xhtml");
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

    
