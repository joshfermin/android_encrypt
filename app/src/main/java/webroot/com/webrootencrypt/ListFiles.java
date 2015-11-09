package webroot.com.webrootencrypt;

import android.content.Intent;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListFiles extends AppCompatActivity {

    List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);

        final ListView sdCard;
        ArrayList<String> FilesInFolder = GetFiles(Environment.getExternalStorageDirectory().toString());
        sdCard = (ListView) findViewById(R.id.listView);
        sdCard.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, FilesInFolder));
//        sdCard.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);


//        sdCard.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//            @Override public boolean onPrepareActionMode(    android.view.ActionMode mode,    Menu menu){
////                mode.getMenuInflater().inflate(R.menu.bookmarks,menu);
//                return true;
//            }
//            @Override public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item){
////                BookmarkListActionHandler.handleItemSelection(item, sdCard);
//                mode.finish();
//                return true;
//            }
//        });



        sdCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Clicking on items
                new ClientAsyncTask().execute();
                Intent intent = new Intent(ListFiles.this, Encrypt.class);
//                        .putExtra("position", fillMaps.get(position));
                startActivity(intent);
            }
        });

        Button button = (Button) findViewById(R.id.goToEncrypt);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ListFiles.this, Encrypt.class);
//                startActivity(intent);
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
