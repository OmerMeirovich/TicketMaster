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
 * this represent the pure table structure of 'ticket_type' in the db
 */
public class TicketType implements Serializable{
    private int ID;
    private String NAME;

    public int getID() {
        return ID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }
    
}
