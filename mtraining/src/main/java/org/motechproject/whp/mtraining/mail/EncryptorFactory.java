package org.motechproject.whp.mtraining.mail;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.io.IOException;
import java.util.Properties;

public class EncryptorFactory {

    public String source;
    public String key;
    private Properties encryptionProperties;

    public void initialize() throws IOException {
        Properties sourceProperties = getPropertiesFromFile(source);
        String keyFileName = sourceProperties.getProperty(key);
        encryptionProperties = getPropertiesFromFile(keyFileName);
    }

    public StandardPBEStringEncryptor createInstance() throws IOException {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm(encryptionProperties.getProperty("algorithm"));
        encryptor.setPassword(encryptionProperties.getProperty("key"));
        return encryptor;
    }

    private Properties getPropertiesFromFile(String source) throws IOException {
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(source));
        return properties;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
