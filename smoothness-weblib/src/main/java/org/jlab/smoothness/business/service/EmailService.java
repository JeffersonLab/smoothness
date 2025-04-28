package org.jlab.smoothness.business.service;

import java.util.ArrayList;
import java.util.List;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.persistence.view.User;

/** Service for sending emails. */
public class EmailService {
  private final Session mailSession;

  /**
   * Creates a new EmailService.
   *
   * @throws UserFriendlyException If unable to initialize a mail Session from the Application
   *     Server.
   */
  public EmailService() throws UserFriendlyException {
    try {
      mailSession = (Session) new InitialContext().lookup("mail/jlab");
    } catch (NamingException e) {
      throw new UserFriendlyException("Unable to obtain email service", e);
    }
  }

  /**
   * Send an email given Addresses.
   *
   * @param sender The sender address
   * @param from The from address
   * @param toAddresses The to addresses
   * @param ccAddresses The cc addresses
   * @param subject The subject
   * @param body The body
   * @param html true if HTML body, false otherwise
   * @throws MessagingException If unable to send
   */
  private void doSend(
      Address sender,
      Address from,
      Address[] toAddresses,
      Address[] ccAddresses,
      String subject,
      String body,
      boolean html)
      throws MessagingException {
    MimeMessage message = new MimeMessage(mailSession);

    message.setSender(sender);

    message.setFrom(from);

    message.setRecipients(Message.RecipientType.TO, toAddresses);
    message.setRecipients(Message.RecipientType.CC, ccAddresses);
    message.setSubject(subject);

    if (html) {
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

  /**
   * Send a multipart email.
   *
   * @param sender The sender address
   * @param from The from address
   * @param toAddresses The to Addresses
   * @param ccAddresses The cc Addresses
   * @param subject The subject
   * @param multipart They parts
   * @throws MessagingException If unable to send
   */
  private void doSendMultipart(
      Address sender,
      Address from,
      Address[] toAddresses,
      Address[] ccAddresses,
      String subject,
      Multipart multipart)
      throws MessagingException {
    MimeMessage message = new MimeMessage(this.mailSession);
    message.setSender(sender);
    message.setFrom(from);
    message.setRecipients(Message.RecipientType.TO, toAddresses);
    message.setRecipients(Message.RecipientType.CC, ccAddresses);
    message.setSubject(subject);
    message.setContent(multipart);

    message.saveChanges();
    Transport tr = this.mailSession.getTransport();
    tr.connect();
    tr.sendMessage(message, message.getAllRecipients());
    tr.close();
  }

  /**
   * Convert a User list to a Comma Separated Values String of address strings.
   *
   * @param userList The User list
   * @return The CSV of addresses
   */
  public static String usersToAddressCsv(List<User> userList) {
    String csv = "";

    if (userList != null && !userList.isEmpty()) {
      if (userList.get(0) == null) {
        throw new IllegalArgumentException("User must not be null");
      }

      csv = userList.get(0).getEmail();
      for (int i = 1; i < userList.size(); i++) {
        if (userList.get(i) == null) {
          throw new IllegalArgumentException("User must not be null");
        }
        csv = csv + "," + userList.get(i).getEmail();
      }
    }

    return csv;
  }

  /**
   * Convert a Comma Separated Values String to an array of Addresses.
   *
   * @param toCsv The CSV String
   * @return The array
   * @throws AddressException If unable to create an InternetAddress
   */
  public static Address[] csvToAddressArray(String toCsv) throws AddressException {
    List<Address> addressList = new ArrayList<>();

    if (toCsv != null && !toCsv.isEmpty()) {
      String[] tokenArray = toCsv.split(",");

      for (String token : tokenArray) {
        if (!token.trim().isEmpty()) {
          Address address = new InternetAddress(token.trim());
          addressList.add(address);
        }
      }
    }

    return addressList.toArray(new Address[] {});
  }

  /**
   * Send an email given Strings representing addresses.
   *
   * @param sender The sender address as a String
   * @param from The from address
   * @param toCsv The to addresses as Strings
   * @param ccCsv The cc addresses as Strings
   * @param subject The subject
   * @param body The body
   * @param html true if HTML body, false otherwise
   * @throws UserFriendlyException If unable to send
   */
  public void sendEmail(
      String sender,
      String from,
      String toCsv,
      String ccCsv,
      String subject,
      String body,
      boolean html)
      throws UserFriendlyException {
    try {
      Address senderAddress = new InternetAddress(sender);
      Address fromAddress = new InternetAddress(from);

      if (sender == null || sender.isEmpty()) {
        throw new UserFriendlyException("sender email address must not be empty");
      }

      if (from == null || from.isEmpty()) {
        throw new UserFriendlyException("from email address must not be empty");
      }

      if (toCsv == null || toCsv.isEmpty()) {
        throw new UserFriendlyException("to email address must not be empty");
      }

      if (subject == null || subject.isEmpty()) {
        throw new UserFriendlyException("subject must not be empty");
      }

      if (body == null || body.isEmpty()) {
        throw new UserFriendlyException("message must not be empty");
      }

      Address[] toAddresses = csvToAddressArray(toCsv);

      Address[] ccAddresses = csvToAddressArray(ccCsv);

      doSend(senderAddress, fromAddress, toAddresses, ccAddresses, subject, body, html);
    } catch (AddressException e) {
      throw new IllegalArgumentException("Invalid address", e);
    } catch (MessagingException e) {
      throw new IllegalArgumentException("Unable to send email", e);
    }
  }

  /**
   * Send a multipart email.
   *
   * @param sender The sender address
   * @param from The from address
   * @param toCsv The to Addresses
   * @param ccCsv The cc Addresses
   * @param subject The subject
   * @param multipart They parts
   * @throws UserFriendlyException If unable to send
   */
  public void sendEmailMultipart(
      String sender, String from, String toCsv, String ccCsv, String subject, Multipart multipart)
      throws UserFriendlyException {
    try {
      Address senderAddress = new InternetAddress(sender);
      Address fromAddress = new InternetAddress(from);
      if (sender != null && !sender.isEmpty()) {
        if (from != null && !from.isEmpty()) {
          if (toCsv != null && !toCsv.isEmpty()) {
            if (subject != null && !subject.isEmpty()) {
              if (multipart != null && multipart.getCount() > 0) {
                Address[] toAddresses = EmailService.csvToAddressArray(toCsv);
                Address[] ccAddresses = EmailService.csvToAddressArray(ccCsv);
                this.doSendMultipart(
                    senderAddress, fromAddress, toAddresses, ccAddresses, subject, multipart);
              } else {
                throw new UserFriendlyException("multipart content must not be empty");
              }
            } else {
              throw new UserFriendlyException("subject must not be empty");
            }
          } else {
            throw new UserFriendlyException("to email address must not be empty");
          }
        } else {
          throw new UserFriendlyException("from email address must not be empty");
        }
      } else {
        throw new UserFriendlyException("sender email address must not be empty");
      }
    } catch (AddressException e) {
      throw new IllegalArgumentException("Invalid address", e);
    } catch (MessagingException e) {
      throw new IllegalArgumentException("Unable to send email", e);
    }
  }
}
