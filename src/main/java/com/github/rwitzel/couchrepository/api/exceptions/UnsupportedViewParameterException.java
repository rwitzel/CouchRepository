package com.github.rwitzel.couchrepository.api.exceptions;

import com.github.rwitzel.couchrepository.api.ViewParams;

/**
 * This {@link IllegalArgumentException} is thrown when the underlying Couchdb Driver does not supported a value set in
 * {@link ViewParams}.
 * 
 * @author rwitzel
 */
public class UnsupportedViewParameterException extends IllegalArgumentException {

    private static final long serialVersionUID = 580055614837609769L;

    public UnsupportedViewParameterException(String unsupportedViewParameter) {
        super("view parameter " + unsupportedViewParameter + " is not supported");
    }

}
