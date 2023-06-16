package database;

import uitilities.DbUtilities;
import exceptions.SqlInjectionException;
import exceptions.UserNotAuthorizedException;
import exceptions.UserNotFoundException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.TicketType;
import models.TicketWithExtra;

/**
 *
 * @author OmerMeirovich
 *
 * Handles all the queries related to the tickets in the database.
 */
public class TicketQueries extends DbConnection {

    /**
     * returns the ticket by buyer id, will also add the type_name to it.
     *
     * @param buyerId id of a user in db
     * @return list of TicketWithExtra
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public ArrayList<TicketWithExtra> getTicketByBuyerId(int buyerId) throws SQLException {
        String whereQuery;
        whereQuery = "WHERE t.buyer_id = " + buyerId;
        return getTicketsWithExtraQuery(whereQuery);
    }

    /**
     * returns the ticket by buyer id, will also add the type_name to it.
     *
     * @param sellerId the id of a user in db
     * @return list of TicketWithExtra
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public ArrayList<TicketWithExtra> getTicketsBySellerId(int sellerId) throws SQLException {
        String whereQuery;
        whereQuery = " WHERE t.seller_id = " + sellerId;
        return getTicketsWithExtraQuery(whereQuery);
    }

    /**
     * get all the tickets from dataqbase by location and typeId
     *
     * @param typeId id of type in table database
     * @param location string location of a ticket
     * @return list of TicketWithExtra
     * @throws SqlInjectionException if any of the input parameters contain SQL
     * injection patterns or characters.
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public ArrayList<TicketWithExtra> getTicketByLocationAndTypeId(int typeId, String location) throws SQLException, SqlInjectionException {
        String whereQuery;
        whereQuery = " WHERE t.type_id = " + typeId + " AND t.location LIKE " + "'%" + location + "%'";
        String[] params = {location};
        if (!DbUtilities.isValidParams(params)) {
            throw new SqlInjectionException("Params not valid");
        }
        return getTicketsWithExtraQuery(whereQuery);
    }

    /**
     * returns the ticket by location, will also add the type_name to it.
     *
     * @param location string location of a ticket
     * @return list of TicketWithExtra
     * @throws SqlInjectionException if any of the input parameters contain SQL
     * injection patterns or characters.
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public ArrayList<TicketWithExtra> getTicketByLocation(String location) throws SQLException, SqlInjectionException {
        String whereQuery;
        String[] params = {location};
        whereQuery = " WHERE t.location LIKE " + "'%" + location + "%'";
        if (!DbUtilities.isValidParams(params)) {
            throw new SqlInjectionException("Params not valid");
        }
        return getTicketsWithExtraQuery(whereQuery);
    }

    /**
     * returns the ticket by type, will also add the type_name to it.
     *
     * @param typeId id of type in table database
     * @return list of TicketWithExtra
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public ArrayList<TicketWithExtra> getTicketsByTypeId(int typeId) throws SQLException {
        String whereQuery;
        whereQuery = " WHERE t.type_id = " + typeId;
        return getTicketsWithExtraQuery(whereQuery);
    }

    /**
     * returns the tickets based on the whereQuery we got: "WHERE ..."
     *
     * @param whereQuery String represents the where part in query, We do not
     * escape any sequence from whereQuery (sqlInjection vulnerable)
     * @return list of TicketWithExtra
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public ArrayList<TicketWithExtra> getTicketsWithExtraQuery(String whereQuery) throws SQLException {
        String query;
        ArrayList<TicketWithExtra> ticketList = new ArrayList<>();
        query = " SELECT t.id as id, t.price as price , tt.name as type_name, t.name as name, t.location as location, t.seller_id as seller_id, t.date_of_ticket as date_of_ticket, t.is_sold as is_sold, t.buyer_id as buyer_id"
                + " FROM ticket t"
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

    /**
     * returns a ticket (TicketWithExtra) by it's id from the database.
     *
     * @param ticketId id of a ticket in the database
     * @return TicketWithExtra object
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public TicketWithExtra getTicketbyId(int ticketId) throws SQLException {
        String query;
        TicketWithExtra twe = null;
        try {
            query = " SELECT t.id as id, t.price as price , tt.name as type_name, t.name as name, t.location as location, t.seller_id as seller_id, t.date_of_ticket as date_of_ticket, t.is_sold as is_sold, t.buyer_id as buyer_id"
                    + " FROM ticket t"
                    + " INNER JOIN ticket_type tt ON t.type_id = tt.id "
                    + " WHERE t.id = " + ticketId;

            ps = connect().prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                twe = new TicketWithExtra();
                twe.setID(rs.getInt("id"));
                twe.setPRICE(rs.getInt("price"));
                twe.setNAME(rs.getString("name"));
                twe.setLOCATION(rs.getString("location"));
                twe.setSELLER_ID(rs.getInt("seller_id"));
                twe.setDATE_OF_TICKET(rs.getDate("date_of_ticket"));
                twe.setIS_SOLD(rs.getBoolean("is_sold"));
                twe.setBUYER_ID(rs.getInt("buyer_id"));
                twe.setTYPE_NAME(rs.getString("type_name"));
            }
        } finally {
            disconnect();
        }
        return twe;
    }

    /**
     * returns all ticketTypes and their ids.
     *
     * @return list of TicketType
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public ArrayList<TicketType> getAllTicketType() throws SQLException {
        String query;
        ArrayList<TicketType> ticketTypes = new ArrayList<>();
        try {
            query = "SELECT * "
                    + "FROM ticket_type ";
            ps = connect().prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                TicketType tempTicket = new TicketType();
                tempTicket.setID(rs.getInt("id"));
                tempTicket.setNAME(rs.getString("name"));
                ticketTypes.add(tempTicket);
            }
        } finally {
            disconnect();
        }
        return ticketTypes;
    }

    /**
     * insert a ticket to the database, Please notice that buyer_id is not a
     * param that is because by default a ticket has no buyer when created.
     *
     * @param typeId int id of the type of ticket
     * @param name String name of ticket
     * @param location String location of ticket
     * @param seller_id int the id of the user that sells the ticket
     * @param price int price of ticket
     * @param date_of_ticket Date of ticket
     * @throws java.sql.SQLException
     * @throws exceptions.UserNotAuthorizedException if unauthorized user makes
     * request
     */
    public void insertTicket(int typeId, String name, String location, int seller_id, int price, Date date_of_ticket) throws SQLException, UserNotAuthorizedException {

        String query;

        // check if logged user is authorized to make request
        try {
            DbUtilities.isUserAuthorized(seller_id);
        } catch (UserNotFoundException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserNotAuthorizedException();
        }

        query = "INSERT INTO ticket (type_id, name, location, seller_id, price, date_of_ticket, is_sold, buyer_id)\n"
                + "VALUES (?, ?, ?, ?, ?, ?, false, null)";
        try {
            ps = connect().prepareStatement(query);
            ps.setInt(1, typeId);
            ps.setString(2, name);
            ps.setString(3, location);
            ps.setInt(4, seller_id);
            ps.setDouble(5, price);
            ps.setDate(6, new java.sql.Date(date_of_ticket.getTime()));
            ps.executeUpdate();
        } finally {
            disconnect();
        }
    }

