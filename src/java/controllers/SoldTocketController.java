/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


/**
 * a controller for the sold ticket history browser
 * @author amir
 */
@ManagedBean(name = "soldTicketBrowser")
@SessionScoped
public class SoldTocketController extends TicketBrowserController {
    
    /**
     * called when page loads , sets the defualt query and calls to get the ticket list
     */
    @Override
    public void onPageLoad(){
        int userID=getUserID();
        if(userID<0)
            return;
        
        //set the defualt query and reset the filter
        defaultChecks=" SELLER_ID = "+userID +" AND is_sold = true";
        super.onPageLoad();
    }
    
    
}
