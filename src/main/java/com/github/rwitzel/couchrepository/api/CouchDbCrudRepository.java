package com.github.rwitzel.couchrepository.api;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

/**
 * This {@link CrudRepository} for CouchDB databases allows you to query views.
 * 
 * @author rwitzel
 * @param <T> See type parameter in {@link CrudRepository}
 * @param <ID> See type parameter in {@link CrudRepository}
 */
public interface CouchDbCrudRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {

    /**
     * Queries the database with the given parameters.
     * 
     * @param viewParams the query parameters
     * @param <R> the return type, depends on {@link ViewParams#getReturnType()}.
     * @return Returns the result of the query. The type of the return value depends on {@link ViewParams#getReturnType()}.
     */
    <R> R find(ViewParams viewParams);

}
