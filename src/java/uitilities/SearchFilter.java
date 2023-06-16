/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uitilities;


import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Date;
import javax.el.ELContext;
import javax.faces.context.FacesContext;

/**
 *SearchFilter holds all the information for a filtered search request 
 * @author amir
 */
@ManagedBean(name = "SearchFilter")
@SessionScoped
public class SearchFilter {
    private int minPrice;
    private int maxPrice;
    //type of the ticket
    private int type=-1;
    private String location="";
    private String name="";
    //starting date to filter
    private Date fromDate;
    //end date to filter
    private Date toDate;
    
    /**
     * resets on the SearchFilter variables to default values
     */
    public static void resetFilter(){
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        SearchFilter filter = (SearchFilter)FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "SearchFilter");
        filter.fromDate=null;
        filter.toDate=null;
        filter.name="";
        filter.location="";
        filter.maxPrice=0;
        filter.minPrice=0;
        filter.type=-1;
    }
    
    /**
     * returns a Where conditions query with all the filltered parameter
     * @param defaultChecks a string containing conditions outside of the filter
     * @return  string with all the condition combined with one where query
     */
    public static String getFilteredQuery(String defaultChecks){
        //get instance of SearchFilter in session
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        ErrorMessage errMsg = (ErrorMessage)FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "ErrorMessage");
        errMsg.setText("");
        
        SearchFilter filter = (SearchFilter)FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "SearchFilter");
        
        //get defualt conditions 
        String query=" WHERE ";
        query+=defaultChecks;
        //a list for validating some of the parmeters
        ArrayList<String> params=new ArrayList<>();
        //check if field needs to be filtered or ignored(if empty)
        if(!filter.getName().equals("")){ //check name
            params.add(filter.getName());
            query+=" AND UPPER(t.name) LIKE UPPER('%"+filter.getName()+"%') ";
        }
        if(!filter.getLocation().equals("")){//check location
            params.add(filter.getLocation());
            query+=" AND UPPER(LOCATION) LIKE UPPER('%"+filter.getLocation()+"%') ";
        }
        if(filter.getMaxPrice()!=0){//check max price
            query+=" AND PRICE <= "+filter.getMaxPrice();
        }
        if(filter.getMinPrice()!=0){//check minimum price
            query+=" AND PRICE >= "+filter.getMinPrice();
        }
        if(filter.getType() >=0){ //check type
            query+=" AND TYPE_ID = "+filter.getType();
        }
        if(filter.getFromDate()!=null){ //check starting date
            //convert to sql format
            java.sql.Date sqlDate=new java.sql.Date(filter.getFromDate().getTime());
            query+=" AND DATE_OF_TICKET >= '"+sqlDate.toString()+"' ";
        }
        if(filter.getToDate()!=null){ //check end date
            //convert to sql format
            java.sql.Date sqlDate=new java.sql.Date(filter.getToDate().getTime());
            query+=" AND DATE_OF_TICKET <= '"+sqlDate.toString()+"' ";
        }
        
        if(!DbUtilities.isValidParams(params)){ //check that params does not contains sql injection
            errMsg.setText("Invalid charecter in filter (field name or location)");
            return null;
        }
        
        
        return query;
    }
    
    ////////////////////getters and setters//////////////////////
    
    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
   
    
    
    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
}
