/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author OmerMeirovich
 * This exeption will be thrown when a row in the users table in DB tried to be
 * inserted but another row with same email already exists.
 */
public class UserExistsException extends Exception{
    public UserExistsException(){
        super("User already exists!");
    }
}
