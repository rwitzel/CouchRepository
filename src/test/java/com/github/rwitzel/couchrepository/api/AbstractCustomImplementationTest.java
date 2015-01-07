package com.github.rwitzel.couchrepository.api;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.rwitzel.couchrepository.model.Product;

/**
 * Tests the integration of custom implementations of query methods.
 * 
 * @author rwitzel
 */
public abstract class AbstractCustomImplementationTest extends AbstractAutomaticImplementationTest {

    @Autowired
    private ProductRepository productRepo;
    
    @Test
    public void testDelegateToCustomImplementation() throws Exception {
        
        deleteProductRepoAndCreateSomeProducts();

        List<Product> products = productRepo.findByComment(new Object[] { "authorB", "textB" }, true);
        assertEquals(1, products.size());
        Product value = products.get(0);
        assertEquals("p1", value.getId());
    }
    

    @Test
    public void testDelegateToUnderlyingMethod_exceptionUnwrapped() throws Exception {
        
        catchException(productRepo).throwException(new Object[] { "authorB", "textB" }, true);
        assertTrue(caughtException() instanceof UnsupportedOperationException);
        assertEquals("this exception is thrown to test exception unwrapping", caughtException().getMessage());
    }

}
