package com.example.testwork.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Entity
public class GuestBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message="Name is required")
    private String userName;
    @Pattern(regexp="^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$", message="e-mail must be corrected")
    private String eMail;
    @NotBlank(message="Text is required")
    private String text;
    private String ip;
    private String browser;
    private Date date;

    public GuestBook(@NotBlank(message = "Name is required") String userName, @Pattern(regexp = "^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$", message = "e-mail must be corrected") String eMail, @NotBlank(message = "Text is required") String text, String ip, String browser) {
        this.userName = userName;
        this.eMail = eMail;
        this.text = text;
        this.ip = ip;
        this.browser = browser;
    }

    public GuestBook() {
    }

    @PrePersist
    void date(){
        this.date = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "GuestBook{" +
                "userName='" + userName + '\'' +
                ", eMail='" + eMail + '\'' +
                ", text='" + text + '\'' +
                ", ip='" + ip + '\'' +
                ", browser='" + browser + '\'' +
                ", date=" + date +
                '}';
    }
}

