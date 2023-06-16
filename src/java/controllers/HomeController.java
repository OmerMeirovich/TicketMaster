/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import models.TicketWithExtra;
import static uitilities.ControllerUtilities.*;
import uitilities.InternalStorageManager;

/**
 * This Class is for controlling the Home Page
 * @author nohav
 */
@ManagedBean(name = "home")
@SessionScoped
public class HomeController {
    
    // This function redirect the user to the login page.
    public void logout(){
        InternalStorageManager ism = new InternalStorageManager();
        ism.clearUserCookie();
        ism.clearTicketToBuy();
        TicketWithExtra.resetUserID();
        redirectToPage("login.xhtml");
    }
    
    // This function redirect the user to the history page.
    public void redirectToHistory(){
        redirectToPage("history.xhtml");
    }
    
    // This function redirect the user to the favorites page.
    public void redirectToFavorites(){
        redirectToPage("favorites.xhtml");
    }
    
    // This function redirect the user to the selling ticket page.
    public void redirectToSellingTickets(){
        redirectToPage("selling_tickets.xhtml");
    }
    
    // This function redirect the iser to the buying ticket page.
    public void redirectToBuyingTickets(){
        redirectToPage("buying_tickets.xhtml");
    }
}
