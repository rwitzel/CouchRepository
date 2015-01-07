package com.github.rwitzel.couchrepository.api;

import com.github.rwitzel.couchrepository.api.exceptions.UnsupportedViewParameterException;

/**
 * This POJO represents
 * <ul>
 * <li>the parameters for a view query, see CouchDB 1.7.0 API reference <a
 * href="http://docs.couchdb.org/en/latest/api/ddoc/views.html#api-ddoc-view">10.5.4.
 * /db/_design/design-doc/_view/view-name</a>,
 * <li>the name of the view,
 * <li>the name of the design document that contains the view,
 * <li>the return types of the view (key, value, document).
 * </ul>
 * <p>
 * The underlying driver must not support all of the listed parameters. In this case a
 * {@link UnsupportedViewParameterException} might be thrown.
 * <p>
 * Additionally, the name of the view and its design document can be set.
 * 
 * @author rwitzel
 */
public class ViewParams {

    private Boolean conflicts;

    private Boolean descending;

    private Object endKey;

    private String endKeyDocId;

    private Boolean group;

    private Integer groupLevel;

    private Boolean includeDocs;

    private Boolean attachments;

    private Boolean attEncodingInfo;

    private Boolean inclusiveEnd;

    private Object key;

    private Integer limit;

    private Boolean reduce;

    private Integer skip;

    private String stale;

    private Object startKey;

    private String startKeyDocId;

    private Boolean updateSeq;

    /**
     * The name of the design document, excluding the prefix "_design".
     * <p>
     * Only set if the design document deviates from the default.
     */
    private String designDocument;

    /**
     * The name of the view.
     * <p>
     * Only set if the view deviates from the default.
     */
    private String view;

    /**
     * The type of keys in a view.
     */
    private Class<?> keyType;

    /**
     * The type of values in a view.
     */
    private Class<?> valueType;

    /**
     * The type of documents in a view (if documents are included).
     */
    private Class<?> documentType;

    /**
     * If null, then the return type of the query will be {@link ViewResult}. If "value" or "key" or "doc" or "id"
     * then a list of values or keys or documents or document IDs will be returned.
     */
    private String returnType;

    public Boolean getConflicts() {
        return conflicts;
    }

    public void setConflicts(Boolean conflicts) {
        this.conflicts = conflicts;
    }

    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    public Object getEndKey() {
        return endKey;
    }

    public void setEndKey(Object endKey) {
        this.endKey = endKey;
    }

    public String getEndKeyDocId() {
        return endKeyDocId;
    }

    public void setEndKeyDocId(String endKeyDocId) {
        this.endKeyDocId = endKeyDocId;
    }

    public Boolean getGroup() {
        return group;
    }

    public void setGroup(Boolean group) {
        this.group = group;
    }

    public Integer getGroupLevel() {
        return groupLevel;
    }

    public void setGroupLevel(Integer groupLevel) {
        this.groupLevel = groupLevel;
    }

    public Boolean getIncludeDocs() {
        return includeDocs;
    }

    public void setIncludeDocs(Boolean includeDocs) {
        this.includeDocs = includeDocs;
    }

    public Boolean getAttachments() {
        return attachments;
    }

    public void setAttachments(Boolean attachments) {
        this.attachments = attachments;
    }

    public Boolean getAttEncodingInfo() {
        return attEncodingInfo;
    }

    public void setAttEncodingInfo(Boolean attEncodingInfo) {
        this.attEncodingInfo = attEncodingInfo;
    }

    public Boolean getInclusiveEnd() {
        return inclusiveEnd;
    }

    public void setInclusiveEnd(Boolean inclusiveEnd) {
        this.inclusiveEnd = inclusiveEnd;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Boolean getReduce() {
        return reduce;
    }

    public void setReduce(Boolean reduce) {
        this.reduce = reduce;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public String getStale() {
        return stale;
    }

    public void setStale(String stale) {
        this.stale = stale;
    }

    public Object getStartKey() {
        return startKey;
    }

    public void setStartKey(Object startKey) {
        this.startKey = startKey;
    }

    public String getStartKeyDocId() {
        return startKeyDocId;
    }

    public void setStartKeyDocId(String startKeyDocId) {
        this.startKeyDocId = startKeyDocId;
    }

    public Boolean getUpdateSeq() {
        return updateSeq;
    }

    public void setUpdateSeq(Boolean updateSeq) {
        this.updateSeq = updateSeq;
    }

    public String getDesignDocument() {
        return designDocument;
    }

    public void setDesignDocument(String designDocument) {
        this.designDocument = designDocument;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Class<?> getKeyType() {
        return keyType;
    }

    public void setKeyType(Class<?> keyType) {
        this.keyType = keyType;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public void setValueType(Class<?> valueType) {
        this.valueType = valueType;
    }

    public Class<?> getDocumentType() {
        return documentType;
    }

    public void setDocumentType(Class<?> documentType) {
        this.documentType = documentType;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    @Override
    public String toString() {
        return "ViewParams [conflicts=" + conflicts + ", descending=" + descending + ", endKey=" + endKey
                + ", endKeyDocId=" + endKeyDocId + ", group=" + group + ", groupLevel=" + groupLevel + ", includeDocs="
                + includeDocs + ", attachments=" + attachments + ", attEncodingInfo=" + attEncodingInfo
                + ", inclusiveEnd=" + inclusiveEnd + ", key=" + key + ", limit=" + limit + ", reduce=" + reduce
                + ", skip=" + skip + ", stale=" + stale + ", startKey=" + startKey + ", startKeyDocId=" + startKeyDocId
                + ", updateSeq=" + updateSeq + ", designDocument=" + designDocument + ", view=" + view + ", keyType="
                + keyType + ", valueType=" + valueType + ", documentType=" + documentType + ", returnType="
                + returnType + "]";
    }

}
