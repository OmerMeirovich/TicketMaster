/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uitilities;

import controllers.FavoritesController;
import database.UserQueries;
import exceptions.UserNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import models.CookieUserData;
import models.User;

/**
 *
 * @author OmerMeirovich
 * 
 * Handles extra untilities functions that are helpful to controllers.
 */
public class ControllerUtilities {

    /*
        Redirects to a given pageName, Should use page name from Constants.
     */
    public static void redirectToPage(String pageName) {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        try {
            externalContext.redirect(pageName);
        } catch (IOException ex) {
            Logger.getLogger(FavoritesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * checks if email is valid and in the pattern of xxxx@email.com with regex;
     *
     * @param email string of email
     * @return boolean true if email is in correct structure false otherwise.
     */
    public static boolean isEmailValid(String email) {
        // Regular expression for email validation XXXX@XXXX
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        

        // Compile the regular expression and Match the input string with the regular expression
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    /**
     * get the logged in User object from cookies
     *
     * @return User object
     */
    public static User getUserFromCookies() {
        try {
            UserQueries uq = new UserQueries();
            InternalStorageManager manager = InternalStorageManager.getInstance();
            CookieUserData cud = manager.getUserData();
            return uq.getUserFromCookie(cud);
        } catch (SQLException | UserNotFoundException ex) {
            Logger.getLogger(FavoritesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
