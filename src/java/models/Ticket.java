/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Serializable;
import java.sql.Date;

/**
 *
 * @author OmerMeirovich
 * this represent the pure table structure of 'tickets' in the db
 */
public class Ticket implements Serializable{
    private int ID;
    private int TYPE_ID;
    private String NAME;
    private String LOCATION;
    private int SELLER_ID;
    private Date DATE_OF_TICKET;
    private Boolean IS_SOLD;
    private int BUYER_ID;
    private int PRICE;
    
    public Ticket(){
        
    }

    public Ticket(int TYPE_ID, String NAME, String LOCATION, int SELLER_ID, Date DATE_OF_TICKET, Boolean IS_SOLD, int PRICE) {
        this.TYPE_ID = TYPE_ID;
        this.NAME = NAME;
        this.LOCATION = LOCATION;
        this.SELLER_ID = SELLER_ID;
        this.DATE_OF_TICKET = DATE_OF_TICKET;
        this.IS_SOLD = false;
        this.PRICE = PRICE;
    }

    public int getPRICE() {
        return PRICE;
    }

    public void setPRICE(int PRICE) {
        this.PRICE = PRICE;
    }
    
    public int getID() {
        return ID;
    }

    public int getTYPE_ID() {
        return TYPE_ID;
    }

    public String getNAME() {
        return NAME;
    }

    public String getLOCATION() {
        return LOCATION;
    }

    public int getSELLER_ID() {
        return SELLER_ID;
    }

    public Date getDATE_OF_TICKET() {
        return DATE_OF_TICKET;
    }

    public Boolean getIS_SOLD() {
        return IS_SOLD;
    }

    public int getBUYER_ID() {
        return BUYER_ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setTYPE_ID(int TYPE_ID) {
        this.TYPE_ID = TYPE_ID;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public void setLOCATION(String LOCATION) {
        this.LOCATION = LOCATION;
    }

    public void setSELLER_ID(int SELLER_ID) {
        this.SELLER_ID = SELLER_ID;
    }

    public void setDATE_OF_TICKET(Date DATE_OF_TICKET) {
        this.DATE_OF_TICKET = DATE_OF_TICKET;
    }

    public void setIS_SOLD(Boolean IS_SOLD) {
        this.IS_SOLD = IS_SOLD;
    }

    public void setBUYER_ID(int BUYER_ID) {
        this.BUYER_ID = BUYER_ID;
    }
    
    
}