    /**
     * insert a ticket to the database, Please notice that buyer_id is not a
     * param that is because by default a ticket has no buyer when created.
     *
     * @param typeId int id of the type of ticket
     * @param name String name of ticket
     * @param location String location of ticket
     * @param seller_id int the id of the user that sells the ticket
     * @param price int price of ticket
     * @param year string year YYYY
     * @param month string month MM
     * @param day string day DD
     *
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public void insertTicket(int typeId, String name, String location, int seller_id, int price, String year, String month, String day) throws SQLException, UserNotAuthorizedException {

        String query;

        // check if logged user is authorized to make request
        try {
            DbUtilities.isUserAuthorized(seller_id);
        } catch (UserNotFoundException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserNotAuthorizedException();
        }

        // convect the year/month/day strings to Date object
        Date date_of_ticket = getDateFromStrings(year, month, day);

        query = "INSERT INTO ticket (type_id, name, location, seller_id, price, date_of_ticket, is_sold, buyer_id)\n"
                + "VALUES (?, ?, ?, ?, ?, ?, false, null)";

        try {
            ps = connect().prepareStatement(query);
            ps.setInt(1, typeId);
            ps.setString(2, name);
            ps.setString(3, location);
            ps.setInt(4, seller_id);
            ps.setDouble(5, price);
            ps.setDate(6, date_of_ticket);
            ps.executeUpdate();
        } finally {
            disconnect();
        }
    }

    /**
     * update a ticket where id=ticketId in the database with params
     *
     * @param ticketId id of ticket
     * @param typeId int id of the type of ticket
     * @param name String name of ticket
     * @param location String location of ticket
     * @param seller_id int the id of the user that sells the ticket
     * @param price int price of ticket
     * @param year string year YYYY
     * @param month string month MM
     * @param day string day DD
     *
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public void updateTicket(int ticketId, int typeId, String name, String location, int seller_id, int price, String year, String month, String day) throws SQLException, UserNotAuthorizedException {

        String query;

        // check if logged user is authorized to make request
        try {
            DbUtilities.isUserAuthorized(seller_id);
        } catch (UserNotFoundException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserNotAuthorizedException();
        }

        Date date_of_ticket = getDateFromStrings(year, month, day);

        query = "INSERT INTO ticket (type_id, name, location, seller_id, price, date_of_ticket, is_sold, buyer_id)"
                + "VALUES (?, ?, ?, ?, ?, ?, false, null)";
        try {
            ps = connect().prepareStatement(query);
            ps.setInt(1, typeId);
            ps.setString(2, name);
            ps.setString(3, location);
            ps.setInt(4, seller_id);
            ps.setDouble(5, price);
            ps.setDate(6, java.sql.Date.valueOf(date_of_ticket.toString()));
            ps.executeUpdate();
        } finally {
            disconnect();
        }
    }

    /**
     * update a ticket where id=ticketId in the database with params
     *
     * @param ticketId
     * @param typeId int id of the type of ticket
     * @param name String name of ticket
     * @param location String location of ticket
     * @param seller_id int the id of the user that sells the ticket
     * @param price int price of ticket
     * @param date_of_ticket Date of ticket
     *
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     * @throws exceptions.UserNotAuthorizedException if unauthorized user makes
     * request
     */
    public void updateTicket(int ticketId, int typeId, String name, String location, int seller_id, int price, Date date_of_ticket) throws SQLException, UserNotAuthorizedException {

        String query;

        // check if logged user is authorized to make request
        try {
            DbUtilities.isUserAuthorized(seller_id);
        } catch (UserNotFoundException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserNotAuthorizedException();
        }

        query = "UPDATE ticket SET type_id = ?, name = ?, location = ?, seller_id = ?, price = ?, date_of_ticket = ? WHERE id = ? AND is_sold=false";
        try {
            ps = connect().prepareStatement(query);
            ps.setInt(1, typeId);
            ps.setString(2, name);
            ps.setString(3, location);
            ps.setInt(4, seller_id);
            ps.setDouble(5, price);
            ps.setDate(6, new java.sql.Date(date_of_ticket.getTime()));
            ps.setInt(7, ticketId);
            ps.executeUpdate();
        } finally {
            disconnect();
        }
    }

