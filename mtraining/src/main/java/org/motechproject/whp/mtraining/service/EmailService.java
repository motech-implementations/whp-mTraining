package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.mail.Mail;
import org.springframework.mail.MailException;

public interface EmailService {
    void send(Mail message) throws MailException;
}
