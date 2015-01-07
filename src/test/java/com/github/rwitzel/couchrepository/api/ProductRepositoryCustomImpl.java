package com.github.rwitzel.couchrepository.api;

import java.util.List;

import com.github.rwitzel.couchrepository.model.Product;

/**
 * A repository with custom methods.
 * 
 * @author rwitzel
 */
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private CouchDbCrudRepository<Product, String> repository;

    public ProductRepositoryCustomImpl(CouchDbCrudRepository<Product, String> repository) {
        super();
        this.repository = repository;
    }

    @Override
    public List<Product> findByComment(Object[] key, Boolean descending) {

        ViewParams params = new ViewParams();
        params.setReduce(false);
        params.setIncludeDocs(true);
        params.setReturnType("doc");
        params.setDocumentType(Product.class);
        params.setValueType(Object.class);
        params.setKeyType(Object.class);
        params.setView("findByComment");

        params.setKey(key);
        params.setDescending(descending);

        return repository.find(params);
    }
    
    @Override
    public List<Product> throwException(Object[] key, Boolean descending) {
        throw new UnsupportedOperationException("this exception is thrown to test exception unwrapping");
    }

}
