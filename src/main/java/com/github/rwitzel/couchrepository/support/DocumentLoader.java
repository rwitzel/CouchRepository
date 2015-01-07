package com.github.rwitzel.couchrepository.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rwitzel.couchrepository.api.CouchDbCrudRepository;

/**
 * Loads a document into the database. Useful for the initialization of a database.
 * 
 * @author rwitzel
 */
public class DocumentLoader {

    @SuppressWarnings("rawtypes")
    private CouchDbCrudRepository<Map, String> repository;

    /**
     * @param repository
     *            A repository for {@link Map maps}.
     */
    @SuppressWarnings("rawtypes")
    public DocumentLoader(CouchDbCrudRepository<Map, String> repository) {
        super();
        this.repository = repository;
    }

    /**
     * Loads the given document into the database. Overrides an existing document.
     * 
     * @param documentContent
     *            the JSON content of a document
     */
    @SuppressWarnings("unchecked")
    public void load(InputStream documentContent) {

        Map<String, Object> document;
        try {
            document = new ObjectMapper().readValue(documentContent, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String documentId = (String) document.get("_id");

        if (repository.exists(documentId)) {
            Map<String, Object> documentInDb = repository.findOne(documentId);
            document.put("_rev", documentInDb.get("_rev"));
        }

        repository.save(document);
    }

}
