package webroot.com.webrootencrypt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import webroot.com.webrootencrypt.ClientService;

public class ListFiles extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    ListView sdCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);


        ArrayList<String> FilesInFolder = GetFiles(Environment.getExternalStorageDirectory().toString());

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, FilesInFolder);

        sdCard = (ListView)findViewById(R.id.listView);
//        sdCard.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, FilesInFolder));
        sdCard.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        sdCard.setAdapter(adapter);

//        sdCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                // Clicking on items
//                Intent intent = new Intent(ListFiles.this, Encrypt.class);
////                        .putExtra("position", fillMaps.get(position));
//                startActivity(intent);
//            }
//        });

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, ClientService.class);
        intent.putExtra("FILES_IN_SDCARD", FilesInFolder);
        startService(intent);

        Button button = (Button) findViewById(R.id.goToEncrypt);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ListFiles.this, Encrypt.class);
//                startActivity(intent);
                SparseBooleanArray checked = sdCard.getCheckedItemPositions();
                final ArrayList<String> selectedItems = new ArrayList<String>();

                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);
                    if (checked.valueAt(i))
                        selectedItems.add(adapter.getItem(position));
                }
                new Thread(new Runnable() {
                    public void run() {
                        ClientService.sendToClient(selectedItems);
                    }
                }).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_files, menu);
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

    public ArrayList<String> GetFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<>();
        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (int i = 0; i < files.length; i++)
                MyFiles.add(files[i].getName());
        }

        return MyFiles;
    }
}
