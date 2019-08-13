package com.github.rwitzel.couchrepository.api;

import static com.github.rwitzel.couchrepository.internal.AdapterUtils.toList;
import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.github.rwitzel.couchrepository.api.exceptions.BulkOperationException;
import com.github.rwitzel.couchrepository.model.BaseDocument;
import com.github.rwitzel.couchrepository.model.Comment;
import com.github.rwitzel.couchrepository.model.Manufacturer;
import com.github.rwitzel.couchrepository.model.Product;
import com.github.rwitzel.couchrepository.model.ProductRating;

/**
 * Tests implementations of {@link CrudRepository}.
 * 
 * @author rwitzel
 */
public abstract class AbstractCrudRepositoryTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private CrudRepository<Product, String> productRepo;

    @Autowired
    private CrudRepository<Manufacturer, String> manufacturerRepo;

    @Test
    public void testSaveAndFindOneAndDeletebyEntityAndExists() throws Exception {

        // given
        Product newProduct = newProduct("Oak table 1922", "Lumberjack Inc.");

        // when
        Optional<Product> existingProduct = productRepo.findById(newProduct.getId());
        if (existingProduct.isPresent()) {
            productRepo.delete(existingProduct.get());
        }

        // then
        assertFalse(productRepo.findById(newProduct.getId()).isPresent());

        // when
        productRepo.save(newProduct);

        // then
        assertTrue(productRepo.existsById(newProduct.getId()));
        Optional<Product> oFoundProduct = productRepo.findById(newProduct.getId());
        assertTrue(oFoundProduct.isPresent());
        Product foundProduct = oFoundProduct.get(); 
        assertEquals(newProduct.getComments(), foundProduct.getComments());
        assertEquals(newProduct.isHidden(), foundProduct.isHidden());
        assertEquals(newProduct.getId(), foundProduct.getId());
        assertEquals(newProduct.getIsoProductCode(), foundProduct.getIsoProductCode());
        assertEquals(newProduct.getLastModification(), foundProduct.getLastModification());
        assertEquals(newProduct.getManufacturerId(), foundProduct.getManufacturerId());
        assertEquals(newProduct.getNumBuyers(), foundProduct.getNumBuyers());
        assertEquals(newProduct.getPrice().doubleValue(), foundProduct.getPrice().doubleValue(), 0.001);
        assertEquals(newProduct.getRating(), foundProduct.getRating());
        assertEquals(newProduct.getTags(), foundProduct.getTags());
        assertEquals(newProduct.getText(), foundProduct.getText());
        assertEquals(newProduct.getWeight(), foundProduct.getWeight(), 0.0001);

        // when
        productRepo.delete(foundProduct);

        // then
        assertFalse(productRepo.existsById(newProduct.getId()));
        assertFalse(productRepo.findById(foundProduct.getId()).isPresent());
    }

    @Test
    public void testSaveIterableAndFindIterableAndDeleteIterable() throws Exception {

        productRepo.deleteAll();

        // given

        Product newProduct1 = newProduct("Oak table 1720", "Lumberjack Inc.");
        Product newProduct2 = newProduct("Oak table 1721", "Lumberjack Inc.");
        Product newProduct3 = newProduct("Oak table 1722", "Lumberjack Inc.");
        productRepo.saveAll(asList(newProduct1, newProduct2, newProduct3));

        // when
        List<String> productIds = asList(newProduct1.getId(), newProduct2.getId());
        List<Product> foundProducts = toList(productRepo.findAllById(productIds));

        // then
        assertEqualsIdSet(productIds, foundProducts);

        // when
        productRepo.deleteAll(asList(foundProducts.get(0), foundProducts.get(1)));

        // then
        assertEqualsIdSet(singleton(newProduct3.getId()), toList(productRepo.findAll()));
    }

    @Test
    public void testSaveIterable_ErrorOnBulkOperation() throws Exception {

        productRepo.deleteAll();

        // given

        Product newProduct1 = newProduct("Oak table 1720", "Lumberjack Inc.");
        Product newProduct2 = newProduct("Oak table 1721", "Lumberjack Inc.");
        Product newProduct3 = newProduct("Oak table 1722", "Lumberjack Inc.");
        productRepo.saveAll(asList(newProduct1, newProduct2, newProduct3));
        
        String oldRevision2 = newProduct2.getRevision(); 
        String oldRevision3 = newProduct3.getRevision(); 
        newProduct2.setHidden(!newProduct2.isHidden());
        newProduct3.setHidden(!newProduct3.isHidden());

        productRepo.saveAll(asList(newProduct1, newProduct2, newProduct3));

        // when
        newProduct2.setRevision(oldRevision2);
        newProduct3.setRevision(oldRevision3);
        catchException(productRepo).saveAll(asList(newProduct1, newProduct2, newProduct3));
        assertTrue(caughtException() instanceof BulkOperationException );
        BulkOperationException exception = caughtException();
        assertEquals(2, exception.getErrors().size());
        assertEquals(2, exception.getErrors().size());
    }

    @Test
    public void testFindAllAndDeleteAllAndCount() throws Exception {

        // when
        productRepo.deleteAll();
        manufacturerRepo.deleteAll();

        // then
        assertEquals(0, productRepo.count());
        assertEquals(0, manufacturerRepo.count());

        // when
        Manufacturer newManufacturer = newManufacturer("Lumberjack Inc.");
        manufacturerRepo.save(newManufacturer);

        Product newProduct1 = newProduct("Oak table 1822", newManufacturer.getId());
        Product newProduct2 = newProduct("Oak table 1823", newManufacturer.getId());
        productRepo.saveAll(asList(newProduct1, newProduct2));

        // then
        assertEquals(2, productRepo.count());
        assertEquals(1, manufacturerRepo.count());

        // when
        List<Manufacturer> foundManufacturers = toList(manufacturerRepo.findAll());

        // then
        assertEquals(1, foundManufacturers.size());
        assertEquals(newManufacturer.getId(), foundManufacturers.get(0).getId());

        // when
        List<Product> foundProducts = toList(productRepo.findAll());

        // then
        assertEqualsIdSet(asList(newProduct1.getId(), newProduct2.getId()), foundProducts);

        // when
        productRepo.deleteAll();
        manufacturerRepo.deleteAll();

        // then
        assertEquals(0, productRepo.count());
        assertEquals(0, manufacturerRepo.count());
        assertEquals(0, toList(manufacturerRepo.findAll()).size());
        assertEquals(0, toList(productRepo.findAll()).size());
    }

    @Test
    public void testDeleteById() throws Exception {
        productRepo.deleteAll();

        // given
        Product newProduct = newProduct("Oak table 1822", "Lumberjack Inc.");
        productRepo.save(newProduct);

        assertTrue(productRepo.existsById(newProduct.getId()));

        // when
        productRepo.deleteById(newProduct.getId());

        // then
        assertFalse(productRepo.existsById(newProduct.getId()));
    }

    private Manufacturer newManufacturer(String manufacturerId) {
        Manufacturer manufacturer = new Manufacturer();

        manufacturer.setAddress("Berlin, Germany");
        manufacturer.setCommercialRegisterCode(manufacturerId);
        manufacturer.setId(manufacturerId);

        return manufacturer;
    }

    protected Product newProduct(String productId, String manufacturerId) {
        Product product = new Product();

        product.setComments(asList(newComment(1), newComment(2)));
        product.setHidden(true);
        product.setId(productId);
        product.setIsoProductCode(123);
        product.setLastModification(DateTime.now().minusDays(1).toDate());
        product.setManufacturerId(manufacturerId);
        product.setNumBuyers(null); // we want to set a property with value null
        product.setPrice(new BigDecimal(1.23000000, new MathContext(10)));
        product.setRating(ProductRating.FourStars);
        product.setTags(asList("wooden", "blue"));
        product.setText("Vintage\nBargain");
        product.setWeight(34.00);

        return product;
    }

    private Comment newComment(int id) {
        Comment comment = new Comment();
        comment.setAuthor("author" + id);
        comment.setText("text" + id);
        return comment;
    }

    private void assertEqualsIdSet(Iterable<String> ids, Iterable<? extends BaseDocument> docs) {

        Set<String> idsFromDocs = new HashSet<String>();
        for (BaseDocument doc : toList(docs)) {
            idsFromDocs.add(doc.getId());
        }

        ids = new HashSet<String>(toList(ids));

        assertEquals(ids, idsFromDocs);
    }

    /**
     * Only for manual testing.
     * 
     * @throws Exception
     */
    @Test
    //@Ignore
    public void createSomeTestDocuments() throws Exception {

        productRepo.deleteAll();
        manufacturerRepo.deleteAll();

        Manufacturer newManufacturer = newManufacturer("Lumberjack Inc.");
        manufacturerRepo.save(newManufacturer);

        Product newProduct1 = newProduct("Oak table 1822", newManufacturer.getId());
        Product newProduct2 = newProduct("Oak table 1823", newManufacturer.getId());
        productRepo.saveAll(asList(newProduct1, newProduct2));
    }
    
    @Test
    public void testSave_NewDocumentVsUpdatedDocument() throws Exception {
        
        // given
        Product newProduct = newProduct(null, "Lumberjack Inc.");

        // when
        productRepo.save(newProduct);
        
        // then
        assertNotNull(newProduct.getId());
        assertNotNull(newProduct.getRevision());

        // when
        String oldRevision = newProduct.getRevision(); 
        newProduct.setHidden(!newProduct.isHidden());
        productRepo.save(newProduct);
        
        // then
        assertNotEquals(oldRevision, newProduct.getRevision());
    }


}
