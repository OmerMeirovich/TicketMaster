/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import database.UserQueries;
import exceptions.UserNotAuthorizedException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import uitilities.Constants;
import uitilities.ControllerUtilities;
import uitilities.InternalStorageManager;

  
/**
 * controls the BuyTicket page with functionality to browse ticket to buy and filter result
 * 
 * @author amir
 */
@ManagedBean(name = "buyTickets")
@SessionScoped
public class TicketBuyingController extends TicketBrowserController {
    private int chosenTicketID;
    
    /**
     * called when page loads , sets the defualt query and calls to get the ticket list
     */
    @Override
    public void onPageLoad(){
        LocalDate currentDate = LocalDate.now();
        
        int userID=getUserID();
        if(userID<0)
            return;
        
        //set the defualt query and reset the filter
        defaultChecks=" DATE_OF_TICKET > '"+Date.valueOf(currentDate).toString()+"'"+" AND SELLER_ID != "+userID +" AND is_sold = false ";
        super.onPageLoad();
    }  
    
    /**
     * redirects the user to purchase a ticket (in the TicketPayment page) 
     */
    public void buyTicket(){
        //get id of chosen clicked ticket
        InternalStorageManager manager = InternalStorageManager.getInstance();
        manager.putTicketToBuy(chosenTicketID);
        //set url to redirect back to
        MainController.setLastPageUrl(Constants.TICKET_BUYING_PAGE);
        //redirect to payment
        ControllerUtilities.redirectToPage(Constants.TICKET_PAYMENT_PAGE);
        //add request to the DB logger
        UserQueries uq = new UserQueries();
        uq.insertUserActivityLog(Constants.TICKET_BUYING_PAGE, "user requested to buy ticketID:"+chosenTicketID);
    }

    /**
     * listener to the attribute when an update button is clicked 
     * @param event a default variable of the event
     */
    public void attributeListener(ActionEvent event){
        chosenTicketID = (int)event.getComponent().getAttributes().get("ticketId");
        
    }
    
    /**
     * Adds a clicked ticket to favorite
     */
    public void addToFavorite(){
        UserQueries uq = new UserQueries();
        int userId=getUserID();
        if(userId<0)
            return;
        
        try {
            //add ticket to favorite
            uq.insertFavoriteTicket(chosenTicketID, userId);
            //add request to the DB logger
            uq.insertUserActivityLog(Constants.TICKET_BUYING_PAGE, "user requested to add to favorite ticketID:"+chosenTicketID);
    
        } catch (SQLException | UserNotAuthorizedException ex) {
            Logger.getLogger(TicketBuyingController.class.getName()).log(Level.SEVERE, null, ex);
            //add error to DB logger
            uq.insertUserActivityLog(Constants.TICKET_BUYING_PAGE, ex.toString());
        }
    }
    
}
