package shahharshil46.sos;

/**
 * Created by HOME on 12-04-2015.
 */
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;
import android.util.Log;

import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;

public class GMailOauthSender {
    private Session session;


    public SMTPTransport connectToSmtp(String host, int port, String userEmail,
                                       String oauthToken, boolean debug) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.sasl.enable", "false");
        props.put("mail.smtp.auth", "false");
        /*props.setProperty("mail.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", port+"");
        props.put("mail.smtp.socketFactory.port", port+"");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");
        props.put("mtail.smtp.starttls.enable", "true");
        // IMAP provider
        props.setProperty( "mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // POP3 provider
        props.setProperty( "mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // IMAP provider
        props.setProperty( "mail.imap.socketFactory.fallback", "false");
        // POP3 provider
        props.setProperty( "mail.pop3.socketFactory.fallback", "false");
        // IMAP provider
        props.setProperty( "mail.imap.port", "993");
        props.setProperty( "mail.imap.socketFactory.port", "993");
        // POP3 provider
        props.setProperty( "mail.pop3.port", "995");
        props.setProperty( "mail.pop3.socketFactory.port", "995");*/
        session = Session.getInstance(props);
        session.setDebug(debug);


        final URLName unusedUrlName = null;
        SMTPTransport transport = new SMTPTransport(session, unusedUrlName);
        // If the password is non-null, SMTP tries to do AUTH LOGIN.
        final String emptyPassword = null;
        transport.connect(host, port, userEmail, emptyPassword);

        byte[] response = String.format("user=%s\1auth=Bearer %s\1\1", userEmail,
                oauthToken).getBytes();
        response = BASE64EncoderStream.encode(response);

        transport.issueCommand("AUTH XOAUTH2 " + new String(response),
                235);

        return transport;
    }

    public synchronized void sendMail(String subject, String body, String user,
                                      String oauthToken, String recipients) {
        try {
            AccountManagerFuture<Bundle> authBundle = MainActivity.manager.getAuthToken(MainActivity.acctSelected,"oauth2:https://mail.google.com/", null, MainActivity.mainActivity, new OnTokenAcquired(), null);
            Bundle acctBundle = authBundle.getResult();
            /*MainActivity.token[0] = acctBundle.get(AccountManager.KEY_AUTHTOKEN).toString();*/
            MainActivity.token[0] = AccountManager.get(MainActivity.mainActivity).getAuthToken(
                    new Account(MainActivity.acctSelected.name, "com.google"),
                    "oauth2:https://mail.google.com/", true, null, null).getResult().getString(AccountManager.KEY_AUTHTOKEN);
            MainActivity.manager.invalidateAuthToken("com.google",MainActivity.token[0]);
            String newToken = "";
            try {
                newToken = AccountManager.get(MainActivity.mainActivity).getAuthToken(
                        new Account(MainActivity.acctSelected.name, "com.google"),
                        "oauth2:https://mail.google.com/", true, null, null).getResult().getString(AccountManager.KEY_AUTHTOKEN);
                MainActivity.token[0] = newToken;
                Log.d("MAIL_SENDER", "Auth Token is " + MainActivity.token[0]);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            SMTPTransport smtpTransport = connectToSmtp("smtp.gmail.com",
                    25,
                    user,
                    newToken,
                    true);

            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(user));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            smtpTransport.sendMessage(message, message.getAllRecipients());


        } catch (Exception e)
        {
            Log.d("MAIL_SENDER", e.getMessage());
        }

    }
    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result){
            try{
                Bundle bundle = result.getResult();
                MainActivity.token[0] = bundle.getString(AccountManager.KEY_AUTHTOKEN);

            } catch (Exception e){
                Log.d("test", e.getMessage());
            }
        }
    }
}