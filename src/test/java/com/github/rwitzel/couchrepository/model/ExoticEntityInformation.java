package com.github.rwitzel.couchrepository.model;

import java.util.ArrayList;
import java.util.List;

import com.github.rwitzel.couchrepository.api.EntityInformation;
import com.github.rwitzel.couchrepository.ektorp.EktorpEntityInformation;

/**
 * An {@link EktorpEntityInformation} for {@link Exotic} entities.
 * 
 * @author rwitzel
 */
public class ExoticEntityInformation implements EntityInformation<Exotic, ExoticId> {

    @Override
    public String toCouchId(ExoticId id) {
        return id.toCouchId();
    }

    @Override
    public List<String> toCouchIds(Iterable<ExoticId> iter) {
        List<String> list = new ArrayList<String>();
        for (ExoticId item : iter) {
            list.add(toCouchId(item));
        }
        return list;
    }

    @Override
    public String getCouchId(Exotic entity) {
        return entity.getComputedKey();
    }

    @Override
    public String getRev(Exotic entity) {
        return entity.getVersion();
    }

    @Override
    public void setId(Exotic entity, String couchId) {
        entity.setComputedKey(couchId);
        entity.setId(new ExoticId(couchId));
    }

    @Override
    public void setRev(Exotic entity, String rev) {
        entity.setVersion(rev);
    }

    @Override
    public boolean isNew(Exotic entity) {
        return getRev(entity) == null;
    }

}
