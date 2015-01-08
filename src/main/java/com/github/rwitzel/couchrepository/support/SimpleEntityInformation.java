package com.github.rwitzel.couchrepository.support;

import com.github.rwitzel.couchrepository.api.EntityInformation;

/**
 * This {@link EntityInformation} is only for entities with ID of type <i>String</i>.
 * <p>
 * It looks up the ID and the revision in the {@link #propId} resp. {@link #propRev}.
 * 
 * @author rwitzel
 *
 * @param <T> the type of the handled entities
 */
public class SimpleEntityInformation<T> extends GenericEntityInformation<T, String>{
    
    private String propId;
    
    private String propRev;
    
    public SimpleEntityInformation(Class<T> type, String propId, String propRev) {
        super(type, String.class);
        this.propId = propId;
        this.propRev = propRev;
    }

    @Override
    public String toCouchId(String id) {
        return id;
    }

    @Override
    public String getCouchId(T entity) {
        return (String) getPropertyValue(entity, true, propId);
    }

    @Override
    public String getRev(T entity) {
        return (String) getPropertyValue(entity, true, propRev);
    }

    @Override
    public void setId(T entity, String couchId) {
        setPropertyValue(entity, couchId, null, propId);
    }

    @Override
    public void setRev(T entity, String rev) {
        setPropertyValue(entity, rev, null, propRev);
    }

    @Override
    public boolean isNew(T entity) {
        return getRev(entity) == null;
    }


}
