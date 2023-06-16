/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import uitilities.Constants;
import uitilities.ControllerUtilities;

/**
 * the MainController handles general redirection in the website
 * @author OmerMeirovich
 */
@ManagedBean(name = "main")
@SessionScoped
public class MainController {
    
    private String lastPageUrl=Constants.HOME_PAGE;

    /**
     * redirects to log in page
     */
    public void redirectToLogin() {
        String path = "faces/" + Constants.LOGIN_PAGE;
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        try {
            externalContext.redirect(path);
        } catch (IOException ex) {
            Logger.getLogger(FavoritesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *redirects to home page
     */
    public void redirectToHomePage() {
        ControllerUtilities.redirectToPage(Constants.HOME_PAGE);
    }
    
    /**
     *redirects to favorite page
     */
    public void redirectToFavorites() {
        ControllerUtilities.redirectToPage(Constants.FAVORITES_PAGE);
    }
    
    /**
     * redirect to local variable lastPageUrl 
     */
    public void redirectToLastPage() {
        ControllerUtilities.redirectToPage(lastPageUrl);
    }
    
    /**
     * sets the local variable lastPageUrl in current session's instance
     * @param url the url to set 
     */
    public static void setLastPageUrl(String url){
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        MainController mainInstance = (MainController)FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "main");
        mainInstance.lastPageUrl=url;
        
    }
    
}
