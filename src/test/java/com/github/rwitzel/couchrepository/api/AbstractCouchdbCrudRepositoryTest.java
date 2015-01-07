package com.github.rwitzel.couchrepository.api;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rwitzel.couchrepository.api.viewresult.ProductSummary;
import com.github.rwitzel.couchrepository.model.Product;

/**
 * Tests implementations of {@link CouchDbCrudRepository}.
 * 
 * @author rwitzel
 */
public abstract class AbstractCouchdbCrudRepositoryTest extends AbstractCrudRepositoryTest {

    @Autowired
    private CouchDbCrudRepository<Product, String> productRepo;

    private Product p1 = readDocument("../documents/p1_allAttributesSet.js", Product.class);

    private Product p2 = newProduct("Oak table 1922", "Lumberjack1 Inc.");

    private Product p3 = newProduct("Oak table 1923", "Lumberjack1 Inc.");

    private Product p4 = newProduct("Oak table 1924", "Lumberjack Inc.");

    private ViewParams params = new ViewParams();

    protected void deleteProductRepoAndCreateSomeProducts() {

        productRepo.deleteAll();

        productRepo.save(p1);
        productRepo.save(p2);
        productRepo.save(p3);
        productRepo.save(p4);

        params.setView("by_manufacturerId");
        params.setReduce(false);
        params.setKeyType(String.class);
        params.setValueType(ProductSummary.class);
    }

    private <T> T readDocument(String pathToResource, Class<T> type) {
        InputStream documentContent = getClass().getResourceAsStream(pathToResource);
        try {
            return new ObjectMapper().readValue(documentContent, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCompleteResult() throws Exception {
        
        deleteProductRepoAndCreateSomeProducts();

        params.setKey("Lumberjack Association");

        ViewResult viewResults = productRepo.find(params);
        assertEquals(1, viewResults.getRows().size());
        ProductSummary summary = viewResults.getRows().get(0).getValue();
        assertEquals(p1.getId(), summary.getFacts().getDocId());
        assertEquals(p1.getComments(), summary.getFacts().getComments());
        assertEquals(p1.getIsoProductCode(), summary.getFacts().getIsoProductCode());
        assertEquals(p1.getLastModification(), summary.getFacts().getLastModification());
        assertEquals(p1.getNumBuyers(), summary.getFacts().getNumBuyers());
        assertEquals(p1.getPrice().doubleValue(), summary.getFacts().getPrice().doubleValue(), 0.0001);
        assertEquals(p1.getRating(), summary.getFacts().getRating());
        assertEquals(p1.getRevision(), summary.getFacts().getRevision());
        assertEquals(p1.getTags(), summary.getFacts().getTags());
        assertEquals(p1.getText(), summary.getFacts().getText());
        assertEquals(p1.getWeight(), summary.getFacts().getWeight(), 0.00001);
    }

    @Test
    public void testSetKey() throws Exception {
        
        deleteProductRepoAndCreateSomeProducts();

        params.setKey("Lumberjack1 Inc.");

        ViewResult summaries = productRepo.find(params);
        assertEquals(2, summaries.getRows().size());

        List<String> docIds = toDocIds(summaries.getRows());
        List<String> expectedDocsIds = Arrays.asList(p2.getId(), p3.getId());
        assertEquals(new HashSet<String>(expectedDocsIds), new HashSet<String>(docIds));
    }

    private List<String> toDocIds(List<ViewResultRow> results) {
        List<String> docIds = new ArrayList<String>();
        for (ViewResultRow result : results) {
            docIds.add(result.getId());
        }
        return docIds;
    }

    @Test
    public void testSetReduce() throws Exception {
        
        deleteProductRepoAndCreateSomeProducts();

        params.setView("by_manufacturerId");
        params.setReduce(true);
        params.setKeyType(String.class); // any
        params.setValueType(Integer.class);

        ViewResult summaries = productRepo.find(params);
        assertEquals(4, (int) summaries.getRows().get(0).getValue());

    }

    @Test
    public void testSetReturnValueToKey() throws Exception {
        
        deleteProductRepoAndCreateSomeProducts();

        params.setKey("Lumberjack1 Inc.");
        params.setReturnType("key");

        List<String> keys = productRepo.find(params);
        assertEquals(2, keys.size());
        assertEquals("Lumberjack1 Inc.", keys.get(0));
        assertEquals("Lumberjack1 Inc.", keys.get(1));
    }

    @Test
    public void testSetReturnValueToId() throws Exception {
        
        deleteProductRepoAndCreateSomeProducts();

        params.setKey("Lumberjack1 Inc.");
        params.setReturnType("id");

        List<String> docIds = productRepo.find(params);
        assertEquals(2, docIds.size());

        List<String> expectedDocsIds = Arrays.asList(p2.getId(), p3.getId());
        assertEquals(new HashSet<String>(expectedDocsIds), new HashSet<String>(docIds));
    }

}
