package com.github.rwitzel.couchrepository.ektorp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ektorp.util.Documents;

import com.github.rwitzel.couchrepository.api.EntityInformation;

/**
 * This {@link EntityInformation} is only for entities with ID of type <i>String</i>.
 * 
 * @author rwitzel
 */
public class EktorpEntityInformation<T, ID extends Serializable> implements EntityInformation<T, ID> {

    @Override
    public String toCouchId(ID id) {
        if (id == null) {
            return null;
        } else if (id instanceof CharSequence) {
            return ((CharSequence) id).toString();
        } else {
            throw new RuntimeException("unsupported type. Should you use another implementation of "
                    + EntityInformation.class + "?");
        }
    }
    
    @Override
    public List<String> toCouchIds(Iterable<ID> iter) {
        List<String> list = new ArrayList<String>();
        for (ID item : iter) {
            list.add(toCouchId(item));
        }
        return list;
    }

    @Override
    public String getCouchId(T entity) {
        return Documents.getId(entity);
    }
    
    @Override
    public String getRev(T entity) {
        return Documents.getRevision(entity);
    }

    @Override
    public void setId(T entity, String couchId) {
        throw new UnsupportedOperationException("not needed. therefore, not implemented");
    }

    @Override
    public void setRev(T entity, String rev) {
        throw new UnsupportedOperationException("not needed. therefore, not implemented");
    }

    public boolean isNew(T entity) {
        return Documents.isNew(entity);
    }

}
