package com.github.rwitzel.couchrepository.api;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.rwitzel.couchrepository.internal.QueryMethodHandler;
import com.github.rwitzel.couchrepository.model.Comment;

/**
 * Tests interfaces that must be automatically implemented by {@link QueryMethodHandler}.
 * 
 * @author rwitzel
 */
public abstract class AbstractAutomaticImplementationTest extends AbstractCouchdbCrudRepositoryTest {

    @Autowired
    private ProductRepository productRepo;

    @Test
    public void testDelegateToFindByViewParams() throws Exception {
        
        deleteProductRepoAndCreateSomeProducts();

        ViewParams viewParams = new ViewParams();
        viewParams.setKeyType(String[].class);
        viewParams.setReduce(false);
        ViewResult comments = productRepo.findByComment(new Object[] { "authorB", "textB" }, null, viewParams,
                HashMap.class);
        assertEquals(1, comments.getRows().size());
        Map<String, Object> value = comments.getRows().get(0).getValue();
        assertEquals("textB", value.get("text"));
    }

    @Test
    public void testDelegateToFindByViewParams_ReturnValueType() throws Exception {
        
        deleteProductRepoAndCreateSomeProducts();

        ViewParams viewParams = new ViewParams();
        viewParams.setKeyType(String[].class);
        viewParams.setValueType(Comment.class);
        viewParams.setReturnType("value");
        viewParams.setReduce(false);
        List<Comment> comments = productRepo.findByComment(new Object[] { "authorB", "textB" }, viewParams);
        assertEquals(1, comments.size());
        Comment value = comments.get(0);
        assertEquals("authorB", value.getAuthor());
        assertEquals("textB", value.getText());
    }

    @Test
    public void testDelegateToFindByViewParams_emptyViewResult() throws Exception {
        
        productRepo.deleteAll();

        ViewParams viewParams = new ViewParams();
        viewParams.setKeyType(String[].class);
        viewParams.setValueType(Comment.class);
        viewParams.setReturnType("value");
        viewParams.setReduce(false);
        List<Comment> comments = productRepo.findByComment(new Object[] { "authorB", "textB" }, viewParams);
        assertEquals(0, comments.size());
    }

    @Test
    public void testDelegateToUnderlyingMethod() throws Exception {
        
        deleteProductRepoAndCreateSomeProducts();

        assertEquals("p1", productRepo.findById("p1").get().getId());
    }
}
