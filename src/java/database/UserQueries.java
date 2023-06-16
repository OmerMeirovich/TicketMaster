package database;

import uitilities.DbUtilities;
import exceptions.SqlInjectionException;
import exceptions.SqlNullQueryException;
import exceptions.UserExistsException;
import exceptions.UserNotAuthorizedException;
import exceptions.UserNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.CookieUserData;
import models.Payment;
import models.TicketWithExtra;
import models.User;
import uitilities.InternalStorageManager;

/**
 *
 * @author OmerMeirovich
 * 
 * Handles all the queries related to the users in the database.
 */
public class UserQueries extends DbConnection {

    /**
     * inserts a new user to the database if email is not taken, this is for
     * user registration.
     *
     * @param email
     * @param password
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     * @throws UserExistsException if the email already belongs to a user in db
     */
    public synchronized void insertUser(String email, String password) throws SQLException, UserExistsException {
        String query;

        if (this.isEmailTaken(email)) {
            throw new UserExistsException();
        }

        query = "INSERT INTO users (email, password, remember_token) "
                + "VALUES (?, ?, NULL)";
        try {
            ps = connect().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ps.executeUpdate();
        } finally {
            disconnect();
        }
    }

    /**
     * checks if an email address already belongs to a user in the database.
     *
     * @param email (String) to check if exists in db
     * @return boolean value, true if email is taken, false if email is not
     * taken.
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public Boolean isEmailTaken(String email) throws SQLException {
        String query;

        try {
            query = "SELECT * FROM users WHERE email=?";
            ps = connect().prepareStatement(query);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } finally {
            disconnect();
        }
        return false;
    }


    /**
     * returns a user from the database with given email.
     *
     * @param email email of user
     * @param password password of user
     * @return User object
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     * @throws UserNotFoundException user was not found in the database
     */
    public User getUserByEmailAndPass(String email, String password) throws SQLException, UserNotFoundException {
        User user = null;
        String query;

        try {
            query = "SELECT * FROM users WHERE email = ? AND password = ?";
            ps = connect().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setID(rs.getInt("id"));
                user.setEMAIL(rs.getString("email"));
                user.setPASSWORD(rs.getString("password"));
                user.setREMEMBER_TOKEN(rs.getString("remember_token"));
                user.setPAYMENT_ID(rs.getInt("payment_id"));
            }
        } finally {
            disconnect();
        }

        if (user == null) {
            throw new UserNotFoundException();
        }

