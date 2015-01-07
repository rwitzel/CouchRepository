package com.github.rwitzel.couchrepository.internal;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.github.rwitzel.couchrepository.api.ProductRepository;
import com.github.rwitzel.couchrepository.api.ViewParams;

/**
 * Tests {@link ViewParamsMerger}.
 * 
 * @author rwitzel
 */
public class ViewParamsMergerTest {

    private ViewParamsMerger merger = new ViewParamsMerger();

    private ViewParams passedParams = new ViewParams();
    
    private ViewParams resultingParams = new ViewParams();
    
    @Before
    public void before() {
        // prevent validation errors -> set any non-null value
        resultingParams.setValueType(RuntimeException.class); 
        resultingParams.setKeyType(RuntimeException.class); 
    }

    @Test
    public void testCreateViewParams() throws Exception {

        Method method = ReflectionUtils.findMethod(ProductRepository.class, "findByComment", Object[].class,
                Boolean.class, ViewParams.class, Class.class);
        assertNotNull(method);

        passedParams.setKeyType(String.class);
        passedParams.setLimit(100);
        merger.mergeViewParams(resultingParams, method, new Object[] {
                new Object[] { "hemingway", "once upon a time" }, true, passedParams, Map.class });
        assertEquals("findByComment", resultingParams.getView());
        assertEquals(true, resultingParams.getDescending());
        assertEquals(100, (int) resultingParams.getLimit());
        assertArrayEquals(new Object[] { "hemingway", "once upon a time" }, (Object[]) resultingParams.getKey());
        assertEquals("findByComment", resultingParams.getView());
        assertEquals(null, resultingParams.getDesignDocument());
    }

    @Test
    public void testCreateViewParams_precedence_resultingParametersOverPassedParameters() throws Exception {
        
        // given
        resultingParams.setKeyType(Integer.class);
        passedParams.setKeyType(String.class);
        
        // when
        merger.mergeViewParams(resultingParams, "findByComment", new String[] { "viewParams"}, new Object[] { passedParams} );
        
        // then
        assertEquals(Integer.class, resultingParams.getKeyType()); // the passed parameter wins
    }

    @Test
    public void testCreateViewParams_precedence_methodParametersOverPassedParameters() throws Exception {

        // given
        passedParams.setKeyType(String.class);
        
        // when
        merger.mergeViewParams(resultingParams, "findByComment", new String[] { "keyType", "viewParams"}, new Object[] { Integer.class, passedParams} );
        
        // then
        assertEquals(Integer.class, resultingParams.getKeyType()); // the method parameter wins
    }

    @Test
    public void testCreateViewParams_precedence_passedParameterOverMethodName() throws Exception {

        // given
        passedParams.setView("findByComment2");
        
        // when
        merger.mergeViewParams(resultingParams, "findByComment", new String[] { "viewParams"}, new Object[] { passedParams} );
        
        // then
        assertEquals("findByComment2", resultingParams.getView()); // the passed parameter wins
    }
}
