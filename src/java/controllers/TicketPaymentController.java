/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import database.TicketQueries;
import database.UserQueries;
import exceptions.UserNotAuthorizedException;
import exceptions.UserNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import models.Payment;
import models.TicketWithExtra;
import models.User;
import uitilities.Constants;
import uitilities.ControllerUtilities;
import uitilities.InternalStorageManager;

/**
 *
 * @author OmerMeirovich
 */
@ManagedBean(name = "ticket_payment")
@SessionScoped
public class TicketPaymentController {

    private List<Payment> paymentMethod;
    private String cardNumber;
    private String expDate;
    private String cvv;

    /**
     * This function is called on page load to reserve the ticket for the
     * logged-in user and to check if the user is authorized to buy the ticket.
     * If the ticket is reserved by another user, the function will redirect the
     * user to the reserved ticket page, and if the ticket is not reserved, the
     * function will reserve it for the current user.
     */
    public void onPageLoad() {
        cardNumber = "";
        expDate = "";
        cvv = "";
        try {
            InternalStorageManager manager = InternalStorageManager.getInstance();
            int ticketToBuyId = manager.getTicketToBuy();
            User loggedUser = ControllerUtilities.getUserFromCookies();
            TicketQueries tq = new TicketQueries();
            // first we check if a user is even authorized to buy the curernt ticket, if not then redirect to page saying he has to wait 10 minutes because another user
            // reserved it.
            if (tq.isTicketRservedNotByUser(ticketToBuyId, loggedUser.getID())) {
                // redirect to reserved ticket page
                ControllerUtilities.redirectToPage(Constants.RESERVED_TICKET_PAGE);
            } else {
                // the user that entered here first will has the ticket reserved to him.
                tq.insertReservedTicket(ticketToBuyId, loggedUser.getID());
            }
        } catch (SQLException | UserNotAuthorizedException | NullPointerException ex) {
            Logger.getLogger(TicketPaymentController.class.getName()).log(Level.SEVERE, null, ex);
            this.insertToActivityLog(ex);
        }
    }

    /**
     * This function returns a list of payment methods saved by the user. If the
     * user has no saved payment methods, the function will return an empty
     * list.
     *
     * @return A list of Payment objects representing the user's saved payment
     * methods.
     */
    public List<Payment> getPaymentMethod() {
        paymentMethod = new ArrayList<>();
        UserQueries uq = new UserQueries();
        int paymentId = ControllerUtilities.getUserFromCookies().getPAYMENT_ID();
        try {
            paymentMethod.add(uq.getPaymentById(paymentId));
        } catch (SQLException | NullPointerException ex) {
            // TODO: tell user he has no favorites saved.
            Logger.getLogger(FavoritesController.class.getName()).log(Level.SEVERE, null, ex);
            this.insertToActivityLog(ex);
        }

        return paymentMethod;
    }

    /**
     * Pay with an existing payment method shown in the list.
     */
    public void payWithExistingMethod() {
        try {
            int paymentId = getIdOfClickedPayment();
            // TODO: get payment by the paymentId and then call bill amount.
            UserQueries uq = new UserQueries();
            Payment payment = uq.getPaymentById(paymentId);
            TicketWithExtra ticketToBuy = getTicketToBuy();
            if (payment == null) {
                throw new Exception();
            }
            billAmount(ticketToBuy, payment.getCARD_NUMBER(), payment.getEX_DATE(), payment.getCVV());
        } catch (Exception ex) {
            Logger.getLogger(TicketPaymentController.class.getName()).log(Level.SEVERE, null, ex);
            this.insertToActivityLog(ex);
        }
    }

