package com.github.rwitzel.couchrepository.cloudant;

import static com.github.rwitzel.couchrepository.internal.AdapterUtils.toList;
import static com.github.rwitzel.couchrepository.internal.AdapterUtils.transformViewResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.View;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import com.github.rwitzel.couchrepository.api.CouchDbCrudRepository;
import com.github.rwitzel.couchrepository.api.EntityInformation;
import com.github.rwitzel.couchrepository.api.ViewParams;
import com.github.rwitzel.couchrepository.api.ViewResult;
import com.github.rwitzel.couchrepository.api.ViewResultRow;
import com.github.rwitzel.couchrepository.api.exceptions.BulkOperationError;
import com.github.rwitzel.couchrepository.api.exceptions.BulkOperationException;
import com.github.rwitzel.couchrepository.support.GenericEntityInformation;

/**
 * This implementation of {@link CouchDbCrudRepository} uses Cloudant's {@link Database}.
 * <p>
 * This implementation requires a special view in a design document. EXAMPLE: <code>
  "views" : {
    "by_id" : {
      "map" : "function(doc) { if(doc.type == '...') {emit(doc._id, { _id : doc._id, _rev: doc._rev } )} }",
      "reduce" : "_count"
    }
  }
 </code>
 * <p>
 * Take care when using {@link #deleteAll()} because it loads the IDs and revisions of all documents of the
 * abovementioned view at once, then deletes the documents.
 * 
 * @author rwitzel
 */
public class CloudantCrudRepository<T, ID extends Serializable> implements CouchDbCrudRepository<T, ID> {

    protected Logger logger = LoggerFactory.getLogger(CloudantCrudRepository.class);

    protected CloudantViewConfigurer viewBuilder = new CloudantViewConfigurer();

    protected Class<T> type;

    protected EntityInformation<T, ID> ei;

    protected Database couchDbClient;
    
    @SuppressWarnings("unchecked")
    public CloudantCrudRepository(Class<T> type, Database couchDbClient) {
        this(type, false, couchDbClient, new GenericEntityInformation<T, ID>(type, (Class<ID>) String.class));
    }

    public CloudantCrudRepository(Class<T> type, boolean allOrNothing, Database couchDbClient,
            EntityInformation<T, ID> ei) {
        super();
        
        Validate.isTrue(!allOrNothing, "allOrNothing = true is not available by the Cloudant driver");
        this.type = type;
        this.ei = ei;
        this.couchDbClient = couchDbClient;
    }

    public <S extends T> S save(S entity) {

        Assert.notNull(entity, "The given entity must not be null.");

        Response response;
        if (ei.isNew(entity)) {
            response = couchDbClient.save(entity);
        } else {
            response = couchDbClient.update(entity);
        }

        handleResponse(response, entity);

        return entity;
    }

    public <S extends T> Iterable<S> save(Iterable<S> entities) {

        Assert.notNull(entities, "The given list of entities must not be null.");

        executeBulk(toList(entities), true);

        return entities;
    }

    public T findOne(ID id) {

        Assert.notNull(id, "The given ID must not be null.");

        try {
            return couchDbClient.find(type, ei.toCouchId(id));
        } catch (NoDocumentException e) {
            logger.debug("document with ID " + id + " not found", e);
            return null;
        }
    }

    public boolean exists(ID id) {

        Assert.notNull(id, "The given ID must not be null.");

        return couchDbClient.contains(ei.toCouchId(id));
    }

    public Iterable<T> findAll() {
        return couchDbClient.view(designDocument() + "by_id").reduce(false).includeDocs(true).query(type);
    }

    public Iterable<T> findAll(Iterable<ID> ids) {

        Assert.notNull(ids, "The given list of IDs must not be null.");

        return couchDbClient.view(designDocument() + "by_id").keys(ei.toCouchIds(ids)).reduce(false).includeDocs(true)
                .query(type);
    }

    public long count() {
        @SuppressWarnings("rawtypes")
        List<Map> results = couchDbClient.view(designDocument() + "by_id").reduce(true).query(Map.class);
        if (results.size() == 0) {
            return 0;
        } else {
            return ((Number) results.get(0).get("value")).longValue();
        }
    }

    @SuppressWarnings("rawtypes")
    public void delete(ID id) {

        Assert.notNull(id, "The given ID must not be null.");

        View view = couchDbClient.view(designDocument() + "by_id");
        List<Map> results = view.key(ei.toCouchId(id)).reduce(false).includeDocs(false).query(Map.class);
        if (results.size() != 0) {
            Map result = (Map) results.get(0).get("value");
            Response response = couchDbClient.remove((String) result.get("_id"), (String) result.get("_rev"));
            handleResponse(response, null);
        }
    }

