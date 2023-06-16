/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author OmerMeirovich
 * This exception will be thrown in case any query we ran on the database has
 * returned empty, For example: we check if email and password are in the database,
 * if no record has returned then this exception will be thrown.
 */
public class SqlNullQueryException extends Exception {
    public SqlNullQueryException(String msg){
        super(msg);
    }
}
