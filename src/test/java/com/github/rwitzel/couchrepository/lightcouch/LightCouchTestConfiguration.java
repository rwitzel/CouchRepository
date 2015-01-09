package com.github.rwitzel.couchrepository.lightcouch;

import java.util.Map;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
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
import com.google.gson.GsonBuilder;
import com.thoughtworks.paranamer.AnnotationParanamer;

@Configuration
public class LightCouchTestConfiguration {

    private CouchDbCrudRepositoryFactory factory = new CouchDbCrudRepositoryFactory(new ViewParamsMerger(
            new AnnotationParanamer()));

    @Bean(destroyMethod = "shutdown")
    public CouchDbClient couchDbClient() {

        CouchDbProperties properties = new CouchDbProperties();
        properties.setProtocol("http");
        properties.setHost("localhost");
        properties.setPort(5984);
        properties.setDbName("lightcouch-integration-tests");

        CouchDbClient client = new CouchDbClient(properties);
        // we adjust the date format so that it is equal to the format used by Ektorp
        client.setGsonBuilder(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        return client;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Bean
    @Lazy(false)
    public String createDatabaseAndUpdateDesignDocuments(CouchDbClient dbClient) {

        dbClient.context().createDB("lightcouch-integration-tests");

        DocumentLoader loader = new DocumentLoader(new LightCouchCrudRepository(Map.class, dbClient));

        loader.loadYaml(getClass().getResourceAsStream("../Product.yaml"));
        loader.loadJson(getClass().getResourceAsStream("../Manufacturer.json"));
        loader.loadJson(getClass().getResourceAsStream("../Exotic.json"));

        return "OK"; // anything
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Bean
    public ProductRepository productRepository(CouchDbClient couchDbClient) {
        LightCouchCrudRepository underlyingRepository = new LightCouchCrudRepository(Product.class, couchDbClient);
        ProductRepositoryCustom customRepository = new ProductRepositoryCustomImpl(underlyingRepository);
        return factory.createRepository(underlyingRepository, customRepository, ProductRepository.class);
    }

    @Bean
    public LightCouchCrudRepository<Manufacturer, String> manufacturerRepo(CouchDbClient couchDbClient) {
        return new LightCouchCrudRepository<Manufacturer, String>(Manufacturer.class, couchDbClient);
    }

    @Bean
    public LightCouchCrudRepository<Exotic, ExoticId> exoticRepository(CouchDbClient couchDbClient) {
        return new LightCouchCrudRepository<Exotic, ExoticId>(Exotic.class, false, couchDbClient,
                new ExoticEntityInformation());
    }

}