    /**
     * remove a specific ticket that belongs to a specific seller only if the
     * ticket was not sold out yet (if it is then the seller can't delete it).
     *
     * @param ticketId id of ticket in database
     * @param sellerId id of user that is seller in database
     * @throws UserNotAuthorizedException if not authorized to make request
     * @throws SQLException if had error executing query
     */
    public void removeTicket(int ticketId, int sellerId) throws UserNotAuthorizedException, SQLException {
        String query;
        // check if logged user is authorized to make request
        try {
            DbUtilities.isUserAuthorized(sellerId);
        } catch (UserNotFoundException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserNotAuthorizedException();
        }

        // TODO change ticket
        query = "DELETE FROM ticket WHERE id = " + ticketId + " AND seller_id = " + sellerId
                + " AND is_sold = false";

        try {
            ps = connect().prepareStatement(query);
            ps.executeUpdate();
        } finally {
            disconnect();
        }
    }

    /**
     * updates a tickets buyer in the database, this means that a ticket was
     * sold
     *
     * @param buyerId int id of buyer
     * @param ticketId int id of ticket
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public void updateTicketBuyer(int buyerId, int ticketId) throws SQLException {
        String query;

        query = "UPDATE ticket SET buyer_id = ?, is_sold = true WHERE id = ? AND is_sold=false";
        try {
            ps = connect().prepareStatement(query);
            ps.setInt(1, buyerId);
            ps.setInt(2, ticketId);
            ps.executeUpdate();
        } finally {
            disconnect();
        }
    }

    /**
     * returns true if the given ticket is reserved for another user and cannot
     * be payed for at this time.
     *
     * @param ticketId id of a ticket in db
     * @param userId id of user in db
     * @return Boolean value, true if ticket is reserved for another user false
     * otherwise
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    public Boolean isTicketRservedNotByUser(int ticketId, int userId) throws SQLException, NumberFormatException {
        String query;
        LocalDateTime now = LocalDateTime.now();
        now = now.minusMinutes(10);
        java.sql.Timestamp sqldate = java.sql.Timestamp.valueOf(now);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String currentTimestampMinusTenMinutes = Long.toString(timestamp.getTime() - (60 * 10));
        try {

            // TODO: QUERYT IS THE PROBLEM TIMESTAMP THROWS: java.sql.SQLDataException: The resulting value is outside the range for the data type INTEGER.
            // (600 * 1 SECOND) represents 10 minutes
            query = "SELECT * FROM reserved_tickets WHERE reserved_at >= '" + sqldate + "' AND ticket_id = ? AND user_id != ?";
            ps = connect().prepareStatement(query);
            //ps.setString(1, currentTimestampMinusTenMinutes);
            ps.setInt(1, ticketId);
            ps.setInt(2, userId);
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
     * inserts a ticket to be reserved by a user
     *
     * @param ticketId id of a ticket in db
     * @param userId id of user in db
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     * @throws exceptions.UserNotAuthorizedException if unauthorized user tries
     * to make request
     */
    public void insertReservedTicket(int ticketId, int userId) throws SQLException, UserNotAuthorizedException {
        String query;

        // check if logged user is authorized to make request
        try {
            DbUtilities.isUserAuthorized(userId);
        } catch (UserNotFoundException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
            throw new UserNotAuthorizedException();
        }
        // we want to delete all rows already reserved by user, a user may only 
        // reserve ONE TICKET at a time
        delReservedTicketsByUser(userId);

        query = "INSERT INTO reserved_tickets (user_id, ticket_id, reserved_at) "
                + " VALUES (" + userId + ", " + ticketId + ", CURRENT_TIMESTAMP)";

        try {
            ps = connect().prepareStatement(query);
            ps.executeUpdate();
        } finally {
            disconnect();
        }
    }

