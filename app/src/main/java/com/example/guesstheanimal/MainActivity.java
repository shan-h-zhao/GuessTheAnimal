package com.example.guesstheanimal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> animalUrls = new ArrayList<>();
    ArrayList<String> animalNames = new ArrayList<>();

    int currentId;
    String currentAnimalUrl;
    String currentAnimalName;

    ArrayList<String> incorrectAnimalNames;

    ImageView imageView;

    Button button0;
    Button button1;
    Button button2;
    Button button3;

    List<Button> buttons;


    public void showName() {
        String correctTag = Integer.toString(new Random().nextInt(4));

        ArrayList<Integer> answerIds = new ArrayList<Integer>();
        answerIds.add(currentId);

        Log.i("current id", String.valueOf(currentId));
        Log.i("current name", String.valueOf(currentAnimalName));
        Log.i("correct tag", String.valueOf(correctTag));

        for (Button b: buttons) {
            Log.i("button tag", String.valueOf(b.getTag()));
            if (b.getTag().equals(correctTag)) {
                Log.i("current name", String.valueOf(currentAnimalName));
                b.setText(currentAnimalName);
            } else {
                int randomId = new Random().nextInt(40);
                while (answerIds.contains(randomId)) {
                    randomId = new Random().nextInt(40);
                }
                b.setText(animalNames.get(randomId));
                answerIds.add(randomId);
                Log.i("random id", String.valueOf(randomId));
                Log.i("random name", String.valueOf(animalNames.get(randomId)));
            }
        }
    }

    public void downloadImage () {
        ImageDownloader task = new ImageDownloader();
        Bitmap bitmap;

        try {
            bitmap = task.execute(currentAnimalUrl).get();
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Create a DownloadTask class for downloading image url and image names
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

    // Create an ImageDownloader class to download images, given urls
    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return bitmap;

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

        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);


        // Parse the target url to get a list of image urls
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
                    animalUrls.add("https://www.listchallenges.com/" + m.group(1));
                }

            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult1[0]);

            while (m.find()) {
                System.out.println(m.group(1));
                animalNames.add(m.group(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        // Load the imageView with the first image
        currentAnimalUrl = animalUrls.get(0);
        downloadImage();

        // Show animal names on buttons
        buttons = new ArrayList<Button>();
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);

        currentAnimalName = animalNames.get(0);
        currentId = 0;

        showName();









    }
}