package org.motechproject.whp.mtraining.mail;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class EncryptorFactoryIT {

    @Test
    public void shouldReturnEncrpytorForClassPathBasedFile() throws IOException {
        EncryptorFactory encryptorFactory = new EncryptorFactory("test-mtraining.properties", "whp.key.source");
        StandardPBEStringEncryptor instance = encryptorFactory.createInstance();
        assertNotNull(instance);
        String encryptedPassword = "gxSVqAPcsRGAeZj9aus0/3SmMg5Ll03h";
        String decryptedPassword = "password";
        assertEquals(instance.decrypt(encryptedPassword), decryptedPassword);
    }

    @Test
    public void shouldReturnEncrpytorForAbsolutePathBasedFile() throws IOException {
        EncryptorFactory encryptorFactory = new EncryptorFactory("test-mtraining.properties", "whp.key.remote.source");
        StandardPBEStringEncryptor instance = encryptorFactory.createInstance();
        assertNotNull(instance);
        String encryptedPassword = "gxSVqAPcsRGAeZj9aus0/3SmMg5Ll03h";
        String decryptedPassword = "password";
        assertEquals(instance.decrypt(encryptedPassword), decryptedPassword);
    }
}
