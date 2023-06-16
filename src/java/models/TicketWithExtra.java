/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import controllers.HistoryController;
import database.UserQueries;
import exceptions.UserNotFoundException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import uitilities.InternalStorageManager;

/**
 *
 * @author 97252
 * this represents a ticket from the 'tickets' table in the database joined with:
 * the 'ticket_type' tabble on 'type_id'
 * the  'users' table on 'seller_id' and 'buyer_id'
 */
public class TicketWithExtra extends Ticket{
    private static int userID=-1;
    private String TYPE_NAME;
    private String BUYER_EMAIL;
    private String SELLER_EMAIL;
    
    /**
     * check weather to display the 'add to favorite' button in buying_tickets.xhtml
     * @return false if the ticket is favorited by the user and true otherwise
     */
    public boolean getIsNotFavorite(){
        UserQueries uq= new UserQueries();
        InternalStorageManager manager = InternalStorageManager.getInstance();
        //get id from cookies
        try{
            //check to only call the getUserID once when a buy table is rendered
            if(userID<0)
                userID= manager.getUserId();
            //get if the ticker it favorited by the user 
            return !uq.isFavoriteTicketExists(userID,getID());
        } catch (SQLException|UserNotFoundException ex) { //send to login if user not found
            Logger.getLogger(HistoryController.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        } 

    }
    
    public String getTYPE_NAME() {
        return TYPE_NAME;
    }

    public String getBUYER_EMAIL() {
        return BUYER_EMAIL;
    }

    public String getSELLER_EMAIL() {
        return SELLER_EMAIL;
    }

    public void setTYPE_NAME(String TYPE_NAME) {
        this.TYPE_NAME = TYPE_NAME;
    }

    public void setBUYER_EMAIL(String BUYER_EMAIL) {
        this.BUYER_EMAIL = BUYER_EMAIL;
    }
    
    public static void resetUserID() {
        userID=-1;
    }
    
    public void setSELLER_EMAIL(String SELLER_EMAIL) {
        this.SELLER_EMAIL = SELLER_EMAIL;
    }
    
    
}
