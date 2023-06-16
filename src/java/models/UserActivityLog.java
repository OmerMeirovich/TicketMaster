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
 * this represent the pure table structure of 'user_activity_log' in the db
 */
public class UserActivityLog implements Serializable{
    private int ID;
    private int USER_ID;
    private String ROUTE;
    private String exception;
    private String CREATED_AT;

    public int getID() {
        return ID;
    }

    public int getUSER_ID() {
        return USER_ID;
    }

    public String getROUTE() {
        return ROUTE;
    }

    public String getException() {
        return exception;
    }

    public String getCREATED_AT() {
        return CREATED_AT;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setUSER_ID(int USER_ID) {
        this.USER_ID = USER_ID;
    }

    public void setROUTE(String ROUTE) {
        this.ROUTE = ROUTE;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void setCREATED_AT(String CREATED_AT) {
        this.CREATED_AT = CREATED_AT;
    }
    
    
}
