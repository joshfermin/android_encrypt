package webroot.com.webrootencrypt;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by jfermin on 11/9/2015.
 */
public class ClientService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public ClientService() {
        super(ClientService.class.getName());
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onHandleIntent(Intent intent) {
        // Let it continue running until it is stopped.
//        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        sendToClient("Hello");
//        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    protected String sendToClient(String... args) {
        try {
            InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
            Log.d("TCP", "C: Connecting...");

            Socket socket = new Socket(serverAddr, 8080);

            String message = "1";

            PrintWriter out = null;
            BufferedReader in = null;

            try {
                Log.d("TCP", "C: Sending: '" + message + "'");
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println(message);
//                while ((in.readLine()) != null) {
//                    txt.append(in.readLine());
//                }

                Log.d("TCP", "C: Sent.");
                Log.d("TCP", "C: Done.");

            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                socket.close();
            }

        } catch (UnknownHostException e) {
            Log.e("TCP", "C: UnknownHostException", e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("TCP", "C: IOException", e);
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e){
            e.printStackTrace();
        }
        return("Success");
    }
}
