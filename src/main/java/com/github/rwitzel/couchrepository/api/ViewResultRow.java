package com.github.rwitzel.couchrepository.api;

/**
 * The row in a {@link ViewResult}.
 * 
 * @author rwitzel
 */
public class ViewResultRow {

    /**
     * The CouchDB ID of the document.
     */
    protected String id;

    protected Object key;

    protected Object value;

    protected Object doc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public <K> K getKey() {
        return (K) key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    @SuppressWarnings("unchecked")
    public <V> V getValue() {
        return (V) value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public <D> D getDoc() {
        return (D) doc;
    }

    public void setDoc(Object doc) {
        this.doc = doc;
    }

    @Override
    public String toString() {
        return "ViewResultRow [id=" + id + ", key=" + key + ", value=" + value + ", doc=" + doc + "]";
    }
    
}
