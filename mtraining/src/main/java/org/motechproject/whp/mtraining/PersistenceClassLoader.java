package org.motechproject.whp.mtraining;

public class PersistenceClassLoader extends ClassLoader {

    public static final PersistenceClassLoader INSTANCE = new PersistenceClassLoader();

    public PersistenceClassLoader() {
        this(PersistenceClassLoader.class.getClassLoader());
    }

    public PersistenceClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> defineClass(String name, byte[] bytecode) {
        return defineClass(name, bytecode, 0, bytecode.length);
    }
}
