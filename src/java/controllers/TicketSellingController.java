/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import database.TicketQueries;
import database.UserQueries;
import exceptions.UserNotAuthorizedException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import models.TicketWithExtra;
import uitilities.Constants;
import uitilities.ControllerUtilities;

/**
 * A controller class for the selling ticket browser page
 * @author amir
 */
@ManagedBean(name = "sellingTicket")
@SessionScoped
public class TicketSellingController extends TicketBrowserController  {
    private int chosenTicketID=-1;
    
    /**
     * called when page loads , sets the defualt query and calls to get the ticket list
     */
    @Override
    public void onPageLoad(){
        int userID=getUserID();
        if(userID<0)
            return;
        
        //set the defualt query and reset the filter
        defaultChecks=" SELLER_ID = "+userID +" AND is_sold = false";
        super.onPageLoad();
    }
    
    /**
     * listener to the attribute when an update button is clicked 
     * @param event a default variable of the event
     */
    public void attributeListener(ActionEvent event){
       chosenTicketID = (int)event.getComponent().getAttributes().get("ticketId");
        
    }
    
    /**
     * Redirects the user to the AddOrUpdate page to update a given ticket
     */
    public void updateTicket(){
        //get the AddOrUpdateTicketController instance
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        AddOrUpdateTicketController controller = (AddOrUpdateTicketController)FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "add_or_update_ticket");
        TicketQueries tq=new TicketQueries();
        try {
            //get the desired ticket
            TicketWithExtra ticket = tq.getTicketbyId(chosenTicketID);
            if(ticket==null){ // a check incase of a bug, should not get here
                Logger.getLogger(TicketSellingController.class.getName()).log(Level.SEVERE, null, "internal error:id for ticket does not exist");
                return;
            }
            //update the AddOrUpdateTicketController controller to the ticket params
            controller.setToUpdate(ticket);
            ControllerUtilities.redirectToPage(Constants.ADD_UPDATE_TICKET_PAGE);
            //add request to the DB logger
            UserQueries uq = new UserQueries();
            uq.insertUserActivityLog(Constants.TICKET_SELLING_PAGE, "user requested to update ticketID:"+chosenTicketID);
    
                
        } catch (SQLException ex) { //handle sql errors
            Logger.getLogger(TicketSellingController.class.getName()).log(Level.SEVERE, null, ex);
            //add error to DB logger
            UserQueries uq = new UserQueries();
            uq.insertUserActivityLog(Constants.TICKET_SELLING_PAGE,ex.toString());
    
        }
    }
    
    /**
     * Redirect to sold ticket browser page 
     */
    public void redirectToSold(){
        ControllerUtilities.redirectToPage(Constants.TICKET_SOLD_PAGE);
    }
    
    
    /**
     * redirect to AddOrUpdateTicket page to add a new ticket
     */
    public void sellTicket(){
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        AddOrUpdateTicketController controller = (AddOrUpdateTicketController)FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "add_or_update_ticket");
        controller.setToAdd();
        ControllerUtilities.redirectToPage(Constants.ADD_UPDATE_TICKET_PAGE);
        //add request to the DB logger
        UserQueries uq = new UserQueries();
        uq.insertUserActivityLog(Constants.TICKET_SELLING_PAGE, "user requested to sell a new ticket");
    
    }
    
    /**
     * delete a clicked ticket ( will only delete if the ticket wasn't sold
     */
    public void deleteTicket(){
        TicketQueries tq=new TicketQueries();
        int userID=getUserID();
        if(userID<0)
            return;
        
        try {
            tq.removeTicket(chosenTicketID, userID);
            //add request to the DB logger
            UserQueries uq = new UserQueries();
            uq.insertUserActivityLog(Constants.TICKET_SELLING_PAGE, "user requested to delete ticketID:"+chosenTicketID);
    
        } catch (UserNotAuthorizedException | SQLException ex) {
            Logger.getLogger(TicketSellingController.class.getName()).log(Level.SEVERE, null, ex);
            //add error to DB logger
            UserQueries uq = new UserQueries();
            uq.insertUserActivityLog(Constants.TICKET_SELLING_PAGE,ex.toString());
    
        }
    }
    
    
    
    
    
}
