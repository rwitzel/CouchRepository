package com.github.rwitzel.couchrepository.support;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

/**
 * Tests {@link DocumentLoader}.
 * 
 * @author rwitzel
 */
public class DocumentLoaderTest {

    private DocumentLoader loader = new DocumentLoader(null);

    @Test
    public void testParseJson() throws Exception {
        Map<String, Object> doc = loader.parseJson(getClass().getResourceAsStream("../Product.json"));

        assertEquals("_design/Product", get(doc, "_id"));
        assertEquals("_count", get(doc, "views", "by_id", "reduce"));
        assertEquals("function(doc) { if(doc.isoProductCode && doc._id) "
                + "{emit(doc._id, { _id : doc._id, _rev: doc._rev } )} }", get(doc, "views", "by_id", "map"));
    }

    @Test
    public void testParseJsonWithYaml() throws Exception {
        Map<String, Object> doc = loader.parseYaml(getClass().getResourceAsStream("../Product.json"));

        assertEquals("_design/Product", get(doc, "_id"));
        assertEquals("_count", get(doc, "views", "by_id", "reduce"));
        assertEquals("function(doc) { if(doc.isoProductCode && doc._id) "
                + "{emit(doc._id, { _id : doc._id, _rev: doc._rev } )} }", get(doc, "views", "by_id", "map"));
    }

    @Test
    public void testParseYaml() throws Exception {
        Map<String, Object> doc = loader.parseYaml(getClass().getResourceAsStream("../Product.yaml"));

        assertEquals("_design/Product", get(doc, "_id"));
        assertEquals("_count", get(doc, "views", "by_id", "reduce"));
        String mapFunction = get(doc, "views", "by_id", "map").trim();
        assertThat(mapFunction, startsWith("function(doc) {"));
        assertThat(mapFunction, endsWith("}"));
        assertThat(mapFunction, containsString("\n"));
    }

    @SuppressWarnings("unchecked")
    private String get(Map<String, Object> map, String... path) {
        for (String element : path) {
            Object value = map.get(element);
            if (value instanceof Map) {
                map = (Map<String, Object>) value;
            } else {
                return (String) value;
            }
        }
        throw new RuntimeException("programming error");
    }
}