        return user;
    }

    public int getUserId(String email, String password) throws SQLException, SqlNullQueryException, UserNotFoundException {
        User user = getUserByEmailAndPass(email, password);
        return user.getID();
    }

    /**
     * logins the user and saves its creds and token into the internalStorage,
     *
     * @param email email of user
     * @param password password of user
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     * @throws UserNotFoundException user was not found in the database
     */
    public void loginUser(String email, String password) throws SQLException, UserNotFoundException {
        String rememberToken;
        String query;

        // used only to check if user exists, will throw UserNotFoundException if it isn't
        this.getUserByEmailAndPass(email, password);

        rememberToken = DbUtilities.generateRememberToken();

        try {
            query = "UPDATE users SET remember_token = ? WHERE email = ? AND password = ?";
            ps = connect().prepareStatement(query);
            ps.setString(1, rememberToken);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();
        } finally {
            disconnect();
        }

        // save the user to the cookies when loggging in.
        InternalStorageManager manager = InternalStorageManager.getInstance();
        manager.putUserData(new CookieUserData(email, rememberToken));
        manager.clearTicketToBuy();

    }

    /**
     * checks if a given email and token matches a record in the db.
     *
     * @param email the email of a user
     * @param rememberToken a remeber token of a user
     * @return boolean value, true if credentials match record in db and false
     * otherwise
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public Boolean isUserValid(String email, String rememberToken) throws SQLException {

        String query;

        try {
            query = "SELECT * FROM users WHERE email=? AND remember_token=?";
            ps = connect().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, rememberToken);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } finally {
            disconnect();
        }
        return false;
    }

    /**
     * returns the id of the payment method from a given card details.
     *
     * @param cardNumber the card number XXXX XXXX XXXX XXXX
     * @param exDate the exp_date MM/YY
     * @param cvv the cvv XXX
     * @return the id of the payment method extracted from card details
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     *
     * @throws SqlNullQueryException if the query came out empty
     */
    public int getPaymentMethodId(String cardNumber, String exDate, String cvv) throws SQLException, SqlNullQueryException {

        String query;
        int paymentId = 0;

        try {
            query = "SELECT id FROM payment WHERE card_number = ? AND exp_date = ? AND cvv = ?";
            ps = connect().prepareStatement(query);
            ps.setString(1, cardNumber);
            ps.setString(2, exDate);
            ps.setString(3, cvv);
            rs = ps.executeQuery();

            if (rs.next()) {
                paymentId = rs.getInt("id");
            }

        } finally {
            disconnect();
        }
        if (paymentId == 0) {
            throw new SqlNullQueryException("Query is null");
        }

        return paymentId;
    }

    /**
     * returns the payment id of a given payment.
     *
     * @param paymentId id of the payment in payment table
     * @return Payment object of the found Object in the database.
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public Payment getPaymentById(int paymentId) throws SQLException {

        String query;

        Payment payment = null;
        try {
            query = "SELECT *"
                    + " FROM payment"
                    + " WHERE id = ?";
            ps = connect().prepareStatement(query);
            ps.setInt(1, paymentId);
            rs = ps.executeQuery();
            if (rs.next()) {
                payment = new Payment();
                payment.setID(rs.getInt("id"));
                payment.setCARD_NUMBER(rs.getString("card_number"));
                payment.setEX_DATE(rs.getString("exp_date"));
                payment.setCVV(rs.getString("cvv"));
            }

        } finally {
            disconnect();
        }
        return payment;
    }

    /**
     * inserts payment if its not already in the db, returns id of inserted
     * payment.
     *
     * @param cardNumber the card number XXXX XXXX XXXX XXXX
     * @param exDate the exp_date MM/YY
     * @param cvv the cvv XXX
     * @return the id of the insertedPaymentMethod
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public int insertPaymentMethod(String cardNumber, String exDate, String cvv) throws SQLException {

        String query;
        int paymentId;
        int insertedPaymentMethodId = -1;

        try {
            paymentId = this.getPaymentMethodId(cardNumber, exDate, cvv);
            // if paymentId hasn't thrown any exception then we return it
            return paymentId;
        } catch (SqlNullQueryException ex) {
            // if excepion caught here then paymentId is not found.
            // thas ok because we will insert it anyway if it isnt.
        }
        try {
            query = "INSERT INTO payment (card_number, exp_date, cvv) VALUES (?, ?, ?)";
            ps = connect().prepareStatement(query);
            ps.setString(1, cardNumber);
            ps.setString(2, exDate);
            ps.setString(3, cvv);
            ps.executeUpdate();
        } finally {
            disconnect();
        }
        try {
            // after insertion this function will return the newly added payment row.
            insertedPaymentMethodId = this.getPaymentMethodId(cardNumber, exDate, cvv);
        } catch (SqlNullQueryException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
            return insertedPaymentMethodId;
        }
        return insertedPaymentMethodId;
    }

    /**
     * updates a specific users payment_method by with given payment details. (
     * adds the payment method to the database and then uses the inserted id).
     *
     * @param email the email of the user
     * @param password the password of the user
     * @param cardNumber the card number XXXX XXXX XXXX XXXX
     * @param exDate the exp_date MM/YY
     * @param cvv the cvv XXX
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     *
     * @throws SqlNullQueryException if any query returned null meaning we can't
     * continue the operation
     */
    private void updateUserPaymentMethod(String email, String password, String cardNumber, String exDate, String cvv) throws SQLException, SqlNullQueryException {
        int paymentId;

        paymentId = this.insertPaymentMethod(cardNumber, exDate, cvv);
        try {
            String query = "UPDATE users SET payment_id=? WHERE email=? AND password=?";
            ps = connect().prepareStatement(query);
            ps.setInt(1, paymentId);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();
        } finally {
            disconnect();
        }
    }

    /**
     * updates a specific users payment_method by with given paymentId
     *
     * @param userId the id of the user in the database
     * @param paymentMethodId the payment_method id in the database
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     * @throws exceptions.UserNotAuthorizedException if unauthorized tris to
     * make request
     */
    public void updateUserPaymentMethod(int userId, int paymentMethodId) throws SQLException, UserNotAuthorizedException {

        // check if logged user is authorized to make request
        try {
            DbUtilities.isUserAuthorized(userId);
        } catch (UserNotFoundException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserNotAuthorizedException();
        }

        try {
            String query = "UPDATE users SET payment_id=? WHERE id=?";
            ps = connect().prepareStatement(query);
            ps.setInt(1, paymentMethodId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } finally {
            disconnect();
        }
    }

    /**
     * returns all the users in the database, was used for testing.
     *
     * @return list of Users
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     * @throws SqlInjectionException if any of the input parameters contain SQL
     * injection patterns or characters.
     */
    private List<User> getAllUsers() throws SQLException, SqlInjectionException {
        List<User> usersList = new ArrayList<>();
        try {
            ps = connect().prepareStatement("select * from USERS");
            User u;
            rs = ps.executeQuery();
            while (rs.next()) {
                u = new User();
                u.setID(rs.getInt("id"));
                u.setEMAIL(rs.getString("email"));
                u.setPASSWORD(rs.getString("password"));
                u.setREMEMBER_TOKEN(rs.getString("remember_token"));
                u.setPAYMENT_ID(rs.getInt("payment_id"));
                usersList.add(u);
            }

        } finally {
            disconnect();
        }
        return usersList;
    }

    /**
     * get all favorite tickets stored by a specfic user, identify by email and
     * password.
     *
     * @param email the email of the user
     * @param password the password of the user
     * @return list of TicketWithExtra
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     *
     * @throws UserNotFoundException if the user was not found in the database.
     */
    public List<TicketWithExtra> getFavoriteTickets(String email, String password) throws SQLException, UserNotFoundException {
        List<TicketWithExtra> ticketList = new ArrayList<>();
        TicketQueries tq = new TicketQueries();
        List<Integer> ticketIds = new ArrayList<>();
        String query;
        int userId = 0;

        try {
            userId = getUserId(email, password);
            query = "SELECT * FROM favorite_tickets WHERE user_id=?";
            ps = connect().prepareStatement(query);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                ticketIds.add(rs.getInt("ticket_id"));
            }

        } catch (SqlNullQueryException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            disconnect();
        }
        // after getting all the tickets ids we want to get the ticket objects from db.
        for (int i = 0; i < ticketIds.size(); i++) {
            TicketWithExtra ticket = tq.getTicketbyId(ticketIds.get(i));
            if (ticket != null) {
                ticketList.add(ticket);
            }
        }
        return ticketList;
    }

    /**
     * get all the favorite tickets stored by a specific user, identify by
     * userId.
     *
     * @param userId the user id in the database
     * @return a list of ticketWithExtra
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     * @throws UserNotAuthorizedException if unauthorized user make request
     */
    public List<TicketWithExtra> getFavoriteTickets(int userId) throws SQLException, UserNotAuthorizedException {
        List<TicketWithExtra> ticketList = new ArrayList<>();
        TicketQueries tq = new TicketQueries();
        List<Integer> ticketIds = new ArrayList<>();
        String query;

        // check if logged user is authorized to make request
        try {
            DbUtilities.isUserAuthorized(userId);
        } catch (UserNotFoundException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserNotAuthorizedException();
        }

        try {
            query = "SELECT * FROM favorite_tickets WHERE user_id=?";
            ps = connect().prepareStatement(query);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                ticketIds.add(rs.getInt("ticket_id"));
            }

        } finally {
            disconnect();
        }
        // after getting all the tickets ids we want to get the ticket objects from db.
        for (int i = 0; i < ticketIds.toArray().length; i++) {
            TicketWithExtra ticket = tq.getTicketbyId(ticketIds.get(i));
            if (ticket != null) {
                ticketList.add(ticket);
            }
        }
        return ticketList;
    }

    /**
     * removes a row from the favorite_tickets table.
     *
     * @param userId the id of the user in the database
     * @param ticketId the id of the ticket in the database
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     * @throws UserNotAuthorizedException if unauthorized user tries to make
     * request
     */
    public void removeFavoriteTicket(int userId, int ticketId) throws SQLException, UserNotAuthorizedException {
        // check if logged user is authorized to make request
        try {
            DbUtilities.isUserAuthorized(userId);
        } catch (UserNotFoundException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserNotAuthorizedException();
        }
        String query;
        try {
            query = "DELETE FROM favorite_tickets WHERE user_id=? AND ticket_id=?";
            ps = connect().prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, ticketId);
            ps.executeUpdate();
        } finally {
            disconnect();
        }
    }

    // please use the getLoggedUser() function instead
    public User getUserFromCookie(CookieUserData cud) throws SQLException, UserNotFoundException {
        String email = cud.getEmail();
        String token = cud.getToken();
        String query;
        User user = null;

        if (email == null || token == null) {
            throw new UserNotFoundException();
        }

        try {
            query = "SELECT * FROM users WHERE email = ? AND REMEMBER_TOKEN = ?";
            ps = connect().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, token);
            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setID(rs.getInt("id"));
                user.setEMAIL(rs.getString("email"));
                user.setPASSWORD(rs.getString("password"));
                user.setREMEMBER_TOKEN(rs.getString("remember_token"));
                user.setPAYMENT_ID(rs.getInt("payment_id"));
            }
        } finally {
            disconnect();
        }

        if (user == null) {
            throw new UserNotFoundException();
        }

        return user;
    }

    /**
     * get the logged in user instance, This function will get the user data
     * from internal storage by itself.
     *
     * @return User object
     * @throws UserNotFoundException if the user was not found inside the
     * database.
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public User getLoggedUser() throws SQLException, UserNotFoundException {
        String query;
        User user = null;

        InternalStorageManager manager = InternalStorageManager.getInstance();
        CookieUserData cud = manager.getUserData();

        try {
            query = "SELECT * FROM users WHERE email = ? AND REMEMBER_TOKEN = ?";
            ps = connect().prepareStatement(query);
            ps.setString(1, cud.getEmail());
            ps.setString(2, cud.getToken());
            rs = ps.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setID(rs.getInt("id"));
                user.setEMAIL(rs.getString("email"));
                user.setPASSWORD(rs.getString("password"));
                user.setREMEMBER_TOKEN(rs.getString("remember_token"));
                user.setPAYMENT_ID(rs.getInt("payment_id"));
            }

        } finally {
            disconnect();
        }

        if (user == null) {
            throw new UserNotFoundException();
        }

        return user;
    }

    public UserQueries() {
    }

    /**
     * Inserts a new row into the user_activity_log table with the specified
     * user ID, route, and exception message.
     *
     * @param pageName the name of the page we called the function from
     * @param exception an optional exception message related to the activity
     */
    public void insertUserActivityLog(String pageName, String exception) {
        try {
            String query;
            InternalStorageManager manager = InternalStorageManager.getInstance();
            int userId = manager.getUserId();
            if (exception == null) {
                exception = "";
            }

            query = "INSERT INTO user_activity_log (user_id, route, exception_message) VALUES (?, ?, ?)";
            ps = connect().prepareStatement(query);
            ps.setInt(1, userId);
            ps.setString(2, pageName);
            ps.setString(3, exception);
            ps.executeUpdate();
        } catch (SQLException | UserNotFoundException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * insert a new favorite ticket to the database and link it to a user
     *
     * @param ticketId int id of the ticker
     * @param userId int id of the user
     * @throws java.sql.SQLException if any error occured in query execution
     * @throws UserNotAuthorizedException if the user is not authorized to make
     * such request
     */
    public void insertFavoriteTicket(int ticketId, int userId) throws SQLException, UserNotAuthorizedException {
        String query;
        // check if logged user is authorized to make request
        try {
            DbUtilities.isUserAuthorized(userId);
        } catch (UserNotFoundException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserNotAuthorizedException();
        }

        if (isFavoriteTicketExists(userId, ticketId)) {
            // if ticket exists then dont need to add to table
            return;
        }

        try {
            query = "INSERT INTO favorite_tickets (user_id, ticket_id) VALUES (?, ?)";
            ps = connect().prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, ticketId);
            ps.executeUpdate();
        } finally {
            disconnect();
        }
    }

    /**
     * checks if a record with such user and ticket id exists in
     * favorite_tickets table.
     *
     * @param userId
     * @param ticketId
     * @return true if user has ticket in favorite false otherwise
     * @throws java.sql.SQLException if any error occured in query execution
     */
    public Boolean isFavoriteTicketExists(int userId, int ticketId) throws SQLException {

        String query = "SELECT * FROM favorite_tickets WHERE user_id = ? AND ticket_id = ?";
        ps = connect().prepareStatement(query);
        ps.setInt(1, userId);
        ps.setInt(2, ticketId);
        rs = ps.executeQuery();

        return rs.next();
    }

    /**
     * returns the favorite tickets based on the whereQuery we got: "WHERE ..."
     *
     * @param whereQuery String represents the where part in query, We do not
     * escape any sequence from whereQuery (sqlInjection vulnerable)
     * @return list of TicketWithExtra
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public ArrayList<TicketWithExtra> getFavoriteTicketWithExtra(String whereQuery) throws SQLException {
        String query;
        ArrayList<TicketWithExtra> ticketList = new ArrayList<>();
        query = " SELECT t.id, t.price, tt.name AS type_name, t.name, t.location, t.seller_id, t.date_of_ticket, t.is_sold, t.buyer_id "
                + " FROM favorite_tickets f "
                + " INNER JOIN ticket t ON f.ticket_id = t.id "
                + " INNER JOIN ticket_type tt ON t.type_id = tt.id "
                + whereQuery;
        try {
            ps = connect().prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                TicketWithExtra twe = new TicketWithExtra();
                twe.setID(rs.getInt("id"));
                twe.setPRICE(rs.getInt("price"));
                twe.setNAME(rs.getString("name"));
                twe.setLOCATION(rs.getString("location"));
                twe.setSELLER_ID(rs.getInt("seller_id"));
                twe.setDATE_OF_TICKET(rs.getDate("date_of_ticket"));
                twe.setIS_SOLD(rs.getBoolean("is_sold"));
                twe.setBUYER_ID(rs.getInt("buyer_id"));
                twe.setTYPE_NAME(rs.getString("type_name"));

                ticketList.add(twe);
            }
        } finally {
            disconnect();
        }
        return ticketList;
    }

}
