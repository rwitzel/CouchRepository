package com.github.rwitzel.couchrepository.api;

import java.util.List;

import com.github.rwitzel.couchrepository.model.Comment;
import com.github.rwitzel.couchrepository.model.Product;

public interface ProductRepository extends CouchDbCrudRepository<Product, String>, ProductRepositoryCustom {

    ViewResult findByComment(Object[] key, Boolean descending, ViewParams viewParams,
            Class<?> valueType);

    ViewResult findByComment();

    List<Comment> findByComment(Object[] key, ViewParams viewParams);
}
