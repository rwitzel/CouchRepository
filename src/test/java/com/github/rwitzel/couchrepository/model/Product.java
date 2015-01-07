package com.github.rwitzel.couchrepository.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a product description, not a product.
 * 
 * @author rwitzel
 */
public class Product extends BaseDocument {

    private Date lastModification;

    private String manufacturerId;

    private String text;

    private ProductRating rating;

    private boolean hidden;

    private Integer numBuyers;

    private double weight;

    private BigDecimal price;

    private List<String> tags;

    private List<Comment> comments;
    
    private int isoProductCode;

    public Date getLastModification() {
        return lastModification;
    }

    public void setLastModification(Date lastModification) {
        this.lastModification = lastModification;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ProductRating getRating() {
        return rating;
    }

    public void setRating(ProductRating rating) {
        this.rating = rating;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Integer getNumBuyers() {
        return numBuyers;
    }

    public void setNumBuyers(Integer numBuyers) {
        this.numBuyers = numBuyers;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<String> getTags() {
        if (tags == null) {
            return new ArrayList<String>();
        }
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Comment> getComments() {
        if (comments == null) {
            comments = new ArrayList<Comment>();
        }
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public int getIsoProductCode() {
        return isoProductCode;
    }

    public void setIsoProductCode(int isoProductCode) {
        this.isoProductCode = isoProductCode;
    }
}
