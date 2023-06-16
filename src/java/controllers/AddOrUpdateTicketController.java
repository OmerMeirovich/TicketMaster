/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import database.TicketQueries;
import database.UserQueries;
import exceptions.SqlInjectionException;
import exceptions.UserNotAuthorizedException;
import exceptions.UserNotFoundException;
import java.util.Date;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import models.TicketWithExtra;
import uitilities.Constants;
import static uitilities.ControllerUtilities.redirectToPage;
import uitilities.InternalStorageManager;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;

/**
 * AddOrUpdateTicketController can add or update a given ticket ID The action is
 * determined by the ticket ID ( -1 for add and ticketID>0 otherwise)
 *
 * @author nohav
 */
@ManagedBean(name = "add_or_update_ticket")
@SessionScoped
public class AddOrUpdateTicketController {

    //ticket parameters
    private int ticketType;
    @NotNull(message = "Empty name field.")
    private String ticketName;
    private String ticketLocation;
    private Date ticketDate;
    private int tPrice;
    // text for the button on screen 
    private String buttonValue = "Add";
    // Id for desired ticket to update ( -1 if on adding mode )
    private int ticketID = -1;

    /**
     * execute the desired action ( add a ticket or update it)
     */
    public void executeTicketToDB() {
        boolean isValid = true;
        TicketQueries tq = new TicketQueries();
        InternalStorageManager ism = InternalStorageManager.getInstance();
        UserQueries uq = new UserQueries();
        //check that the user is logged in 
        try {
            ism.getUserId();

        } catch (UserNotFoundException ex) {
            Logger.getLogger(AddOrUpdateTicketController.class.getName()).log(Level.SEVERE, null, ex);
            redirectToPage(Constants.LOGIN_PAGE);
            return;
        }
        //get sql date 
        java.sql.Date tDate = new java.sql.Date(ticketDate.getTime());
        //check if price is not zero
        if (tPrice <= 0) {
            isValid = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please enter valid price.", null));
        }
        if (ticketDate.before(java.sql.Date.valueOf(LocalDate.now()))) {
            isValid = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "The Date you entered is in the past. Please enter valid date.", null));
        }
        if (ticketName.equals("")) {
            isValid = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please enter name of ticket.", null));
        }
        if (ticketLocation.equals("")) {
            isValid = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Please enter location of ticket.", null));
        }
        if (isValid == false) {
            return;
        } else {
            try {
                //add if ticket id is less than zero and update otherwise
                if (ticketID > 0) {
                    tq.updateTicket(ticketID, ticketType, ticketName, ticketLocation, ism.getUserId(), tPrice, tDate);
                } else {
                    tq.insertTicket(ticketType, ticketName, ticketLocation, ism.getUserId(), tPrice, tDate);
                }
            } catch (SQLException e) {
                uq.insertUserActivityLog(Constants.ADD_UPDATE_TICKET_PAGE, e.toString());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "There is a problem with the server, please try later.", null));
                return;
            } catch (UserNotFoundException | UserNotAuthorizedException e) {
                uq.insertUserActivityLog(Constants.ADD_UPDATE_TICKET_PAGE, e.toString());
                Logger.getLogger(AddOrUpdateTicketController.class.getName()).log(Level.SEVERE, null, e);
                return;
            }
            redirectToHome();
        }
    }

    /**
     * set the controller to Update mode with a given ticket to update
     *
     * @param ticket a desired ticket to update
     */
    public void setToUpdate(TicketWithExtra ticket) {
        // set the parameters to the ticket's parameters
        java.util.Date utilDate = new java.util.Date(ticket.getDATE_OF_TICKET().getTime());
        ticketDate = utilDate;
        ticketLocation = ticket.getLOCATION();
        ticketName = ticket.getNAME();
        tPrice = ticket.getPRICE();
        ticketType = ticket.getTYPE_ID();
        buttonValue = "Update";
        ticketID = ticket.getID();
    }

    /**
     * set the controller to Adding mode
     */
    public void setToAdd() {
        //reset all the parameters
        ticketDate = null;
        ticketLocation = "";
        ticketName = "";
        tPrice = 0;
        ticketType = -1;
        buttonValue = "Add";
        ticketID = -1;
    }

    /**
     * redirect to home page
     */
    public void redirectToHome() {
        redirectToPage(Constants.HOME_PAGE);
    }

    /////////// getters and setters ///////////////////////////////
    public int getTicketType() {
        return ticketType;
    }

    public void setTicketType(int ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public String getTicketLocation() {
        return ticketLocation;
    }

    public void setTicketLocation(String ticketLocation) {
        this.ticketLocation = ticketLocation;
    }

    public Date getTicketDate() {
        return ticketDate;
    }

    public void setTicketDate(Date ticketDate) {
        this.ticketDate = ticketDate;
    }

    public int getTPrice() {
        return tPrice;
    }

    public void setTPrice(int tPrice) {
        this.tPrice = tPrice;
    }

    public String getButtonValue() {
        return buttonValue;
    }

    public void setButtonValue(String buttonValue) {
        this.buttonValue = buttonValue;
    }
}
