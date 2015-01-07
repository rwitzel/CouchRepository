package com.github.rwitzel.couchrepository.api.viewresult;

/**
 * This class assists in testing views.
 * 
 * @author rwitzel
 */
public class ProductSummary {

    private ProductFacts facts;

    public ProductFacts getFacts() {
        return facts;
    }

    public void setFacts(ProductFacts facts) {
        this.facts = facts;
    }
}
