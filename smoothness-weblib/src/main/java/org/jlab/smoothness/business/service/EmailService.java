package org.jlab.smoothness.business.service;

import org.jlab.smoothness.business.exception.UserFriendlyException;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;

public class EmailService {
    private final Session mailSession;

    public EmailService() throws UserFriendlyException {
        try {
            mailSession = (Session) new InitialContext().lookup("mail/jlab");
        } catch (NamingException e) {
            throw new UserFriendlyException("Unable to obtain email service", e);
        }
    }

    private void doSend(Address sender, Address from, Address[] toAddresses, String subject, String body, boolean html) throws MessagingException {
        MimeMessage message = new MimeMessage(mailSession);

        message.setSender(sender);

        message.setFrom(from);

        message.setRecipients(Message.RecipientType.TO, toAddresses);
        message.setSubject(subject);

        if(html) {
            message.setContent(body, "text/html; charset=UTF-8");
        } else {
            message.setText(body, "UTF-8");
        }

        message.saveChanges();

        Transport tr = mailSession.getTransport();
        tr.connect();
        tr.sendMessage(message, message.getAllRecipients());
        tr.close();
    }

    private Address[] csvToAddressArray(String toCsv) throws AddressException {
        List<Address> addressList = new ArrayList<>();

        if(toCsv != null && !toCsv.isEmpty()) {
            String[] tokenArray = toCsv.split(",");

            for(String token: tokenArray) {
                if(!token.trim().isEmpty()) {
                    Address address = new InternetAddress(token.trim());
                    addressList.add(address);
                }
            }
        }

        return addressList.toArray(new Address[] {});
    }

    public void sendEmail(String sender, String from, String toCsv, String subject, String body, boolean html) throws UserFriendlyException {
        try {
            Address senderAddress = new InternetAddress(sender);
            Address fromAddress = new InternetAddress(from);

            if(sender == null || sender.isEmpty()) {
                throw new UserFriendlyException("sender email address must not be empty");
            }

            if(from == null || from.isEmpty()) {
                throw new UserFriendlyException("from email address must not be empty");
            }

            if(toCsv == null || toCsv.isEmpty()) {
                throw new UserFriendlyException("to email address must not be empty");
            }

            if (subject == null || subject.isEmpty()) {
                throw new UserFriendlyException("subject must not be empty");
            }

            if (body == null || body.isEmpty()) {
                throw new UserFriendlyException("message must not be empty");
            }

            Address[] toAddresses = csvToAddressArray(toCsv);

            doSend(senderAddress, fromAddress, toAddresses, subject, body, html);
        } catch (AddressException e) {
            throw new IllegalArgumentException("Invalid address", e);
        } catch (MessagingException e) {
            throw new IllegalArgumentException("Unable to send email", e);
        }
    }
}