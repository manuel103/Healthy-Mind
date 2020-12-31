package com.example.healthymind.auth;

public class UserHelper {
    String name, username, email, phoneNo, password, doc_id;

    public UserHelper() {

    }

    public UserHelper(String name, String username, String email, String phoneNo, String password, String doc_id) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
        this.doc_id = doc_id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDocId() {
        return doc_id;
    }

    public void setDocId(String doc_id) {
        this.name = doc_id;
    }


}
