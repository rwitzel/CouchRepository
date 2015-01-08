package com.github.rwitzel.couchrepository.api;

import java.io.Serializable;
import java.util.List;

/**
 * This class allows access to the ID and the revision of entities of a specific type.
 * 
 * @author rwitzel
 * @param <T>
 *            the type of the handled entities
 * @param <ID>
 *            the type of the ID of the handled entities
 */
public interface EntityInformation<T, ID extends Serializable> {

    /**
     * Converts the given ID to a CouchDB identifier.
     * 
     * @param id the ID of the entity 
     * @return Returns the transformed identifier.
     */
    String toCouchId(ID id);

    List<String> toCouchIds(Iterable<ID> iter);

    /**
     * @param entity the entity
     * @return Returns the CouchDb identifier of the entity.
     */
    String getCouchId(T entity);

    /**
     * Sets the identifier of the entity, i.e. transforms the given CouchDb identifier if necessary.
     * 
     * @param entity the entity
     * @param couchId the CouchID for the entity
     */
    void setId(T entity, String couchId);

    /**
     * @param entity the entity
     * @return Returns the revision of the given entity.
     */
    String getRev(T entity);

    /**
     * Sets the revision in the given entity.
     * 
     * @param entity the entity
     * @param rev the new revision for the entity 
     */
    void setRev(T entity, String rev);

    /**
     * @param entity the entity
     * @return Returns true if the given entity is new, i.e. it does not have a revision.
     */
    boolean isNew(T entity);
}
