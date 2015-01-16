package com.github.rwitzel.couchrepository.cloudant;

import org.springframework.test.context.ContextConfiguration;

import com.github.rwitzel.couchrepository.api.AbstractExoticTest;

/**
 * TODO ignore this test when Cloudant account is not configured or connection to Cloudant server is not possible
 * 
 * @author rwitzel
 */
@ContextConfiguration(classes = { CloudantTestConfiguration.class })
public class CloudantCrudRepositoryTest extends AbstractExoticTest {

}
