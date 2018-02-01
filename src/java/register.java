/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Hang_Tran
 */
@ManagedBean
@RequestScoped
public class register {
    private String accountID;
    private String password;
    private String name;
    
    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String register() {
        //load the driver
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            
        }
        catch (Exception e)
        {
            return ("Internal Error! Please try again later.");
        }
        
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/dsouzap4384";
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
                                  
        try{
            conn = DriverManager.getConnection(DB_URL, "dsouzap4384", "1558667");
            st = conn.createStatement();

            rs = st.executeQuery("select * from EmailAccount where AccountID = '" +
                    accountID + "'");

            if(rs.next()){
                return ("AccountID should be unique.Please enter a valid Account ID");                           
                
            }else{
                int r = st.executeUpdate("insert into EmailAccount values('" +
                    accountID + "', '" + name + "', '" + password + "')");
                return ("Congratulations " + name + " ! Your Email Account has been Successfully Created");
            }                      
        }catch(SQLException e){
            e.printStackTrace();
            return ("Internal Error! Please try again later.");
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
}
