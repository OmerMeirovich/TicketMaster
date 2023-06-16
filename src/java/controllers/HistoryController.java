/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author ayay6
 */
@ManagedBean(name = "history")
@SessionScoped
public class HistoryController extends TicketBrowserController {

    /**
     * called when page loads , sets the defualt query and calls to get the ticket list
     */
    @Override
    public void onPageLoad(){
        int userID=getUserID();
        if(userID<0)
            return;
        
        //set the defualt query and reset the filter
        defaultChecks="  BUYER_ID = "+userID;
        super.onPageLoad();
    }
    
    
}
