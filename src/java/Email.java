/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Pritam
 */
public class Email {
    private int id;
    private String name;
    private String title;
    private String content;
    private String date;
    private String status;
    private String notification;
    
    public Email(int i, String n, String t, String c, String d, String s, String nf){
        id = i;
        name = n;
        title = t;
        content = c;
        date = d;
        status = s;
        notification = nf;
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

    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
     
    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
