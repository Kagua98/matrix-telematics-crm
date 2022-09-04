package crm.matrixtelematic.com.data.entity;

import javax.persistence.Entity;

@Entity
public class Clients extends AbstractEntity {

    private String clientName;
    private String kraPin;
    private String contact;
    private String address;

    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public String getKraPin() {
        return kraPin;
    }
    public void setKraPin(String kraPin) {
        this.kraPin = kraPin;
    }
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

}
