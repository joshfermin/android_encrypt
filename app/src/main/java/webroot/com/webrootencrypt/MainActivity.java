package webroot.com.webrootencrypt;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // casts view as button via (Button)
        Button button = (Button) findViewById(R.id.dummy_file_button);
        // Callback when button is clicked:
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSDCardInfo();
                final String state = Environment.getExternalStorageState();

                if ( Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {  // we can read the External Storage...
                    getAllFilesOfDir(Environment.getExternalStorageDirectory());
                }

//                try {
//                    // Create Test Folder
//                    File sdCard = Environment.getExternalStorageDirectory();
//                    File newFolder = new File(sdCard.getAbsolutePath(), "TestFolder");
//                    newFolder.mkdir();
//                    try {
//                        // Create new File
//                        File file = new File(newFolder, "DummyFile" + ".txt");
//                        file.createNewFile();
//
//                        // Write to that file with fileWriter
//                        FileWriter f;
//                        f = new FileWriter(Environment.getExternalStorageDirectory()+ "/TestFolder/DummyFile" + ".txt");
//                        f.write("Hello World");
//                        f.flush();
//                        f.close();
//                    } catch (Exception ex){
//                        System.out.println("Error:" + ex);
//                    }
//                } catch (Exception ex) {
//                    System.out.println("Exception: "+ ex);
//                }
            }
        });
    }
    private void getAllFilesOfDir(File directory) {
        if(directory.canRead()) {
            System.out.println("Directory: " + directory.getAbsolutePath() + "\n");

            File[] files = directory.listFiles();
            System.out.println(files);
            if (files != null) {
                for (File file : files) {
                    if (file != null) {
                        if (file.isDirectory()) {  // it is a folder...
                            getAllFilesOfDir(file);
                        } else {  // it is a file...
                            System.out.println("File: " + file.getAbsolutePath() + "\n");
                        }
                    }
                }
            }
        }
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
