package com.github.rwitzel.couchrepository.model;


/**
 * Represents a manufacturer description, not a manufacturer.
 * 
 * @author rwitzel
 */
public class Manufacturer extends BaseDocument {

    private String address;

    private String commercialRegisterCode;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCommercialRegisterCode() {
        return commercialRegisterCode;
    }

    public void setCommercialRegisterCode(String commercialRegisterCode) {
        this.commercialRegisterCode = commercialRegisterCode;
    }
}
