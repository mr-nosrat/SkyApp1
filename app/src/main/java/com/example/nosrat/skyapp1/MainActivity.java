package com.example.nosrat.skyapp1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends Activity {
    ImageView imageView;
    TextView skyDate,skyTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView= findViewById(R.id.skymap);
        skyDate= findViewById(R.id.skydate);
        skyTime= findViewById(R.id.skytime);


         findViewById(R.id.getmap).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 ((Button)findViewById(R.id.getmap)).setText("Loading SkyMap");
                 imageView.setImageBitmap(null);
                 new Thread(new ClientThread()).start();
                 ((Button)findViewById(R.id.getmap)).setText("Get SkyMap");
             }
         });

    }

    class ClientThread implements Runnable{

        public void run() {

            String sData = "" ;
//            StringBuilder imageData = new StringBuilder();
            StringBuffer imageData = new StringBuffer();
            String serverName = "194.5.176.140";
            int port = 7070;//Integer.parseInt("");
            try {
                System.out.println("Connecting to " + serverName + " on port " + port);
                Socket client = new Socket(serverName, port);

                System.out.println("Just connected to " + client.getRemoteSocketAddress());
                OutputStream outToServer = client.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);
                String[] sDate= skyDate.getText().toString().split("/");
                String[] sTime= skyTime.getText().toString().split(":");
                out.writeUTF("https://in-the-sky.org/skymap2.php?no_cookie=1&latitude=43.26&longitude=-86.02&timezone=-4.00&year="+sDate[0]+"&month="+sDate[1]+"&day="+sDate[2]+"&hour="+sTime[0]+"&min="+sTime[1]+"&PLlimitmag=0&zoom=175&ra=17.95833&dec=43.26361");// + client.getLocalSocketAddress());
                InputStream inFromServer = client.getInputStream();
                DataInputStream in = new DataInputStream(inFromServer);
                try {
                    while ((sData = in.readUTF()) != null) {
                        System.out.println(sData);
                        imageData.append(sData);
                    }
                } catch (Exception e) {
                }
                client.close();
            } catch (Exception e) {
                System.out.println("error : " + e.getMessage());
            }
            System.out.println(imageData);

            byte[] decodedString = Base64.decode(imageData.toString().split(",")[1], Base64.DEFAULT);
            final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    imageView.setImageBitmap(decodedByte);

                }
            });

        }
    }
}
