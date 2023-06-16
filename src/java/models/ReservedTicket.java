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
 * this represent the pure table structure of 'reserved_tickeets' in the db
 */
public class ReservedTicket implements Serializable {
    
    private int TICKET_ID;
    private int USER_ID;
    private String RESERVED_AT;

    public int getTICKET_ID() {
        return TICKET_ID;
    }

    public int getUSER_ID() {
        return USER_ID;
    }

    public String getRESERVED_AT() {
        return RESERVED_AT;
    }

    public void setTICKET_ID(int TICKET_ID) {
        this.TICKET_ID = TICKET_ID;
    }

    public void setUSER_ID(int USER_ID) {
        this.USER_ID = USER_ID;
    }

    public void setRESERVED_AT(String RESERVED_AT) {
        this.RESERVED_AT = RESERVED_AT;
    }   
}
