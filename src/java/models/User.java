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
 * this represent the pure table structure of 'users' in the db
 */
public class User implements Serializable{
    private int ID;
    private String EMAIL;
    private String PASSWORD;
    private String REMEMBER_TOKEN;
    private int PAYMENT_ID;

    public int getID() {
        return ID;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public String getREMEMBER_TOKEN() {
        return REMEMBER_TOKEN;
    }

    public int getPAYMENT_ID() {
        return PAYMENT_ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public void setREMEMBER_TOKEN(String REMEMBER_TOKEN) {
        this.REMEMBER_TOKEN = REMEMBER_TOKEN;
    }

    public void setPAYMENT_ID(int PAYMENT_ID) {
        this.PAYMENT_ID = PAYMENT_ID;
    }
    
    
}
