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
 * this represent the pure table structure of 'payment' in the db
 */
public class Payment implements Serializable {
    private int ID;
    private String CARD_NUMBER;
    private String EX_DATE;
    private String CVV;

    public int getID() {
        return ID;
    }

    public String getCARD_NUMBER() {
        return CARD_NUMBER;
    }

    public String getEX_DATE() {
        return EX_DATE;
    }

    public String getCVV() {
        return CVV;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setCARD_NUMBER(String CARD_NUMBER) {
        this.CARD_NUMBER = CARD_NUMBER;
    }

    public void setEX_DATE(String EX_DATE) {
        this.EX_DATE = EX_DATE;
    }

    public void setCVV(String CVV) {
        this.CVV = CVV;
    }
    
    
}
