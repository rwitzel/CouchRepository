package com.github.rwitzel.couchrepository.internal;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;

/**
 * This {@link Paranamer} takes from parameter names from Java 8's new class <code>Parameter</code>.
 * <p>
 * Parameter names for constructors are not supported.
 * 
 * @author rwitzel
 */
public class Java8Paranamer implements Paranamer {

    /**
     * True if this instance is loaded by a Java 8 runtime environment.
     */
    private boolean java8;

    public Java8Paranamer() {
        super();
        try {
            getClass().getClassLoader().loadClass("java.lang.reflect.Parameter");
            java8 = true;
        } catch (ClassNotFoundException e) {
            // nothing to do
        }
    }

    public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {

        if (!java8) {
            throw new ParameterNamesNotFoundException("java8 required");
        }

        if (methodOrConstructor instanceof Constructor) {
            throw new ParameterNamesNotFoundException("constructors not supported yet");
        }

        boolean realParameterNameFound = false;

        Method method = (Method) methodOrConstructor;
        String[] parameterNames = new String[method.getParameterCount()];
        for (int index = 0; index < method.getParameterCount(); index++) {

            String parameterName = method.getParameters()[index].getName();
            parameterNames[index] = parameterName;

            if (!parameterName.matches("arg\\d+")) {
                realParameterNameFound = true;
            }

        }

        if (parameterNames.length > 0 && !realParameterNameFound) {
            throw new ParameterNamesNotFoundException("javac argument '-parameters' is most probably not activated");
        }
        return parameterNames;
    }

    public String[] lookupParameterNames(AccessibleObject methodOrConstructor, boolean throwExceptionIfMissing) {
        try {
            return lookupParameterNames(methodOrConstructor);
        } catch (ParameterNamesNotFoundException e) {
            if (throwExceptionIfMissing) {
                throw e;
            } else {
                return null;
            }
        }

    }

}