    /**
     * get id of existing payment clicked.
     *
     * @return id of payment
     */
    private int getIdOfClickedPayment() {
        return Integer.parseInt((String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("existingPmTicketId"));
    }

    /**
     * Pay with new method meaning the data user inserts and clicks pay.
     */
    public void payWithNewMethod() {
        boolean errorFlag = false;
        UserQueries uq = new UserQueries();
        InternalStorageManager manager = InternalStorageManager.getInstance();
        TicketWithExtra ticketToBuy = getTicketToBuy();
        int userId = 0;

        try {
            userId = manager.getUserId();
        } catch (UserNotFoundException ex) {
            Logger.getLogger(TicketPaymentController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Logged in user not found", null));
            errorFlag = true;

        }

        // validate inputs
        if (!isCardNumberValid()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Card Number must be in pattern of XXXXXXXXXXXXXXXX where X is a digit between 0-9.", null));
            errorFlag = true;
        }
        if (!isExpDateValid()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "EXP date is invalid, Must be in format MM/YY and must not pass current date.", null));
            errorFlag = true;
        }
        if (!isCvvValid()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "CVV must be of pattern XXX where X is a digit between 0-9.", null));
            errorFlag = true;
        }
        // if no errors update current payment method as users primary payment method.
        try {
            if (errorFlag == false) {
                int newPaymentMethodId = uq.insertPaymentMethod(cardNumber, expDate, cvv);
                uq.updateUserPaymentMethod(userId, newPaymentMethodId);
            }
        } catch (SQLException | UserNotAuthorizedException ex) {
            Logger.getLogger(TicketPaymentController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "There was an error inserting the new payment method.", null));

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    ex.getClass().toString(), null));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    ex.toString(), null));

            uq.insertUserActivityLog(Constants.TICKET_PAYMENT_PAGE, ex.toString());
            return;
        }

        if (errorFlag == false) {
            billAmount(ticketToBuy, cardNumber, expDate, cvv);
        }
    }

    /**
     * bills the credit card given with the price of the ticketToBuy given
     *
     * @param ticketToBuy object of ticket to be purchsed
     * @param cardNumber string of card number
     * @param expDate string of expiration date in form MM/YY
     * @param cvv string of cvv in form of XXX
     */
    public synchronized void billAmount(TicketWithExtra ticketToBuy, String cardNumber, String expDate, String cvv) {
        int ticketId = ticketToBuy.getID();

        InternalStorageManager manager = InternalStorageManager.getInstance();
        TicketQueries tq = new TicketQueries();
        int buyerId = 0;

        try {
            buyerId = manager.getUserId();
            if (tq.isTicketRservedNotByUser(ticketId, buyerId)) {
                // redirect to reserved ticket page
                ControllerUtilities.redirectToPage(Constants.RESERVED_TICKET_PAGE);
                return;
            }
            tq.updateTicketBuyer(buyerId, ticketId);
        } catch (SQLException | UserNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "There was an error inserting the new payment method. (BILL)", null));
            this.insertToActivityLog(ex);
            return;
        }

        // if billing was successful redirect to success page! (consider maybe adding msg)
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.redirect(Constants.PAYEMNT_SUCCESS_PAGE);
        } catch (IOException ex) {
            Logger.getLogger(TicketPaymentController.class.getName()).log(Level.SEVERE, null, ex);
            this.insertToActivityLog(ex);
        }

    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public int getTicketPrice() {
        TicketWithExtra ticketToBuy = getTicketToBuy();
        if (ticketToBuy == null) {
            return -1;
        }
        return ticketToBuy.getPRICE();
    }

    /**
     *
     * Checks if the input credit card number is valid.
     *
     * A valid credit card number is a 16-digit string consisting only of
     * digits.
     *
     * @return true if the credit card number is valid, false otherwise
     */
    private boolean isCardNumberValid() {
        if (cardNumber == null) {
            return false;
        }
        cardNumber = cardNumber.trim();
        if (cardNumber.length() != 16) {
            return false;
        }

        for (int i = 0; i < cardNumber.length(); i++) {
            if (!Character.isDigit(cardNumber.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the input expiration date is valid.
     *
     * A valid expiration date is a string in the format "MM/YY", where MM is a
     * two-digit month
     *
     * and YY is a two-digit year. The expiration date must not be before the
     * current date.
     *
     * @return true if the expiration date is valid, false otherwise
     */
    private boolean isExpDateValid() {
        if (expDate == null) {
            return false;
        }
        expDate = expDate.trim();
        String[] parts = expDate.split("/");
        if (parts.length != 2) {
            return false;
        }

        String month = parts[0];
        String year = parts[1];

        if (isDateExpired(month, year)) {
            return false;
        }

        return !(!month.matches("^(0?[1-9]|1[0-2])$") || !year.matches("^\\d{2}$"));
    }


    /**
     * Checks if the input expiry date (month and year) is before the current
     * date. returns true if date given is not good (expired).
     *
     * @param month the expiry month of the credit card (as a string)
     * @param year the expiry year of the credit card (as a string) - YEAR IN
     * FORMAT OF YY
     * @return true if the input expiry date is before the current date, false
     * otherwise
     */
    private boolean isDateExpired(String month, String year) {
        int currentYear = Year.now().getValue();
        int currentMonth = LocalDate.now().getMonthValue();
        int yearToCheck, monthToCheck;
        year = "20" + year;
        try {
            yearToCheck = Integer.parseInt(year);
            monthToCheck = Integer.parseInt(month);

            if(yearToCheck < currentYear)
                return true;
            if(yearToCheck == currentYear)
                return monthToCheck < currentMonth;     
            return false;
        } catch (Exception ex) {
            insertToActivityLog(ex);
            return true;
        }
    }

    /**
     * Checks if the input CVV (Card Verification Value) is valid.
     *
     * A valid CVV is a 3-digit string consisting only of digits.
     *
     * @return true if the CVV is valid, false otherwise
     */
    private boolean isCvvValid() {
        if (cvv == null) {
            return false;
        }
        cvv = cvv.trim();
        if (cvv.length() != 3) {
            return false;
        }

        for (int i = 0; i < cvv.length(); i++) {
            if (!Character.isDigit(cvv.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Inserts an exception to the user activity log.
     *
     * @param ex the exception to be inserted to the log
     */
    private void insertToActivityLog(Exception ex) {
        UserQueries uq = new UserQueries();
        uq.insertUserActivityLog(Constants.TICKET_PAYMENT_PAGE, ex.toString());
    }

    /**
     *
     * Gets the ticket to be purchased from the internal storage.
     *
     * @return the ticket to be purchased, or null if an error occurred
     */
    private TicketWithExtra getTicketToBuy() {
        TicketWithExtra ticketToBuy = null;
        try {
            InternalStorageManager manager = InternalStorageManager.getInstance();
            TicketQueries tq = new TicketQueries();
            int ticketToBuyId = manager.getTicketToBuy();
            ticketToBuy = tq.getTicketbyId(ticketToBuyId);
        } catch (SQLException ex) {
            Logger.getLogger(TicketPaymentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ticketToBuy;
    }
}
