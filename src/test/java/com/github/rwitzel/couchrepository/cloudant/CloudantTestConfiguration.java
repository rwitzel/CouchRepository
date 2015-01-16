package com.github.rwitzel.couchrepository.cloudant;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.ReflectionUtils;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.org.lightcouch.CouchDbClient;
import com.cloudant.client.org.lightcouch.CouchDbClientBase;
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
public class CloudantTestConfiguration {

    private CouchDbCrudRepositoryFactory factory = new CouchDbCrudRepositoryFactory(new ViewParamsMerger(
            new AnnotationParanamer()));

    @Bean(destroyMethod = "shutdown")
    public CloudantClient client() throws IOException {

        Properties props = new Properties();
        props.load(getClass().getClassLoader().getResourceAsStream("cloudant.properties"));
        String account = props.getProperty("cloudant.account");
        String loginUsername = props.getProperty("cloudant.username");
        String password = props.getProperty("cloudant.password");

        CouchDbClient lightCouchClient = new CouchDbClient("https", account + ".cloudant.com", 443, loginUsername,
                password);
        
        // set Gson builder
        GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Method method = ReflectionUtils.findMethod(CouchDbClientBase.class, "setGsonBuilder", GsonBuilder.class);
        ReflectionUtils.makeAccessible(method);
        ReflectionUtils.invokeMethod(method, lightCouchClient, gsonBuilder);

        return new CloudantClient(lightCouchClient);
    }

    @Bean
    public Database db(CloudantClient client) {
        return client.database(dbName(), true);
    }
    
    private String dbName() {
        return "cloudant-integration-tests";
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Bean
    @Lazy(false)
    public String createDatabaseAndUpdateDesignDocuments(CloudantClient client, Database db) {

        client.createDB(dbName());

        DocumentLoader loader = new DocumentLoader(new CloudantCrudRepository(Map.class, db));

        loader.loadYaml(getClass().getResourceAsStream("../Product.yaml"));
        loader.loadJson(getClass().getResourceAsStream("../Manufacturer.json"));
        loader.loadJson(getClass().getResourceAsStream("../Exotic.json"));

        return "OK"; // anything
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Bean
    public ProductRepository productRepository(Database db) {
        CloudantCrudRepository underlyingRepository = new CloudantCrudRepository(Product.class, db);
        ProductRepositoryCustom customRepository = new ProductRepositoryCustomImpl(underlyingRepository);
        return factory.createRepository(underlyingRepository, customRepository, ProductRepository.class);
    }

    @Bean
    public CloudantCrudRepository<Manufacturer, String> manufacturerRepo(Database db) {
        return new CloudantCrudRepository<Manufacturer, String>(Manufacturer.class, db);
    }

    @Bean
    public CloudantCrudRepository<Exotic, ExoticId> exoticRepository(Database db) {
        return new CloudantCrudRepository<Exotic, ExoticId>(Exotic.class, false, db, new ExoticEntityInformation());
    }

}
