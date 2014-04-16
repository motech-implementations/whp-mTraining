package org.motechproject.whp.mtraining.mail;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.apache.commons.lang.StringUtils.isBlank;

public class EncryptorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptorFactory.class);

    private Properties encryptionProperties;

    public EncryptorFactory(String propertiesFileName, String key) throws IOException {
        this.encryptionProperties = readProperties(propertiesFileName, key);
    }

    private Properties readProperties(String propertiesFileName, String key) throws IOException {
        Properties sourceProperties = getPropertiesFromFile(propertiesFileName);
        if (sourceProperties.isEmpty()) {
            throw new FileNotFoundException(String.format("%s not found.Not available in class path as well.", propertiesFileName));
        }
        String keyFileName = sourceProperties.getProperty(key);
        if (isBlank(keyFileName)) {
            throw new IllegalStateException(String.format("No value specified for %s in file %s", key, propertiesFileName));
        }
        return getPropertiesFromFile(keyFileName);
    }

    public StandardPBEStringEncryptor createInstance() throws IOException {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm(encryptionProperties.getProperty("algorithm"));
        encryptor.setPassword(encryptionProperties.getProperty("key"));
        return encryptor;
    }

    private Properties getPropertiesFromFile(String source) throws IOException {
        LOGGER.error(String.format("Trying to read properties from %s", source));
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(source);
        } catch (FileNotFoundException e) {
            LOGGER.error(String.format("File not found at %s trying to read from class path instead", source));
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(source);
        } finally {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        }
        return properties;
    }

}
