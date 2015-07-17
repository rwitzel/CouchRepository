Some parts of this documentation require knowledge about CouchDB IDs, revisions, and views.

[![Travis build status](https://travis-ci.org/rwitzel/CouchRepository.svg)](https://travis-ci.org/rwitzel/CouchRepository)
[![Coveralls coverage status](https://img.shields.io/coveralls/rwitzel/CouchRepository.svg)](https://coveralls.io/r/rwitzel/CouchRepository)
[![Apache 2](http://img.shields.io/badge/license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.rwitzel/couchrepository-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.rwitzel/couchrepository-core/)


When should I use CouchRepository?
==================================

There are two main use cases.

A. You choose CouchRepository when 

* you are going to use [Spring Data](http://projects.spring.io/spring-data/) for a new project and 
* you do not want to decide about the eventual database solution yet.

In this case, you make your new application configurable so that one can easily switch between different persistence solutions (SQL, MongoDB, CouchDB).

B. You choose CouchRepository when 

* you want to stick with the familiar Spring Data framework, and 
* you are going to work with an existing [CouchDB](http://couchdb.apache.org/) database, and 
* you need only basic CRUD operations and simple view queries in the first place.

As soon as you want to use more sophisticated operations on the CouchDB, you will directly use the underlying Java CouchDB drivers, namely LightCouch or Ektorp.


How do I use CouchRepository?
=============================

You may want to checkout the source code and have a look at the tests to find working examples for both the Ektorp and LightCouch drivers.

Prerequisites
-------------

CouchRepository requires Java 7 or higher.

Basics
------

Here is a short example that assumes you use Ektorp as CouchDB driver. The code for LightCouch is almost the same. 

First of all you add the needed dependencies to your project.
The dependencies for Ektorp must be added explicitly because you can choose between Ektorp and LightCouch.

    compile 'com.github.rwitzel:couchrepository-core:0.9.1'
    compile 'org.ektorp:org.ektorp:1.4.2'
    compile 'org.ektorp:org.ektorp.spring:1.4.2'

Have a look a the [CouchRepository Example](https://github.com/rwitzel/CouchRepository-example) to get a full list of the needed dependencies.    

Then you set up a `CouchDbConnector` as described by the Ektorp documentation. 

    CouchDbConnector db = ...

Then you create a repository for each type of entities. For an entity type like `Product` you write

    CouchDbCrudRepository<Product,String> productRepository = new EktorpCrudRepository(Product.class, db);

The type `CouchDbCrudRepository` extends Spring Data's `CrudRepository` so that you are able to query CouchDB views immediately by using the `find` method.

One thing is still missing. The repository requires a design document (by default named `_design/Product`) that provides a view named `by_id`.
This view must provide ID and revision for each entity of the given type. The reduce function must be `_count`. The design document may look like this.

    {
      "_id" : "_design/Product",
      "language" : "javascript",
      "views" : {
        "by_id" : {
          "map" : "function(doc) { if(doc.type == 'Product') {emit(doc._id, { _id : doc._id, _rev: doc._rev } )} }",
          "reduce" : "_count"
        }
    } 

One way to load the design document into the database is `DocumentLoader` but you can use any other mean, of course.
You can use the repository as soon the design document is available in the database.

    DocumentLoader loader = new DocumentLoader(new EktorpCrudRepository(Map.class, db));
    loader.loadJson(getClass().getResourceAsStream("../Product.json")); // design document Product.json is taken from the classpath

In case you prefer YAML documents, you can use the method `loadYaml` instead of `loadJson`.
In YAML documents you don't have to care about the formatting of the contained Javascript functions.  
    
Finally, your Java-based Spring configuration might look like this.

    @Configuration
    public class CouchDbRepositoriesConfiguration {
    
        @Bean
        public StdCouchDbConnector connector() throws Exception { ... }
    
        @Bean
        public CouchDbCrudRepository<Product,String> productRepository(CouchDbConnector db) { ... }

        @Bean @Lazy(false)
        public String initializeDatabaseAndDesignDocuments(CouchDbConnector db) { ... }
    }
  
Automatic implementation of finder methods
------------------------------------------  

If you want to use the automatic implementation of finder methods, you create an interface that contains the desired methods.

    public interface ProductRepository extends CouchDbCrudRepository<Product, String> {
    
        List<Comment> findByComment(Object[] key, ViewParams viewParams);

        ViewResult findByComment(Object[] key, Boolean descending, ViewParams viewParams, Class<?> valueType);
    }

The method parameter `viewParams` allow you to specify arbitrary parameters for the view query.
But usually you will add more parameters to the method signature because they cover your main use cases, here: `key`.
Parameters set in `viewParams` do not override parameters that are contained in the method signature, i.e the key in `viewParams` is ignored. *Attention!* Please read more about method parameter name detection in section Q&A.   

To actually use the methods, your design document must contain a view `findByComment` that is appropriate for your methods.  

    {
      "_id" : "_design/Product",
      "language" : "javascript",
      "views" : {
        ...,
        "findByComment" : {
          "map" : "... your custom implementation ...",
          "reduce" : "... your custom implementation ..."
        }
      }
    }

Finally, you create a proxy that replaces the existing repository. The proxy wraps the original repository and adds the custom finder methods.
 
    CouchDbCrudRepositoryFactory factory = new CouchDbCrudRepositoryFactory();
    productRepository = factory.createRepository(productRepository, null, ProductRepository.class);


Custom implementations
----------------------

In some cases you want to provide custom implementations of finder methods. For our example we create an interface called `ProductRepositoryCustom`.

    public interface ProductRepositoryCustom {
        List<Product> findByComment(Object[] key, Boolean descending);
    }

Then we create an implementation of this interface.

    public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    
        private CouchDbCrudRepository<Product, String> repository;
    
        public ProductRepositoryCustomImpl(CouchDbCrudRepository<Product, String> repository) {
            super(); 
            this.repository = repository;
        }
    
        public List<Product> findByComment(Object[] key, Boolean descending) {
    
            ViewParams params = new ViewParams();
            params.setReduce(false);
            params.setIncludeDocs(true);
            params.setReturnType("doc");
            params.setDocumentType(Product.class);
            params.setValueType(Object.class);
            params.setKeyType(Object.class);
            params.setView("findByComment");
    
            params.setKey(key);
            params.setDescending(descending);
    
            return repository.find(params);
        }
    }

Now the creation of the proxy is a bit different.

    ProductRepositoryCustom customRepository = new ProductRepositoryCustomImpl(productRepository);
    
    productRepository = factory.createRepository(productRepository, customRepository, ProductRepository.class);


More Questions and Answers
=====================

**Q. Which CouchDB drivers are supported?** 

A. Based on the activity derived from the commit history, this project tried to use the following Java drivers and Scala drivers: 

* [Ektorp](https://github.com/helun/Ektorp). A lot of commits in 2014. 
* [LightCouch](https://github.com/lightcouch/LightCouch). A lot of commits in 2014. 
* [Sohva](https://github.com/gnieh/sohva). A lot of commits in 2014. 

At the moment Sohva does not provide an API that can be easily used by Java applications. Therefore, Sohva is not used in this project but Ektorp and LightCouch adapters are implemented. 

**Q. The exception com.thoughtworks.paranamer.ParameterNamesNotFoundException is thrown. What can I do?**

A. [Paranamer](https://github.com/paul-hammant/paranamer) is used to identify the names of method parameters. Go to the project page to get more information about the exception.

For Java 7 you could try the `AnnotationParanamer`. In this case you have to add JSR-330 to your classpath (javax.inject:javax.inject:1).

For Java 8 try [Java8Paranamer](https://github.com/rwitzel/Java8Paranamer). Java8Paranamer is a single class you can copy to your source code. Be aware that even for JDK 8 you have to activate a compiler option: `-parameters`.

Then configure a custom CouchDbCrudRepositoryFactory with your chosen paranamer.

    factory = new CouchDbCrudRepositoryFactory(new ViewParamsMerger(.. your paranamer here...));

**Q. Do I have to modify my entity classes to make them compatible with CouchDB?**

Both Ektorp and LightCouch use JSON serialization frameworks like Jackson and Gson in order to save and load entities.
Therefore, most probably you have to add annotations to your entity classes.

Additionally, in many cases you will have to add a property for the CouchDB revision. If the IDs of your entities are not of type String, then you have to add a property for the CouchDB ID.

If your entity classes store ID and revision not in properties with standard names like `_id`, `id` resp. `_rev`, `rev`, `revision`, then use `EntityInformation` to grant CouchRepository access to the ID and the revision.        

**Q. Can I use IDs that are of other type than String, like Integer, a custom class etc.?**

A. Yes, but natively CouchDB (up to 1.6.1) supports only IDs of type String. Therefore, you have to define a mapping between the CouchDB ID and your domain-specific ID. You will find an example among the unit tests: Look for the class `Exotic` in the source code to find a working example. 

**Q. Are you going  to support Spring Data's API for paging and sorting?**

A. No, there is no plan to support `PagingAndSortingRepository`.

**Q. Are you going to support XML-based or annotation-based definitions of Spring Data repositories?**

A. No, there is no plan to do this because the benefit of the declarative approach does not look very big at the moment. A simple Java-based Spring configuration is sufficient.

**Q. How is the performance of CouchRepository?**

A. The performance is determined by the performance of the underlying CouchDB drivers and the design of CouchDB at all. Thus, a method like `deleteAll` has to fetch all document IDs from the database to delete the documents.

**Q. How does the future of CouchRepository look like?**

A. CouchRepository was build for a single purpose, and it already serves this purpose. Thus, at the moment there are no plans to add more features. But feel free to suggest improvements or to send in patches. 
    