package com.example.ananta.mythread;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private ListView listView;
    private ProgressBar progressBar;
    private LinearLayout loadingSection = null;
    private String[] listOfImages;
    private ImageView imageView = null;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.downloadUrl);
        listView= (ListView) findViewById(R.id.urlList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editText.setText(listOfImages[position]);
            }
        });
        listOfImages = getResources().getStringArray(R.array.imageUrls);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loadingSection = (LinearLayout)findViewById(R.id.loadingSection);
        imageView = (ImageView) findViewById(R.id.imageView);
        handler = new Handler();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    public void downloadImage(View view){
        String url=editText.getText().toString();
        imageView.setImageDrawable(Drawable.createFromPath(url));
        Thread myThread = new Thread(new DownloadImagesThread(url));
        myThread.start();

        //downloadImageUsingThreads(listOfImages[0]);
       // imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));


    }

    public boolean downloadImageUsingThreads(String url){

        boolean successful = false;
        URL downloadURL=null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        File file = null;
        try{

            downloadURL = new URL(url);
            connection =  (HttpURLConnection)downloadURL.openConnection();
            inputStream = connection.getInputStream();
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsoluteFile() + "/" + Uri.parse(url).getLastPathSegment());
            fileOutputStream = new FileOutputStream(file);
            int read = -1;
            byte[] buffer = new byte[1024];
            while((read = inputStream.read(buffer))!= -1){
                //Toast.makeText(this,read,Toast.LENGTH_SHORT).show();
                fileOutputStream.write(buffer,0,read);
            }
            successful = true;

        } catch (MalformedURLException e){

        } catch(IOException e){

        }
        finally{
//            this.runOnUiThread(new Runnable(){
//                @Override
//            public void run(){
//                    loadingSection.setVisibility(View.GONE);
//                }
//            });
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loadingSection.setVisibility(View.GONE);
                }
            });
            if (connection != null){
                connection.disconnect();
            }
            if(inputStream != null){
                try{
                    inputStream.close();

                }catch(IOException e){

                }

            }
            if (fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



        return successful;
    }
    private class DownloadImagesThread implements Runnable{

        private String url;

        public DownloadImagesThread(String url){
            this.url= url;
        }

        @Override
        public void run(){
//            MainActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    loadingSection.setVisibility(View.VISIBLE);
//                    imageView.setImageDrawable(Drawable.createFromPath(url));
//                }
//            });
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loadingSection.setVisibility(View.VISIBLE);

                }
            });
            downloadImageUsingThreads(url);


        }

    }

}
