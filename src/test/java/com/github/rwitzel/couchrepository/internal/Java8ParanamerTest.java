package com.github.rwitzel.couchrepository.internal;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import com.github.rwitzel.couchrepository.api.ProductRepository;
import com.github.rwitzel.couchrepository.api.ViewParams;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;

/**
 * Tests {@link Java8Paranamer}.
 * 
 * @author rwitzel
 */
public class Java8ParanamerTest {

    private Java8Paranamer paranamer = new Java8Paranamer();

    @Test
    public void testLookupParameterNames() throws Exception {
        
        Method method = ReflectionUtils.findMethod(ProductRepository.class, "findByComment", Object[].class,
                Boolean.class, ViewParams.class, Class.class);
        assertNotNull(method);

        String[] names = paranamer.lookupParameterNames(method);
        assertArrayEquals(new String[] { "key", "descending", "viewParams", "valueType" }, names);
    }

    @Test
    public void testLookupParameterNames_emptyList() throws Exception {

        Method method = ReflectionUtils.findMethod(ProductRepository.class, "findByComment");
        assertNotNull(method);

        String[] names = paranamer.lookupParameterNames(method);
        assertArrayEquals(new String[] {}, names);
    }

    @Test
    public void testLookupParameterNames_throwExceptionIfMissing() throws Exception {

        Constructor<?> constructor = this.getClass().getConstructors()[0];

        assertNull(paranamer.lookupParameterNames(constructor, false));

        try {
            paranamer.lookupParameterNames(constructor, true);
            fail("expected " + ParameterNamesNotFoundException.class);
        } catch (ParameterNamesNotFoundException e) {
            // OK
        }
    }

}
