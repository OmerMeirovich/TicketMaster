/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author OmerMeirovich
 * This expcetion will be thrown if a row was searched for in the users table in 
 * the DB with given email and token but was not found.
 */
public class UserNotFoundException extends Exception{
    public UserNotFoundException(){
        super("User not found in database");
        }
}
