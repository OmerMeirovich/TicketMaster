/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import database.TicketQueries;
import database.UserQueries;
import exceptions.UserNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import models.TicketWithExtra;
import uitilities.Constants;
import uitilities.ControllerUtilities;
import uitilities.InternalStorageManager;
import uitilities.SearchFilter;
import static uitilities.SearchFilter.getFilteredQuery;

/**
 * an abstract class for all the ticket browsing pages to implement
 * @author amir
 */
public abstract class TicketBrowserController {
    protected List<TicketWithExtra> ticketList;
    protected String defaultChecks="";
    
    

    /**
     *Sets the ticketList to contain only tickets that are filltered
     */
    public void filteredSearch(){
        
       TicketQueries tq= new TicketQueries();
       String query=getFilteredQuery(defaultChecks);
        
        //execute query
        try {
            ticketList = tq.getTicketsWithExtraQuery(query);
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    ex.getMessage(), null));
            Logger.getLogger(TicketBuyingController.class.getName()).log(Level.SEVERE, null, ex);
            //add error to DB logger
            UserQueries uq = new UserQueries();
            //get current page 
            String page =FacesContext.getCurrentInstance().getViewRoot().getViewId();
            uq.insertUserActivityLog(page, ex.toString());
        }
       
    }
    
    /**
     * called when page loads , get the ticket list with a restarted filter
     */
    public void onPageLoad(){
        SearchFilter.resetFilter();
        //seach for tickets with the default filter
        filteredSearch();
    }
    
    /**
     *  get user ID from the cookie manager with error handling
     * @return the user ID or -1 if user not found
     */
    protected int getUserID(){
        InternalStorageManager manager = InternalStorageManager.getInstance();
        //get id from cookies
        int userID;
        try{
            userID= manager.getUserId();
            return userID;
        } catch (UserNotFoundException ex) { //send to login if user not found
            Logger.getLogger(HistoryController.class.getName()).log(Level.SEVERE, null, ex);
            ControllerUtilities.redirectToPage(Constants.LOGIN_PAGE);
            //add error to DB logger
            UserQueries uq = new UserQueries();
            //get current page 
            String page =FacesContext.getCurrentInstance().getViewRoot().getViewId();
            uq.insertUserActivityLog(page, ex.toString());
            return -1;
        } 
    }
    
    /**
     * return the ticket list
     * @return the ticket list
     */
    public List<TicketWithExtra> getTicketList(){
        return ticketList;
    }
}