    public void delete(T entity) {

        Response response = couchDbClient.remove(entity);

        handleResponse(response, null);
    }

    /**
     * @param response the response
     * @param entity Null if the entity shall not be updated
     */
    protected void handleResponse(Response response, T entity) {
        if (response.getError() != null) {
            throw new RuntimeException(String.format("error: %s, reason: %s, entity id: %s, rev: %s",
                    response.getError(), response.getReason(), response.getId(), response.getRev()));
        } else if (entity != null) {
            if (ei.isNew(entity)) {
                ei.setId(entity, response.getId());
            }
            ei.setRev(entity, response.getRev());
        }
    }

    @SuppressWarnings("rawtypes")
    public void delete(Iterable<? extends T> entities) {

        Assert.notNull(entities, "The given list of entities must not be null.");

        List<Map> collection = new ArrayList<Map>();
        for (T entity : entities) {
            collection.add(createBulkDeleteDocument(ei.getCouchId(entity), ei.getRev(entity)));
        }

        executeBulk(collection, false);
    }

    protected Map<String, Object> createBulkDeleteDocument(String id, String revision) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("_id", id);
        map.put("_rev", revision);
        map.put("_deleted", true);
        return map;
    }

    @SuppressWarnings("unchecked")
    protected void executeBulk(List<?> list, boolean updateEntities) {
        List<Response> responses = couchDbClient.bulk(list);

        // handle errors
        List<BulkOperationError> errors = new ArrayList<BulkOperationError>();
        List<String> messages = new ArrayList<String>();
        int index = 0;
        for (Response response : responses) {
            if (response.getError() != null) {
                errors.add(new BulkOperationError(response.getId(), response.getRev(), response.getError(), response
                        .getReason()));
                messages.add(response.toString());
            } else if (updateEntities) {
                T entity = (T) list.get(index);
                if (ei.isNew(entity)) {
                    ei.setId(entity, response.getId());
                }
                ei.setRev(entity, response.getRev());
            }
            index++;
        }
        if (errors.size() > 0) {
            throw new BulkOperationException(StringUtils.join(messages, ","), errors);
        } else {
            logger.debug("All documents have been processed.");
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deleteAll() {

        View view = couchDbClient.view(designDocument() + "by_id");
        List<Map> results = view.reduce(false).includeDocs(false).query(Map.class);
        List<Map> deleteDocuments = new ArrayList<Map>();
        for (Map result : results) {
            Map deleteDocument = (Map) result.get("value");
            deleteDocument.put("_deleted", true);
            deleteDocuments.add(deleteDocument);
        }

        executeBulk(deleteDocuments, false);
    }

    @SuppressWarnings({ "rawtypes" })
    public <R> R find(ViewParams viewParams) {

        String designDocument = viewParams.getDesignDocument() != null ? viewParams.getDesignDocument() + "/"
                : designDocument();
        View view = couchDbClient.view(designDocument + viewParams.getView());
        viewBuilder.configure(view, viewParams);

        ViewResult viewResult;
        try {
            com.cloudant.client.api.model.ViewResult lightCouchViewResult = view.queryView(viewParams.getKeyType(),
                    viewParams.getValueType(), viewParams.getDocumentType());
            viewResult = toViewResult(lightCouchViewResult);
        } catch (NoDocumentException e) {
            logger.debug("no documents found for view parameters  " + viewParams, e);
            viewResult = new ViewResult(); 
        }

        return transformViewResult(viewResult, viewParams.getReturnType());
    }

    /**
     * Transforms a Cloudant-specific view result to CouchRepository's view result.
     * 
     * @param viewResult the original view result
     * @return Returns the transformed view result.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected ViewResult toViewResult(com.cloudant.client.api.model.ViewResult viewResult) {
        ViewResult result = new ViewResult();
        result.setOffset(result.getOffset());
        result.setTotalRows(viewResult.getTotalRows());
        result.setUpdateSeq(viewResult.getUpdateSeq());

        List<com.cloudant.client.api.model.ViewResult.Rows> rows = viewResult.getRows();
        List<ViewResultRow> resultRows = new ArrayList<ViewResultRow>();
        for (com.cloudant.client.api.model.ViewResult.Rows row : rows) {
            ViewResultRow resultRow = new ViewResultRow();
            resultRow.setId(row.getId());
            resultRow.setDoc(row.getDoc());
            resultRow.setKey(row.getKey());
            resultRow.setValue(row.getValue());
            resultRows.add(resultRow);
        }

        result.setRows(resultRows);

        return result;
    }

    /**
     * @return Returns the name of the design document to use. This is the ID of the design document without the
     *         preceding "_design/". EXAMPLE: "Product".
     */
    protected String designDocument() {
        return type.getSimpleName() + "/";
    }
}
