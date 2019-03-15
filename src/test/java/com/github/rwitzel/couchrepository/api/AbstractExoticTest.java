package com.github.rwitzel.couchrepository.api;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.rwitzel.couchrepository.model.Exotic;
import com.github.rwitzel.couchrepository.model.ExoticId;

/**
 * Tests entities with custom ID and revision attributes.
 * 
 * @author rwitzel
 */
public abstract class AbstractExoticTest extends AbstractCustomImplementationTest {

    @Autowired
    CouchDbCrudRepository<Exotic, ExoticId> exoticRepository;

    @Before
    public void before() {
        exoticRepository.deleteAll();
    }

    @Test
    public void testExoticSaveAndFindOneAndFindSomeDocumentWithId() {

        // given
        ExoticId id1 = new ExoticId(123, "abc", true);
        Exotic doc1 = new Exotic(id1);
        doc1.setData("test1");

        ExoticId id2 = new ExoticId(123, "xyz", true);
        Exotic doc2 = new Exotic(id2);
        doc2.setData("test2");

        // when
        exoticRepository.save(doc1);
        exoticRepository.save(doc2);

        // then
        Optional<Exotic> oFoundDoc = exoticRepository.findById(id1);
        assertTrue(oFoundDoc.isPresent());
        assertEquals(doc1.getData(), oFoundDoc.get().getData());

        // when
        oFoundDoc = exoticRepository.findById(new ExoticId(123, "def", true));

        // then
        assertFalse(oFoundDoc.isPresent());

        // when
        Iterable<Exotic> foundDocs = exoticRepository.findAllById(singleton(id1));

        // then
        Exotic foundDoc = foundDocs.iterator().next();
        assertEquals(doc1.getData(), foundDoc.getData());
    }

    @Test
    public void testExoticSaveAndSaveAgainAndDelete() {

        // given
        ExoticId id1 = new ExoticId(123, "abc", true);
        Exotic doc1 = new Exotic(id1);
        doc1.setData("test1");
        exoticRepository.save(doc1);

        // when
        doc1.setData("test1_changed");
        exoticRepository.save(doc1);

        // then
        Optional<Exotic> foundDoc = exoticRepository.findById(id1);
        assertEquals(doc1.getData(), foundDoc.get().getData());

        // when
        exoticRepository.delete(doc1);

        // then
        assertFalse(exoticRepository.findById(id1).isPresent());
    }

}
