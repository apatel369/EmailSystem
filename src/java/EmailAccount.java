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
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Pritam
 */
@ManagedBean
@SessionScoped
public class EmailAccount {

    /**
     * Creates a new instance of EmailAccount
     */
    private static String accountId, accountName;
    private String name;
    private String to;
    private String forwardTo;
    private String replyContent;
    private String title;
    private String content;
    private String notification;
    private ArrayList<Email> emails;
    private ArrayList<Email> sentEmails;
    private ArrayList<Email> trashEmails;
    private int emailIDtoView;
    private int sentEmailIDtoView;
    private int emailIDtoDelete;
    private int emailIDtoDeleteForever;
    private int deletedEmailIDtoView;
    private String emailDeletedFrom;
    final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/dsouzap4384";
    
    public EmailAccount(String aId, String aName) {
        accountId = aId;
        accountName = aName;
    }
    
    public EmailAccount(){
        emails = new ArrayList<Email>();
                
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null; 
        
        try{
            conn = DriverManager.getConnection(DB_URL, "dsouzap4384", "1558667");
            st = conn.createStatement();
            rs = st.executeQuery("select e.id, ea.name, e.title, e.content, e.dateandtime, e.type, e.notification from email e, emailaccount ea where ea.accountid = e.sender and e.receiver = '" + 
                    accountId + "' order by e.id desc");

            while(rs.next()){
                String emailTitle = "";
                    if(rs.getString(6).equals("New")){
                        emailTitle = rs.getString(3) + "(new)";
                    }else{
                        emailTitle = rs.getString(3);
                    }
                    emails.add(new Email(rs.getInt(1), rs.getString(2), emailTitle, rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)));
            }     
        }
        catch(SQLException e){
            e.printStackTrace();          
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
    
    public Email readSelectedEmail()
    {
        for (Email e: emails)
        {
            if (e.getId() == emailIDtoView)
            {
                Connection conn = null;
                Statement st = null;
                ResultSet rs = null;
                
                try{
                    conn = DriverManager.getConnection(DB_URL, "dsouzap4384", "1558667");
                    st = conn.createStatement();
                    String to = null, readBy = null;
                    if(e.getStatus().equals("New")){
                        int r = st.executeUpdate("update email set type = 'Read' where id = '" + emailIDtoView + "'");
                        rs = st.executeQuery("select * from email where id = '" + emailIDtoView + "'");
                        if(rs.next()){
                            if(rs.getString(8).equals("true")){
                                int n = st.executeUpdate("update email set notification = 'false' where id = '" + emailIDtoView + "'");
                                rs = st.executeQuery("select * from emailaccount where name = '" + e.getName() + "'");
                                if(rs.next()){
                                    to = rs.getString(1);
                                }

                                rs = st.executeQuery("select * from emailaccount where accountid = '" + accountId + "'");
                                if(rs.next()){
                                    readBy = rs.getString(2);
                                }

                                rs = st.executeQuery("select max(id) from email");
                                int id = 0;
                                if(rs.next()){
                                    id = rs.getInt(1) + 1;
                                }

                                int s = st.executeUpdate("insert into email values('" + 
                                        id + "', 'admin1', '" + to + "', 'Your message to " + readBy + " is read', 'Message Read is : " + e.getContent() + "', '" 
                                        + DateAndTime.DateTime() + "', 'New', 'false')");
                            }
                        }
                    }                                       
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
                finally{
                    try{
                        conn.close();
                        st.close();
                        rs.close();
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
                return e;
            }
        }
        return null;
    }

    public Email readSelectedSentEmail(){
        for (Email e: sentEmails)
        {
            if (e.getId() == sentEmailIDtoView)
            {
                return e;
            }
        }
        return null;
    }
    
    public Email readSelectedDeletedEmail(){
        for (Email e: trashEmails)
        {
            if (e.getId() == deletedEmailIDtoView)
            {
                return e;
            }
        }
        return null;
    }

    public String getInboxEmailIDtoDelete(int i) {
        emailIDtoDelete = i;
        emailDeletedFrom = "inbox";
        return "deleteConfirmation";
    }
    
    public String getSentEmailIDtoDelete(int i) {
        emailIDtoDelete = i;
        emailDeletedFrom = "sentbox";
        return "deleteConfirmation";
    }

    public void setEmailIDtoDelete(int emailIDtoDelete) {
        this.emailIDtoDelete = emailIDtoDelete;
    }

    public String getEmailIDtoDeleteForever(int i) {
        emailIDtoDeleteForever = i;
        return "deleteForeverConfirmation";
    }

    public void setEmailIDtoDeleteForever(int emailIDtoDeleteForever) {
        this.emailIDtoDeleteForever = emailIDtoDeleteForever;
    }
    
    public String getEmailIDtoView(int i) {
        emailIDtoView = i;
        return "viewEmail";
    }

    public void setEmailIDtoView(int emailIDtoView) {
        this.emailIDtoView = emailIDtoView;
    }

    public String getDeletedEmailIDtoView(int i) {
        deletedEmailIDtoView = i;
        return "viewDeletedEmail";
    }

    public void setDeletedEmailIDtoView(int deletedEmailIDtoView) {
        this.deletedEmailIDtoView = deletedEmailIDtoView;
    }
    
    public ArrayList<Email> getSentEmails() {
        return sentEmails;
    }

    public void setSentEmails(ArrayList<Email> sentEmails) {
        this.sentEmails = sentEmails;
    }

    public String getSentEmailIDtoView(int i) {
        sentEmailIDtoView = i;
        return "viewSentEmail";
    }

    public void setSentEmailIDtoView(int sentEmailIDtoView) {
        this.sentEmailIDtoView = sentEmailIDtoView;
    }
    
    public ArrayList<Email> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<Email> emails) {
        this.emails = emails;
    }
    
    public String sentEmails(){
            
        sentEmails = new ArrayList<Email>();
        
        //Database code to retrieve emails
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        
        try{
            conn = DriverManager.getConnection(DB_URL, "dsouzap4384", "1558667");
            st = conn.createStatement();
            rs = st.executeQuery("select e.id, ea.name, e.title, e.content, e.dateandtime, e.type, e.notification from email e, emailaccount ea where ea.accountid = e.receiver and e.sender = '" + 
                        accountId + "' order by e.id desc");
            
            while(rs.next()){
                    sentEmails.add(new Email(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)));
            }       
            
            return "sentbox";
        }
        catch(SQLException e){
            e.printStackTrace();
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
        return "internalError";
    }
    
    public String composeEmail() {
        System.out.println(notification);
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch(Exception e){
            e.printStackTrace();
            return ("internalError");
        }
        
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/dsouzap4384";
        
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        
        try{
            conn = DriverManager.getConnection(DB_URL, "dsouzap4384", "1558667");
            st = conn.createStatement();
            rs = st.executeQuery("select max(id) from email");
            int id = 0;
            if(rs.next()){
                id = rs.getInt(1) + 1;
            }      
            
            rs = st.executeQuery("select * from emailaccount where accountid = '" + 
                    to + "'");
            
            if(rs.next()){
                int r = st.executeUpdate("insert into email values('" + 
                            id + "', '" + accountId + "', '" + to + "', '" + title + "', '" +
                            content + "', '" + DateAndTime.DateTime() + "', 'New', '" +
                            notification + "')");
                return ("Your Email is sent");
            }
            else{
                return ("Email Sending Failed. Reason: Wrong Account ID.");
            }   
        }
        catch(SQLException e){
            e.printStackTrace();
            return ("Email Sending Failed");
        }
        finally{
            try{
                conn.close();
                st.close();
                rs.close();
            }
            catch(Exception e){
                e.printStackTrace();
                return ("internalError");
            }
        }
    } 
    
    public String deleteEmail(){
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        
        try{
            conn = DriverManager.getConnection(DB_URL, "dsouzap4384", "1558667");
            st = conn.createStatement();
            rs = st.executeQuery("select * from email where id = '" + emailIDtoDelete + "'");
            if(rs.next()){
                int r = st.executeUpdate("insert into emailtrash values('" + 
                            rs.getInt(1) + "', '" + rs.getString(2) + "', '" + rs.getString(3) + "', '" + rs.getString(4) + "', '" +
                            rs.getString(5) + "', '" + rs.getString(6) + "', '" + rs.getString(7) + "', '" + emailDeletedFrom + "')");
                
                int d = st.executeUpdate("delete from email where id = '" + emailIDtoDelete + "'");
                
                return "Email Deleted Successfully and moved to Delete Folder";
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
                return ("internalError");
            }
        }
        return null;
    }
    
    public String trashEmailsFolder(){
            
        trashEmails = new ArrayList<Email>();
        
        //Database code to retrieve emails
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        
        try{
            conn = DriverManager.getConnection(DB_URL, "dsouzap4384", "1558667");
            st = conn.createStatement();
            
            //Adding deleted emails send by email account to other accounts
            rs = st.executeQuery("select e.id, ea.name, e.title, e.content, e.dateandtime, e.type from emailtrash e, emailaccount ea where ea.accountid = e.receiver and e.sender = '" + 
                        accountId + "' and deletedFrom = 'sentbox' order by e.id desc");
            while(rs.next()){
                    trashEmails.add(new Email(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), ""));
            }       
            
            //Adding deleted emails send to email account from other accounts
            rs = st.executeQuery("select e.id, ea.name, e.title, e.content, e.dateandtime, e.type from emailtrash e, emailaccount ea where ea.accountid = e.sender and e.receiver = '" + 
                    accountId + "' and deletedFrom = 'inbox' order by e.id desc");
            while(rs.next()){
                trashEmails.add(new Email(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), ""));
            }
            return "trash";
        }
        catch(SQLException e){
            e.printStackTrace();
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
        return "internalError";
    }

    public String deleteForever(){
        Connection conn = null;
        Statement st = null;
       // ResultSet rs = null;
        
        try{
            conn = DriverManager.getConnection(DB_URL, "dsouzap4384", "1558667");
            st = conn.createStatement();
            int d = st.executeUpdate("delete from emailtrash where id = '" + emailIDtoDeleteForever + "'");
                
            return "Email Deleted Permanentely";
        }
        catch(SQLException e){
            e.printStackTrace();
            return "internalError";
        }
        finally{
            try{
                conn.close();
                st.close();
               // rs.close();
            }
            catch(Exception e){
                e.printStackTrace();
                return "internalError";
            }
        }
    }
    
    public String deleteAllForever(){
        Connection conn = null;
        Statement st = null;
       // ResultSet rs = null;
        
        try{
            conn = DriverManager.getConnection(DB_URL, "dsouzap4384", "1558667");
            st = conn.createStatement();
            int d = st.executeUpdate("delete from emailtrash where sender = '" + accountId + "' and deletedfrom = 'sentbox'");
            d = st.executeUpdate("delete from emailtrash where receiver = '" + accountId + "' and deletedfrom = 'inbox'");
                
            return "deleteAllConfirmation";
        }
        catch(SQLException e){
            e.printStackTrace();
            return ("internalError");
        }
        finally{
            try{
                conn.close();
                st.close();
                //rs.close();
            }
            catch(Exception e){
                e.printStackTrace();
                return ("internalError");
            }
        }
    }
    
    public String forwardSentEmail(){
        
        String title = "";
         System.out.println(notification);
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch(Exception e){
            e.printStackTrace();
            return ("internalError");
        }
        
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/dsouzap4384";
        
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        Statement st2 = null;
        ResultSet rs2 = null;
        
        try{
            conn = DriverManager.getConnection(DB_URL, "dsouzap4384", "1558667");
            st = conn.createStatement();
            st2 = conn.createStatement();
            
            int id = 0;                            
             rs = st.executeQuery("select * from email where id = '" + 
                            sentEmailIDtoView + "'");            
            if(rs.next()){
                rs2 = st2.executeQuery("select max(id) from email");
                if(rs2.next()){
                id = rs2.getInt(1) + 1;
                }                                    
                    if(!rs.getString(4).contains("FW: ")){
                        title =  "FW: " + rs.getString(4);
                    }
                    else{
                        title = rs.getString(4);
                    }
            
                int r = st2.executeUpdate("insert into email values('" + 
                                id + "', '" + rs.getString(2) + "', '" + to + "', '" + title + "', '" +
                                rs.getString(5) + "', '" + DateAndTime.DateTime() + "', 'New', '" + notification + "')");
                return ("Your Email is sent");
            }
            else{
                return ("Email Sending Failed. Reason: Wrong Account ID.");
            }   
        }
        catch(SQLException e){
            e.printStackTrace();
            return ("Email Sending Failed");
        }
        finally{
            try{
                        rs2.close();
                         rs.close();
                        st2.close();  
                        st.close();
                        conn.close();
            }
            catch(Exception e){
                e.printStackTrace();
                return ("internalError");
            }
        }
    }
    
    public String replySentEmail(){
        String title = "";
        System.out.println(notification);
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch(Exception e){
            e.printStackTrace();
            return ("internalError");
        }      
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/dsouzap4384";                 
                Connection conn = null;
                Statement st = null;
                ResultSet rs = null;
                Statement st2 = null;
                ResultSet rs2 = null;
                try{
                    conn = DriverManager.getConnection(DB_URL, "dsouzap4384", "1558667");
                    st = conn.createStatement();
                    st2 = conn.createStatement();
                    rs = st.executeQuery("select * from email where id = '" + 
                            sentEmailIDtoView + "'");
                       

                    if(rs.next()){
                    content = content + "<br/>-------------------------------------------------------------------<br/>" + rs.getString(5);    
                    rs2 = st2.executeQuery("select max(id) from email");
                    int id = 0;
                    if(rs2.next()){
                        id = rs2.getInt(1) + 1;
                    }
                       
                    if(!rs.getString(4).contains("RE: ")){
                        title = "RE: " + rs.getString(4);
                    }
                    else{
                        title = rs.getString(4);
                    }
                        int r = st2.executeUpdate("insert into email values('" + 
                            id + "', '" + rs.getString(2) + "', '" + rs.getString(3) + "', '" + title + "', '" +
                            content + "', '" + DateAndTime.DateTime() + "', 'New', '" + notification + "')");
                        return ("Email Sent"); 
                    }
                    else{
                        return ("Email Sent failed"); 
                    }                                                       
                }                                 
                catch(SQLException e){
                    e.printStackTrace();
                    return ("Email Sending Failed");
                }

                finally{
                    try{
                        rs2.close();
                         rs.close();
                        st2.close();  
                        st.close();
                        conn.close();                          
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        return ("internalError");
                    }
                }  
    }
    
    public String replyEmail()
    {
        System.out.println(notification);
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return ("internalError");
        }
        String title = "";
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/dsouzap4384";
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        
        try
        {
            conn = DriverManager.getConnection(DB_URL,"dsouzap4384", "1558667");
            st = conn.createStatement();
            rs = st.executeQuery("select max(id) from email");
            int id =0;
            if (rs.next())
            {
                id = rs.getInt(1) + 1;
            }
            rs1 = st.executeQuery("select * from email where id = '"+emailIDtoView+"'");
                              
            if (rs1.next())
            {
                if(!rs1.getString(4).contains("RE: ")){
                        title = "RE: " + rs1.getString(4);
                    }
                    else{
                        title = rs1.getString(4);
                    }
                int r = st.executeUpdate("insert into email values('"+id+"','"+accountId +"','"+rs1.getString(2)+"','"+ title+"','"+replyContent +"<br/>-------------------------------------------------------------------<br/>"+rs1.getString(5)+"','"+DateAndTime.DateTime()+"','"+"New"+"','"+notification+"')");
                return ("Your Replied Email is sent");
            }
            else{
                return ("Replied Email Sending Failed. Please try again!");
            } 
            
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                st.close();
                rs1.close();
                rs.close();
                conn.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public String forwardInboxEmail()
    {
        System.out.println(notification);
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return ("internalError");
        }
        String title = "";
        final String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/dsouzap4384";
        Connection conn = null;
        Statement st = null;
        Statement st1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        
        try
        {
            conn = DriverManager.getConnection(DB_URL,"dsouzap4384", "1558667");
            st = conn.createStatement();
            st1 = conn.createStatement();
            rs = st.executeQuery("select max(id) from email");
            int id = 0;
            if(rs.next()){
                id = rs.getInt(1) + 1;
            }      
            
            rs = st.executeQuery("select * from emailaccount where accountid = '" + 
                    forwardTo + "'");
            
            if(rs.next()){
                rs1 = st1.executeQuery("select * from email where id = '"+emailIDtoView+"'");
                
                
                if (rs1.next())
                {
                    if(!rs1.getString(4).contains("FW: ")){
                        title =  "FW: " + rs1.getString(4);
                    }
                    else{
                        title = rs1.getString(4);
                    }
                    int r = st.executeUpdate("insert into email values('"+
                            id+"','"+accountId +"','"+forwardTo+"','"+ title+"','"+
                            rs1.getString(5)+"','"+DateAndTime.DateTime()+"','"+"New"+"','"+
                            notification+"')");
                    return ("Your Forwarded Email is sent");
                }
                else{
                    return ("Email Sending Failed.");
                } 
            }          
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                st.close();
                rs1.close();
                rs.close();
                conn.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
       
    public ArrayList<Email> getTrashEmails() {
        return trashEmails;
    }

    public void setTrashEmails(ArrayList<Email> trashEmails) {
        this.trashEmails = trashEmails;
    }
    
    public static String getAccountId() {
        return accountId;
    }

    public static void setAccountId(String accountId) {
        EmailAccount.accountId = accountId;
    }

    public static String getAccountName() {
        return accountName;
    }

    public static void setAccountName(String accountName) {
        EmailAccount.accountName = accountName;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getName() {
        return accountName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailDeletedFrom() {
        return emailDeletedFrom;
    }

    public void setEmailDeletedFrom(String emailDeletedFrom) {
        this.emailDeletedFrom = emailDeletedFrom;
    }

    public String getForwardTo() {
        return forwardTo;
    }

    public void setForwardTo(String forwardTo) {
        this.forwardTo = forwardTo;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }
    
    
}
