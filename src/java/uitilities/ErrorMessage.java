/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uitilities;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * used to contain error messages
 * @author amir
 */
@ManagedBean(name = "ErrorMessage")
@SessionScoped
public class ErrorMessage {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
