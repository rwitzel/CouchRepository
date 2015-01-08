package com.github.rwitzel.couchrepository.api;

import java.lang.reflect.Proxy;

import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import com.github.rwitzel.couchrepository.internal.QueryMethodHandler;
import com.github.rwitzel.couchrepository.internal.ViewParamsMerger;

/**
 * Creates {@link CouchDbCrudRepository repositories} that automatically implement a specific interface. Comparable to
 * Spring Data's {@link RepositoryFactorySupport}.
 * 
 * @author rwitzel
 */
public class CouchDbCrudRepositoryFactory {

    /**
     * Creates a repository that implements the given interface.
     * 
     * @param underlyingRepository
     *            the underlying repository
     * @param customRepository
     *            An object that implements custom finder methods. Can be null.
     * @param repositoryType
     *            the interface of the returned repository
     * @param <T> the return type
     * @return Returns the created repository.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> T createRepository(CouchDbCrudRepository underlyingRepository, Object customRepository,
            Class<?> repositoryType) {

        QueryMethodHandler handler = new QueryMethodHandler(underlyingRepository, customRepository,
                new ViewParamsMerger());
        return (T) Proxy.newProxyInstance(repositoryType.getClassLoader(), new Class<?>[] { repositoryType }, handler);
    }

}
