package org.motechproject.whp.mtraining.service.impl;

import org.apache.log4j.Logger;
import org.motechproject.whp.mtraining.mail.Mail;
import org.motechproject.whp.mtraining.mail.MailMimePreparator;
import org.motechproject.whp.mtraining.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("emailService")
public class EmailServiceImpl implements EmailService {
    @Autowired
    @Qualifier("javaMailSender")
    private JavaMailSender javaMailSender;
    private static final Logger LOG = Logger.getLogger(EmailServiceImpl.class);

    @Override
    public void send(final Mail mail) {
        LOG.info(String.format("Sending message [%s] from [%s] to [%s] with subject [%s].",
                mail.getMessage(), mail.getFromAddress(), mail.getToAddress(), mail.getSubject()));
        javaMailSender.send(getMimeMessagePreparator(mail));
    }

    MailMimePreparator getMimeMessagePreparator(Mail mail) {
        return new MailMimePreparator(mail);
    }
}
