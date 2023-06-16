/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author OmerMeirovich
 * This exception is thrown in case a user tried to request and run any function
 * that he is not authorized to for example delete a ticket that is not his.
 */
public class UserNotAuthorizedException extends Exception{
    public UserNotAuthorizedException(){
        super("User is not authorized to make this request");
        }
}
