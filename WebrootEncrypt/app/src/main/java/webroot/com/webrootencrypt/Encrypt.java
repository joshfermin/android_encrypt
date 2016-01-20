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

import java.io.File;
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



public class Encrypt extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        // casts view as button via (Button)
        Button encryptButton = (Button) findViewById(R.id.dummy_file_button);
        Button decryptButton = (Button) findViewById(R.id.decrypt);

        // Callback when button is clicked:
        decryptButton.setOnClickListener(this);
        encryptButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        final Bundle extras = getIntent().getExtras();
        final ArrayList<String> selectedItems = extras.getStringArrayList("filesToEncrypt");

        TextView passwordTextView = (TextView) findViewById(R.id.textPassword);
        final String password = passwordTextView.getText().toString();

        switch(v.getId()){
            case R.id.decrypt:
                if (password.matches("")) {
                    emptyPassNotif();
                } else {
                    for (int i = 0; i < selectedItems.size(); i++) {
                        decrypt(selectedItems.get(i), password);
                    }


                    Intent intent = new Intent(Encrypt.this, ListFiles.class);
                    startActivity(intent);
                }
                break;
            case R.id.dummy_file_button:
                if (password.matches("")) {
                    emptyPassNotif();
                } else {
                    if (extras != null) {

                        new Thread(new Runnable() {
                            public void run() {
                                Socket socket = ClientService.sendToClient(selectedItems);
                                ClientService.listenToServer(socket);
                            }
                        }).start();

                        for (int i = 0; i < selectedItems.size(); i++) {
                            encrypt(selectedItems.get(i), password);
                        }

                        Context context = getApplicationContext();
                        CharSequence text = "Files successfully sent to server.";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        Intent intent = new Intent(Encrypt.this, ListFiles.class);
                        startActivity(intent);
                    }
                }
                break;
        }
    }

    private void emptyPassNotif() {
        Context context = getApplicationContext();
        CharSequence text = "Please add a password.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
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

            // Here you read the cleartext.
            FileInputStream fis = new FileInputStream(destZipFile);

            // This stream write the encrypted text. This stream will be wrapped by another stream.
            if (destZipFile.endsWith(".zip")) {
                destZipFile = destZipFile.substring(0, destZipFile.length() - 4);
            }
            FileOutputStream fos = new FileOutputStream(destZipFile + ".encrypted");

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

            File directory = new File(pathToEncrypt);
            deleteDirectory(directory);
            File zip = new File(destZipFile + ".zip");
            zip.delete();

            // Flush and close streams.
            cos.flush();
            cos.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decrypt(String pathToEncrypt, String password)  {
        String salt = "saltysalt"; // should make this randomized per user.
        try {
            pathToEncrypt = Environment.getExternalStorageDirectory().toString() + "/" + pathToEncrypt;
            FileInputStream fis = new FileInputStream(pathToEncrypt);
            if (pathToEncrypt.endsWith(".encrypted")) {
                pathToEncrypt = pathToEncrypt.substring(0, pathToEncrypt.length() - 10);
            } else {
                Context context = getApplicationContext();
                CharSequence text = "File is not encrypted. Please choose another.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                return;
            }
            FileOutputStream fos = new FileOutputStream(pathToEncrypt  + ".zip");

            byte[] key = (salt + password).getBytes("UTF-8");
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

            Compress.unzipFolder(pathToEncrypt  + ".zip");

            Context context = getApplicationContext();
            CharSequence text = "Files successfully decrypted.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();


            File encrypted = new File(pathToEncrypt + ".encrypted");
            encrypted.delete();
            File zip = new File(pathToEncrypt + ".zip");
            zip.delete();


            fos.flush();
            fos.close();
            cis.close();

        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }


    private static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
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

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

