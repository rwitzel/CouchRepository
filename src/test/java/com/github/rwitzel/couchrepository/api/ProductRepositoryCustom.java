package com.github.rwitzel.couchrepository.api;

import java.util.List;

import com.github.rwitzel.couchrepository.model.Product;

public interface ProductRepositoryCustom {

    List<Product> findByComment(Object[] key, Boolean descending);

    List<Product> throwException(Object[] key, Boolean descending);

}
