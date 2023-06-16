/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import database.UserQueries;
import exceptions.UserNotAuthorizedException;
import exceptions.UserNotFoundException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import uitilities.Constants;
import uitilities.ControllerUtilities;
import uitilities.InternalStorageManager;
import static uitilities.SearchFilter.getFilteredQuery;

/**
 *
 * @author OmerMeirovich
 */
@ManagedBean(name = "favorites")
@SessionScoped
public class FavoritesController extends TicketBrowserController {

    /**
     * This method is called when the page loads and sets the default query to
     * retrieve the list of favorite tickets.
     */
    @Override
    public void onPageLoad() {
        LocalDate currentDate = LocalDate.now();
        int userID = getUserID();
        if (userID < 0) {
            return;
        }

        //set the defualt query and reset the filter
        defaultChecks = " DATE_OF_TICKET > '" + Date.valueOf(currentDate).toString() + "' AND user_id= " + userID + " AND is_sold = false ";
        super.onPageLoad();
    }

    /**
     * Sets the ticketList to contain only tickets that are filltered overriden
     * for the favorite ticket browser since this page needs to filter it's
     * tickets from a different database table
     */
    @Override
    public void filteredSearch() {

        UserQueries uq = new UserQueries();
        String query = getFilteredQuery(defaultChecks);

        //execute query
        try {
            ticketList = uq.getFavoriteTicketWithExtra(query);
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    ex.getMessage(), null));
            Logger.getLogger(TicketBuyingController.class.getName()).log(Level.SEVERE, null, ex);
            //add error to DB logger
            //get current page 
            String page = FacesContext.getCurrentInstance().getViewRoot().getViewId();
            uq.insertUserActivityLog(page, ex.toString());
        }

    }

    /**
     * This function activeates when user clicks on the "Buy" button for a
     * specific ticket in the list. The method gets the ID of the ticket to buy
     * and stores it in the internal storage manager before redirecting to the
     * ticket payment page.
     */
    public void BuyTicket() {
        int ticketToBuy = getIdOfClickedTicket();
        InternalStorageManager manager = InternalStorageManager.getInstance();
        manager.putTicketToBuy(ticketToBuy);
        //set url to redirect back to
        MainController.setLastPageUrl(Constants.FAVORITES_PAGE);
        ControllerUtilities.redirectToPage(Constants.TICKET_PAYMENT_PAGE);
    }

    /**
     * This method is called when the user clicks on the "Remove" button for a
     * specific ticket in the list. The method gets the ID of the ticket to
     * remove and the ID of the logged-in user from the internal storage manager
     * and then removes with from favorites table.
     */
    public void removeFromFavorites() {
        UserQueries uq = new UserQueries();
        int ticketIdForRemove = getIdOfClickedTicket();
        InternalStorageManager manager = InternalStorageManager.getInstance();
        try {
            int userId = manager.getUserId();
            uq.removeFavoriteTicket(userId, ticketIdForRemove);
        } catch (SQLException | UserNotFoundException | UserNotAuthorizedException ex) {
            Logger.getLogger(FavoritesController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "There was an error loading: " + ex.toString(), null));
            uq.insertUserActivityLog(Constants.FAVORITES_PAGE, ex.toString());
        }
        ControllerUtilities.redirectToPage(Constants.FAVORITES_PAGE);
    }

    /**
     * This function gets the ID of the ticket that the user clicked on.
     *
     * @return int id of ticket
     */
    private int getIdOfClickedTicket() {
        return Integer.parseInt((String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ticketId"));
    }

}
