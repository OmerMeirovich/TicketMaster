/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Serializable;

/**
 *
 * @author OmerMeirovich
 * this represent the pure table structure of 'favorite_tickets' in the db
 */
public class FavoriteTicket implements Serializable{
    private int USER_ID;
    private int TICKET_ID;

    public int getUSER_ID() {
        return USER_ID;
    }

    public int getTICKET_ID() {
        return TICKET_ID;
    }

    public void setUSER_ID(int USER_ID) {
        this.USER_ID = USER_ID;
    }

    public void setTICKET_ID(int TICKET_ID) {
        this.TICKET_ID = TICKET_ID;
    }
    
  
}
