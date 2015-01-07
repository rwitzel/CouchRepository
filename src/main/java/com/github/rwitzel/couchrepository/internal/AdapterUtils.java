package com.github.rwitzel.couchrepository.internal;

import java.util.ArrayList;
import java.util.List;

import com.github.rwitzel.couchrepository.api.ViewResult;
import com.github.rwitzel.couchrepository.api.ViewResultRow;

public class AdapterUtils {

    public static <E> List<E> toList(Iterable<E> iter) {
        List<E> list = new ArrayList<E>();
        for (E item : iter) {
            list.add(item);
        }
        return list;
    }

    /**
     * Transforms the view result to a list of keys or values or documents or IDs depending on the given return type. If
     * the return type is null, the view result is not transformed.
     * 
     * @param viewResult
     * @param returnType
     *            null or "key or "value" or "doc" or "id"
     * @return Returns the transformed view result.
     */
    @SuppressWarnings("unchecked")
    public static <E> E transformViewResult(ViewResult viewResult, String returnType) {

        if (returnType == null) {
            return (E) viewResult;
        } else {
            List<Object> list = new ArrayList<Object>();
            for (ViewResultRow row : viewResult.getRows()) {

                Object obj;
                if (returnType.equals("key")) {
                    obj = row.getKey();
                } else if (returnType.equals("value")) {
                    obj = row.getValue();
                } else if (returnType.equals("doc")) {
                    obj = row.getDoc();
                } else if (returnType.equals("id")) {
                    obj = row.getId();
                } else {
                    throw new IllegalArgumentException("not supported return type: " + returnType);
                }
                list.add(obj);
            }
            return (E) list;
        }
    }

}
