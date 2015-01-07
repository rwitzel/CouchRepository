package com.github.rwitzel.couchrepository.ektorp;

import org.ektorp.ViewQuery;

import com.github.rwitzel.couchrepository.api.ViewParams;
import com.github.rwitzel.couchrepository.api.exceptions.UnsupportedViewParameterException;

/**
 * This internal class assists in configuring a {@link ViewQuery}.
 * 
 * @author rwitzel
 */
public class EktorpCouchViewConfigurer {

    /**
     * Configures Ektorp's {@link ViewQuery} with the given {@link ViewParams}.
     * 
     * @param view
     * @param params
     */
    public void configure(ViewQuery view, ViewParams params) {
        if (params.getAttachments() != null) {
            throw new UnsupportedViewParameterException("attachments");
        }
        if (params.getAttEncodingInfo() != null) {
            throw new UnsupportedViewParameterException("att_encoding_info");
        }
        if (params.getConflicts() != null) {
            throw new UnsupportedViewParameterException("conflicts");
        }
        if (params.getDescending() != null) {
            view.descending(params.getDescending());
        }
        if (params.getGroup() != null) {
            view.group(params.getGroup());
        }
        if (params.getIncludeDocs() != null) {
            view.includeDocs(params.getIncludeDocs());
        }
        if (params.getInclusiveEnd() != null) {
            view.inclusiveEnd(params.getInclusiveEnd());
        }
        if (params.getReduce() != null) {
            view.reduce(params.getReduce());
        }
        if (params.getUpdateSeq() != null) {
            view.updateSeq(params.getUpdateSeq());
        }
        if (params.getEndKey() != null) {
            view.endKey(params.getEndKey());
        }
        if (params.getEndKeyDocId() != null) {
            view.endDocId(params.getEndKeyDocId());
        }
        if (params.getGroupLevel() != null) {
            view.groupLevel(params.getGroupLevel());
        }
        if (params.getKey() != null) {
            view.key(params.getKey());
        }
        if (params.getLimit() != null) {
            view.limit(params.getLimit());
        }
        if (params.getSkip() != null) {
            view.skip(params.getSkip());
        }
        if (params.getStale() != null) {
            if ("ok".equals(params.getStale())) {
                view.staleOk(true);
            } else if ("update_after".equals(params.getStale())) {
                view.staleOkUpdateAfter();
            }
        }
        if (params.getStartKey() != null) {
            view.startKey(params.getStartKey());
        }
        if (params.getStartKeyDocId() != null) {
            view.startDocId(params.getStartKeyDocId());
        }

        //

        if (params.getDesignDocument() != null) {
            view.designDocId("_design/" + params.getDesignDocument());
        }
        if (params.getView() != null) {
            view.viewName(params.getView());
        }
    }

}
