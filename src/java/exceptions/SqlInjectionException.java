/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author OmerMeirovich
 * This exception will be thrown only if the parameters we check has symbols
 * that can cause an sql injection: '-', '/' , '+' etc...
 * For example if we call a function that has an email parameter and
 * email = "-32ksja" then this exception will be thrown.
 * 
 * NOTE - this exception WILL NOT be thrown if parameter is too short/long etc..
 */
public class SqlInjectionException extends Exception  {
    public SqlInjectionException(String msg){
        super(msg);
    }
}
