package com.github.rwitzel.couchrepository.ektorp;

import java.util.Map;
import java.util.Properties;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.spring.HttpClientFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.github.rwitzel.couchrepository.api.CouchDbCrudRepositoryFactory;
import com.github.rwitzel.couchrepository.api.ProductRepository;
import com.github.rwitzel.couchrepository.api.ProductRepositoryCustom;
import com.github.rwitzel.couchrepository.api.ProductRepositoryCustomImpl;
import com.github.rwitzel.couchrepository.internal.ViewParamsMerger;
import com.github.rwitzel.couchrepository.model.Exotic;
import com.github.rwitzel.couchrepository.model.ExoticEntityInformation;
import com.github.rwitzel.couchrepository.model.ExoticId;
import com.github.rwitzel.couchrepository.model.Manufacturer;
import com.github.rwitzel.couchrepository.model.Product;
import com.github.rwitzel.couchrepository.support.DocumentLoader;
import com.thoughtworks.paranamer.AnnotationParanamer;

/**
 * This configuration uses the the database "/ektorp-integration-tests/", creates or updates the standard design
 * document including the views of the design document.
 * 
 * @author rwitzel
 */
@Configuration
public class EktorpTestConfiguration {

    private CouchDbCrudRepositoryFactory factory = new CouchDbCrudRepositoryFactory(new ViewParamsMerger(
            new AnnotationParanamer()));

    @Bean
    public StdCouchDbConnector connector() throws Exception {

        String url = "http://localhost:5984/";
        String databaseName = "ektorp-integration-tests";

        Properties properties = new Properties();
        properties.setProperty("autoUpdateViewOnChange", "true");

        HttpClientFactoryBean factory = new HttpClientFactoryBean();
        factory.setUrl(url);
        factory.setProperties(properties);
        factory.afterPropertiesSet();
        HttpClient client = factory.getObject();

        CouchDbInstance dbInstance = new StdCouchDbInstance(client);
        return new StdCouchDbConnector(databaseName, dbInstance);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Bean
    @Lazy(false)
    public String createDatabaseAndUpdateDesignDocuments(CouchDbConnector db) {

        db.createDatabaseIfNotExists();

        DocumentLoader loader = new DocumentLoader(new EktorpCrudRepository(Map.class, db));

        loader.loadYaml(getClass().getResourceAsStream("../Product.yaml"));
        loader.loadJson(getClass().getResourceAsStream("../Manufacturer.json"));
        loader.loadJson(getClass().getResourceAsStream("../Exotic.json"));

        return "OK"; // anything
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Bean
    public ProductRepository productRepository(CouchDbConnector db) {
        EktorpCrudRepository underlyingRepository = new EktorpCrudRepository(Product.class, db);
        ProductRepositoryCustom customRepository = new ProductRepositoryCustomImpl(underlyingRepository);
        return factory.createRepository(underlyingRepository, customRepository, ProductRepository.class);
    }

    @Bean
    public EktorpCrudRepository<Manufacturer, String> manufacturerRepository(CouchDbConnector db) {
        return new EktorpCrudRepository<Manufacturer, String>(Manufacturer.class, db);
    }

    @Bean
    public EktorpCrudRepository<Exotic, ExoticId> exoticRepository(CouchDbConnector db) {
        return new EktorpCrudRepository<Exotic, ExoticId>(Exotic.class, db, new ExoticEntityInformation());
    }
}
