/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author HP
 */
@ManagedBean
@SessionScoped
public class Login implements Serializable {

    
    //attributes
    private String id;
    private String password;
    private EmailAccount loggedInAccount = null;

    
    //get methods and set methods
    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    

    public String login() {
          //load the Driver
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //return to internalError.xhtml
            return ("internalError");
        }
        
        
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/dsouzap4384";
        
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null; 
        
        try{
            conn = DriverManager.getConnection(DB_URL, "dsouzap4384", "1558667");
            st = conn.createStatement();
            rs = st.executeQuery("select * from EmailAccount where AccountID = '" + 
                    id + "'");
            
            if(rs.next()){
                if(password.equals(rs.getString(3))){
                    loggedInAccount = new EmailAccount(rs.getString(1), rs.getString(2));                               
                    return "welcome?faces-redirect=true";
                }
                else{
                    id = "";
                    password = "";
                    return "loginNotOk";
                }
            }
            else{
                id = "";
                password = "";
                return "loginNotOk";
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            return ("internalError");
        }
        
        finally{
            try{
                conn.close();
                st.close();
                rs.close();
            } 
            catch(Exception e){
                e.printStackTrace();
                
            } 
        }       
    }
    
    public String logOut()
    {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index.xhtml";

        
    }
}
