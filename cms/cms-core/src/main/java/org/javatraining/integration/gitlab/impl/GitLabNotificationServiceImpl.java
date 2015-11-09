package org.javatraining.integration.gitlab.impl;

import org.apache.velocity.app.VelocityEngine;
import org.javatraining.config.system.MailPropKeys;
import org.javatraining.config.system.props.ApplicationProperty;
import org.javatraining.integration.gitlab.api.model.GitLabUser;
import org.javatraining.notification.email.impl.MailNotification;
import org.javatraining.notification.email.interfaces.NotificationService;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The project name is cms.
 * Created by sergey on 17.06.15 at 13:26.
 * For more information you should send mail to codedealerb@gmail.com
 */
@Dependent
public class GitLabNotificationServiceImpl implements NotificationService<GitLabUser> {
    private static final String MAIL_TRANSPORT_PROTOCOL = "smtp";
    private static final String MAIL_SMTP_AUTH = "true";
    private static final String MAIL_SSL_PORT = "465";
    private static final String MAIL_TLS_PORT = "587";
    private static final String MAIL_SSL_SOCKET_FACTORY_CLASS = "javax.net.ssl.SSLSocketFactory";
    private static final String MAIL_SOCKET_FACTORY_FALLBACK = "false";
    private static final String MAIL_DEBUG_ENABLE = "false";

    @Inject
    @ApplicationProperty(name = MailPropKeys.PROP_HOST)
    private String mailHost;
    @Inject
    @ApplicationProperty(name = MailPropKeys.PROP_PORT)
    private String mailPort;
    @Inject
    @ApplicationProperty(name = MailPropKeys.PROP_EMAIL)
    private String mailEmail;
    @Inject
    @ApplicationProperty(name = MailPropKeys.PROP_PSWRD)
    private String mailPswrd;

    @Override
    public void sendNotificationToEndPoint(String subject, String message, GitLabUser user) {
        Properties props = new Properties();
        try {
            props.put("mail.transport.protocol", MAIL_TRANSPORT_PROTOCOL);
            props.put("mail.host", mailHost);
            props.put("mail.smtp.auth", MAIL_SMTP_AUTH);
            props.put("mail.smtp.port", mailPort);
            props.put("mail.debug", MAIL_DEBUG_ENABLE);

            if (mailPort.equals(MAIL_SSL_PORT)) {
                props.put("mail.smtp.socketFactory.class", MAIL_SSL_SOCKET_FACTORY_CLASS);
                props.put("mail.smtp.socketFactory.port", mailPort);
                props.put("mail.smtp.socketFactory.fallback", MAIL_SOCKET_FACTORY_FALLBACK);
            } else if (mailPort.equals(MAIL_TLS_PORT)) {
                props.put("mail.smtp.starttls.enable", "true");
            }

            String from = mailEmail;
            String password = mailPswrd;

            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(from, password);
                        }
                    });

            Transport transport = session.getTransport();
            InternetAddress addressFrom = new InternetAddress(from);

            VelocityEngine velocityEngine = MailNotification.createEngine();

            MimeMessage mMessage = new MimeMessage(session);
            mMessage.setSender(addressFrom);
            mMessage.setSubject(subject);

            Map<String, String> model = new HashMap<>();
            model.put("username", user.getName());
            model.put("password", user.getPassword());
            mMessage.setContent(VelocityEngineUtils
                            .mergeTemplateIntoString(velocityEngine, "velocity/gitlab-mail-template.vm", "UTF-8", model),
                    "text/html; charset=UTF-8");

            mMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
            mMessage.setSubject("Регистрация в GitLab");

            transport.connect();
            Transport.send(mMessage);
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
