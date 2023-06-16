/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uitilities;

import database.UserQueries;
import exceptions.UserNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import models.CookieUserData;
import models.User;

/**
 *
 * @author OmerMeirovich
 *
 * Handles the internal storage of a client.
 */
public class InternalStorageManager {

    private static InternalStorageManager instance;

    private final String TICKET_TO_BUY_KEY = "ticketToBuy";
    private final String EMAIL_KEY = "email";
    private final String TOKEN_KEY = "token";
    private final String TICKET_TO_UPDATE_KEY = "ticketToUpdate";

    /**
     * Retrieves the only one static instnace of the InternalStorageManager will
     * be active on all times
     *
     *
     * @return InternalStorageManager instance
     */
    public static synchronized InternalStorageManager getInstance() {
        if (instance == null) {
            instance = new InternalStorageManager();
        }
        return instance;
    }

    /**
     * inserts a value into cookies with given key and value pair
     *
     * @param key string of key
     * @param value string of value
     */
    private void put(String key, String value) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("maxAge", 31536000);
        properties.put("path", "/");
        try {
            FacesContext.getCurrentInstance().getExternalContext().addResponseCookie(key, URLEncoder.encode(value, "UTF-8"), properties);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(InternalStorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * returns the row saved in cookie with given key parameter
     *
     * @param key string key
     * @return String value of key
     */
    private String get(String key) {
        Cookie cookie = (Cookie) FacesContext.getCurrentInstance().getExternalContext().getRequestCookieMap().get(key);
        try {
            String value = URLDecoder.decode(cookie.getValue(), "UTF-8");
            return value;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(InternalStorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * save the id of a ticket the user is intersted in buying right now, only
     * one value at a time (that is because a user can't buy two different
     * tickets at the same time).
     *
     * @param ticketId the id of a ticket
     */
    public void putTicketToBuy(int ticketId) {
        String ticketIdStr = Integer.toString(ticketId);
        put(TICKET_TO_BUY_KEY, ticketIdStr);
    }

    /**
     * clear the id of a ticket the user is intersted in buying right now, only
     * one value at a time (that is because a user can't buy two different
     * tickets at the same time).
     *
     */
    public void clearTicketToBuy() {
        put(TICKET_TO_BUY_KEY, "0");
    }

    /**
     * save the id of a ticket you want to update to internal storage.
     *
     * @param ticketId id of a ticket
     */
    public void putTicketToUpdate(int ticketId) {
        String ticketIdStr = Integer.toString(ticketId);
        put(TICKET_TO_UPDATE_KEY, ticketIdStr);
    }

    /**
     * get the id of a ticket the user is intersted in buying right now, only
     * one value at a time (that is because a user can't buy two different
     * tickets at the same time).
     *
     * @return int - id of the ticket
     */
    public int getTicketToBuy() {

        String ticketIdStr = get(TICKET_TO_BUY_KEY);
        int id = Integer.parseInt(ticketIdStr);
        return id;
    }

    /**
     * get the id of a ticket that requires updating from internal storage.
     *
     * @return id of the ticket
     */
    public int getTicketToUpdate() {

        String ticketIdStr = get(TICKET_TO_UPDATE_KEY);
        int id = Integer.parseInt(ticketIdStr);
        return id;
    }

    /**
     * insert CookieUserData to the cookies (the computer of the client)
     *
     * @param cud user data notice that CookieUserData is assumed to has values
     * (NOT NULL) and also should contain email and token inside for insertion.
     */
    public void putUserData(CookieUserData cud) {
        put(EMAIL_KEY, cud.getEmail());
        put(TOKEN_KEY, cud.getToken());
    }

    /**
     * PLEASE DO NOT USE THIS FUNCTION, THIS IS FOR INTERNAL USE. get insntace
     * of the saved data for a logged in user, returns CookieUserData instance.
     * if no user is logged in will stil return instnace but the values inside
     * are an empty string.
     *
     * @return CookieUserData if found on cookies.
     */
    public CookieUserData getUserData() {
        String email = get(EMAIL_KEY);

        String token = get(TOKEN_KEY);

        return new CookieUserData(email, token);
    }

    /**
     * return the id of the user thats logged in, or -1 if does not exist.
     *
     * @return id (int) of user saved to cookies.
     * @throws exceptions.UserNotFoundException
     */
    public int getUserId() throws UserNotFoundException {
        UserQueries uq = new UserQueries();
        User user = null;
        try {
            user = uq.getLoggedUser();

        } catch (SQLException ex) {
            Logger.getLogger(InternalStorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (user == null) {
            throw new UserNotFoundException();
        }

        return user.getID();
    }

    /**
     *
     * @return the User instance of a logged in user.
     * @throws UserNotFoundException
     */
    public User getLoggedUser() throws UserNotFoundException {
        UserQueries uq = new UserQueries();
        try {
            return uq.getLoggedUser();
        } catch (SQLException ex) {
            Logger.getLogger(InternalStorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * This method deletes the cookie when a user is logging out.
     */
    public void clearUserCookie() {
        CookieUserData cud = new CookieUserData("", "");
        this.putUserData(cud);
    }
}
