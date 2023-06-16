/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uitilities;

import database.TicketQueries;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import models.TicketType;

/**
 *TicketTypesBean reads all the bean types at construction and than holds
 * a list with all ticket types
 * @author amir
 */
@ManagedBean(name = "TicketTypesBean")
@SessionScoped
public class TicketTypesBean {
    private List<TicketType> types;
    
    //called after the constructor to initiate the types list with values from the database
    @PostConstruct
    public void init() {
        TicketQueries tq=new TicketQueries();
        try {
            types = tq.getAllTicketType();
        } catch (SQLException ex) {
            Logger.getLogger(TicketTypesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<TicketType> getTypes() {
        return types;
    }
}
