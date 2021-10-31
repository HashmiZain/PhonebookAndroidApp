package com.zain.phonebook10;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

//Class to hold contact information
public class Contact {
    private int userId;
    private int contactId;
    private String contactName;
    private String contactNumber;
    private String contactEmail;
    private String contactStatus;
    private String contactPicture;

    Contact(int contactId,int userId,String contactName, String contactNumber, String contactEmail, String contactStatus,String contactPicture){
        this.contactId = contactId;
        this.userId = userId;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactEmail = contactEmail;
        this.contactStatus = contactStatus;
        this.contactPicture = contactPicture;
    }

    Contact(int userId,String contactName, String contactNumber, String contactEmail, String contactStatus,String contactPicture){
        this.userId = userId;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactEmail = contactEmail;
        this.contactStatus = contactStatus;
        this.contactPicture = contactPicture;
    }

    public int getContactId() {
        return contactId;
    }
    public int getuserId() {
        return userId;
    }
    public String getcontactName() {
        return contactName;
    }
    public String getcontactNumber() {
        return contactNumber;
    }
    public String getcontactEmail() {
        return contactEmail;
    }
    public String getcontactStatus() {
        return contactStatus;
    }
    public String getContactPicture() {
        return contactPicture;
    }

}
