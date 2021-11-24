package com.example.guesstheanimal;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    // Where to get the animals: https://www.listchallenges.com/100-animals
    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Download images
        DownloadTask downloadTask = new DownloadTask();
        String result = null;

        try {
            result = downloadTask.execute("https://www.listchallenges.com/100-animals").get();
            String[] splitResult = result.split("checklist-listItems clearfix");
            String[] splitResult1 = splitResult[1].split("checklist-itemsSectionRight");

            Pattern p = Pattern.compile("src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult1[0]);

            while (m.find()) {
                String firstLetters = m.group(1).substring(0, 8);
                if (firstLetters.equals("/f/items")) {
                    // append https://www.listchallenges.com/ to the beginning
                    System.out.println("https://www.listchallenges.com/" + m.group(1));
                }

            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult1[0]);

            while (m.find()) {
                Log.i("animal name", "xxxxxxxx");
                System.out.println(m.group(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("error","error");
        }

    }
}