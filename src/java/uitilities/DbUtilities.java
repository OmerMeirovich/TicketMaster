/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uitilities;

import exceptions.UserNotFoundException;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author OmerMeirovich 
 * 
 * Handles extra helpful functions used in queries functions.
 */
public class DbUtilities {

    private final static char[] alphabetAndDigits = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private final static int tokenLength = 10;
    private final static char[] forbiddenSqlChars = "|-+*/:'$".toCharArray();

    public static String generateRememberToken() {
        String token = "";
        Random rnd = new Random();
        for (int i = 0; i < tokenLength; i++) {
            token += alphabetAndDigits[rnd.nextInt(alphabetAndDigits.length)];
        }
        return token;
    }

    // returns boolean if an array of strings are valid (avoid sql injections)
    public static Boolean isValidParams(String[] arr) {
        for (String param : arr) {
            // check if the word is white-listed then it won't check for sql injection
            if (isStrInWhiteList(param)) {
                continue;
            }
            if (param == null) {
                continue;
            }
            for (char c : forbiddenSqlChars) {
                if (param.contains(Character.toString(c))) {
                    return false;
                }
            }
        }
        return true;
    }

    // returns boolean if an array of strings are valid (avoid sql injections)
    public static Boolean isValidParams(ArrayList<String> arr) {
        String[] stringArr = new String[arr.size()];
        for (int i = 0; i < stringArr.length; i++) {
            stringArr[i] = arr.get(i);
        }
        return isValidParams(stringArr);
    }

    /**
     * check if given userId is authorized to make an action (matches the user
     * that is logged in).
     *
     * @param userId id of user that wants to make an action
     * @return true if user is authorized and false otherwise
     * @throws exceptions.UserNotFoundException if a user is not logged in
     */
    public static Boolean isUserAuthorized(int userId) throws UserNotFoundException {
        InternalStorageManager manager = InternalStorageManager.getInstance();
        return manager.getUserId() == userId;
    }

    /**
     * check if the string is in the format of NN/NN where N is a number.
     *
     * @param dateFormat string represents date foramt
     * @return boolean value
     */
    private static Boolean isDateFormat(String dateFormat) {
        if (dateFormat.length() < 5) {
            return false;
        }

        return Character.isDigit(dateFormat.charAt(0)) && Character.isDigit(dateFormat.charAt(1))
                && (dateFormat.charAt(2) == '/') && Character.isDigit(dateFormat.charAt(3)) && Character.isDigit(dateFormat.charAt(4));
    }

    /**
     * here we want to appends any function that gives a pass to a word that
     * usually throws an exception because of any symbols.
     *
     * @param str represents a word for check
     * @return boolean value
     */
    private static Boolean isStrInWhiteList(String str) {
        return isDateFormat(str);
    }
}
