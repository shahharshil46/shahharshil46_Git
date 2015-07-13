package shahharshil46.sos;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 * 07-11 19:57:57.511: D/OpenGLRenderer(25249): clear (0.00, 243 - 243.00 = 0.00, 513.00 - 0.00 = 513.00, 243.00 - 0.00 = 243.00) opaque 0 <0x62055300>
 tag:^(?!.*(Power|Provider|wifi|Surface|ADB|IPC|BATTERY|Quick|libc|System.out|AES|InputReader|Phone|dalvik|mnl_linux|OpenGL|gps|Gps|Buffer|Status|Sig|SIM|Wi|wpa|Native|Activity)).*$
 Power|Provider|wifi|Surface|ADB|IPC|BATTERY|Quick|libc|System.out|AES|InputReader|Phone|dalvik|mnl_linux|OpenGL|gps|Gps|Buffer|Status|Sig|SIM|Wi|wpa|Native|Activity
 Power|Provider|wifi|Surface|ADB|IPC|BATTERY|Quick|libc|System.out|AES|InputReader|Phone|dalvik|mnl_linux|OpenGL|gps|Gps|Buffer|Status|Sig|SIM|Wi|wpa|Native|Activity
 app:(shah|com.).*$
 */

public class MainActivity extends Activity {
    private EditText emailEdit;
    private EditText subjectEdit;
    private EditText messageEdit;
    private Multipart _multipart;
    static final String[] token = {""};
    static MainActivity mainActivity;
    static Account acctSelected;
    static AccountManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        Button sendButton = (Button) findViewById(R.id.sendMail);

        showAccountsDialog(getApplicationContext());
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try{
                    new SendMailTask("shahharshil46@gmail.com", "Gmail sending by OAuth 2.0", "My Message").execute();
                }
                catch(Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    private void sendMail(String email, String subject, String messageBody, String from) {
        Session session = createSessionObject();

        try {
            new SendMailTask(email, subject, messageBody).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465"); // 587, 25, 465

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                String username="", password="";
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private class SendMailTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private String toEmail, subject, mailMessage;
        private SendMailTask(String toEmail, String subject, String mailMessage) {
            this.toEmail = toEmail;
            this.subject = subject;
            this.mailMessage = mailMessage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.d("MAIL_SENDER","Executing mail sender with OAuth 2.0 try 4");
                GMailOauthSender gMailOauthSender = new GMailOauthSender();
                gMailOauthSender.sendMail(subject, mailMessage, acctSelected.name, token[0], toEmail);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void showAccountsDialog(final Context context){
        manager = AccountManager.get(context);
        final Account[] accounts = manager.getAccountsByType("com.google");
        if(accounts.length>0){
            String accountNameArray[] = new String[accounts.length];
            for(int i=0;i<accounts.length;i++){
                Log.d("WHEREAMI","accounts[i].name "+accounts[i].name+" accounts[i].type "+accounts[i].type);
                accountNameArray[i] = accounts[i].name;
            }
            Log.d("WHEREAMI","Multiple google accounts found");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select a Google Account")
                    .setItems(accountNameArray, new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("WHEREAMI", "accounts[which].name " + accounts[which].name + " accounts[which].type " + accounts[which].type);
                            Toast.makeText(context, "You selected " + accounts[which].name, Toast.LENGTH_LONG);
                            acctSelected = accounts[which];
                        }
                    });
            AlertDialog accountSelectorDialog = builder.create();
            accountSelectorDialog.show();
        }
    }
}

