package com.github.rwitzel.couchrepository.api;

import java.util.List;

import javax.inject.Named;

import com.github.rwitzel.couchrepository.model.Comment;
import com.github.rwitzel.couchrepository.model.Product;

public interface ProductRepository extends CouchDbCrudRepository<Product, String>, ProductRepositoryCustom {

    ViewResult findByComment(@Named("key") Object[] key, @Named("descending") Boolean descending,
            @Named("viewParams") ViewParams viewParams, @Named("valueType") Class<?> valueType);

    ViewResult findByComment();

    List<Comment> findByComment(@Named("key") Object[] key, @Named("viewParams") ViewParams viewParams);
}