    /**
     * converts the year,month and day strings to a Date object.
     *
     * @param year string YYYY
     * @param month string MM
     * @param day string DD
     * @return Date object
     */
    private Date getDateFromStrings(String year, String month, String day) {
        // Construct a LocalDate object from the year, month, and day strings
        LocalDate localDate = LocalDate.parse(year + "-" + month + "-" + day, DateTimeFormatter.ISO_LOCAL_DATE);

        // Convert the LocalDate object to a java.sql.Date object
        return Date.valueOf(localDate);
    }

    /**
     * delete all rows in reserved ticket of a user.
     *
     * @param userId int id of user
     * @throws SQLException if an error occurs while executing the SQL
     * statement.
     */
    private void delReservedTicketsByUser(int userId) throws SQLException {
        String query = "DELETE FROM reserved_tickets WHERE user_id = ?";
        try {
            // TODO: QUERYT IS THE PROBLEM TIMESTAMP THROWS: java.sql.SQLDataException: The resulting value is outside the range for the data type INTEGER.
            // (600 * 1 SECOND) represents 10 minutes
            ps = connect().prepareStatement(query);
            //ps.setString(1, currentTimestampMinusTenMinutes);
            ps.setInt(1, userId);
            ps.executeUpdate();

        } finally {
            disconnect();
        }
    }

}
