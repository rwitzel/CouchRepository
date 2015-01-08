package com.github.rwitzel.couchrepository.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

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
     * Loads the given JSON document into the database. Overrides an existing document.
     * 
     * @param documentContent
     *            the JSON content of a document
     */
    public void loadJson(InputStream documentContent) {
        save(parseJson(documentContent));
    }

    /**
     * Loads the given YAML document into the database. Overrides an existing document.
     * 
     * @param documentContent
     *            the YAML content of a document
     */
    public void loadYaml(InputStream documentContent) {
        save(parseYaml(documentContent));
    }

    @SuppressWarnings("unchecked")
    public void save(Map<String, Object> document) {

        String documentId = (String) document.get("_id");

        if (repository.exists(documentId)) {
            Map<String, Object> documentInDb = repository.findOne(documentId);
            document.put("_rev", documentInDb.get("_rev"));
        }

        repository.save(document);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> parseJson(InputStream documentContent) {
        try {
            return (Map<String, Object>) new ObjectMapper().readValue(documentContent, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> parseYaml(InputStream documentContent) {
        return (Map<String, Object>) new Yaml().load(documentContent);
    }

}
