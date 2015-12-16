package webroot.com.webrootencrypt;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;



public class Encrypt extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        // casts view as button via (Button)
        Button button = (Button) findViewById(R.id.dummy_file_button);
        // Callback when button is clicked:
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView passwordTextView = (TextView)findViewById(R.id.textPassword);
                final String password = passwordTextView.getText().toString();
                if (password.matches("")) {
                    Context context = getApplicationContext();
                    CharSequence text = "Please add a password.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        final ArrayList<String> selectedItems = extras.getStringArrayList("filesToEncrypt");

                        new Thread(new Runnable() {
                            public void run() {
                                Socket socket = ClientService.sendToClient(selectedItems);
                                ClientService.listenToServer(socket);



                                for (int i = 0; i < selectedItems.size(); i++) {
                                    encrypt(selectedItems.get(i), password);
                                }
                            }
                        }).start();

                        Context context = getApplicationContext();
                        CharSequence text = "Files successfully sent to server.";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent intent = new Intent(Encrypt.this, ListFiles.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private boolean checkSDCardInfo() {
        String state = Environment.getExternalStorageState();
        System.out.println(state);
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            System.out.println("R/W");
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            System.out.println("R");
            return true;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            System.out.println("something wrong");
            return false;
        }
    }

    public static void encrypt(String pathToEncrypt, String password) {
        try {
            String salt = "saltysalt"; // should make this randomized per user.
            pathToEncrypt = Environment.getExternalStorageDirectory().toString() + "/" + pathToEncrypt;

            String destZipFile = pathToEncrypt + ".zip";
            Compress.zipFolder(pathToEncrypt, destZipFile);
            System.out.println("***********************************************************************************pathtoencrypt:" +  pathToEncrypt);


            // Here you read the cleartext.
            FileInputStream fis = new FileInputStream(destZipFile);
            // This stream write the encrypted text. This stream will be wrapped by another stream.
            FileOutputStream fos = new FileOutputStream(destZipFile + ".encrypted");

//            // Here you read the cleartext.
//            FileInputStream fis = new FileInputStream(pathToEncrypt.toString());
//            // This stream write the encrypted text. This stream will be wrapped by another stream.
//            FileOutputStream fos = new FileOutputStream(pathToEncrypt.toString() + ".encrypted");

            byte[] key = (salt + password).getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit

            SecretKeySpec sks = new SecretKeySpec(key, "AES");

            // Create cipher
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sks);

            // Wrap the output stream
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);

            // Write bytes
            int b;
            byte[] d = new byte[8];

            while((b = fis.read(d)) != -1) {
                cos.write(d, 0, b);
            }

            // Flush and close streams.
            cos.flush();
            cos.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decrypt()  {
        String salt = "saltysalt"; // should make this randomized per user.
        try {
            FileInputStream fis = new FileInputStream("/storage/sdcard/sched.PNG.encrypted");
            FileOutputStream fos = new FileOutputStream("/storage/sdcard/sched.PNG");

            byte[] key = (salt + "test").getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit

            SecretKeySpec sks = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            int b;
            byte[] d = new byte[8];
            while((b = cis.read(d)) != -1) {
                fos.write(d, 0, b);
            }
            fos.flush();
            fos.close();
            cis.close();
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

