package com.example.guesstheanimal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    String correctTag;

    ImageView imageView;

    TextView textView;

    Button nextButton;

    Button button0;
    Button button1;
    Button button2;
    Button button3;

    List<Button> buttons;

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


    // Create a function to download image given an url and display it in imageView
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

    // Create a function to show the correct and three random incorrect names on the buttons
    public void showName() {
        correctTag = Integer.toString(new Random().nextInt(4));

        ArrayList<Integer> answerIds = new ArrayList<Integer>();
        answerIds.add(currentId);

        for (Button b: buttons) {
            if (b.getTag().equals(correctTag)) {
                b.setText(currentAnimalName);
            } else {
                int randomId = new Random().nextInt(40);
                while (answerIds.contains(randomId)) {
                    randomId = new Random().nextInt(40);
                }
                b.setText(animalNames.get(randomId));
                answerIds.add(randomId);
            }
        }
    }


    // Create a function to load a random image and display names
    public void showRandAnimal () {
        currentId = new Random().nextInt(40);
        currentAnimalUrl = animalUrls.get(currentId);
        downloadImage();

        buttons = new ArrayList<Button>();
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);

        currentAnimalName = animalNames.get(currentId);

        showName();
    }

    // Create a button onClick function to check if the correct answer is picked
    public void checkResults (View view) {
        if (view.getTag().equals(correctTag)) {
            textView.setText("Correct!");
        } else {
            textView.setText("Wrong!");
        }
        textView.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        for (Button b: buttons) {
            b.setEnabled(false);
        }
    }

    // Create a button onClick function to move to the next image
    public void showNext (View view) {
        showRandAnimal();
        textView.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);
        for (Button b: buttons) {
            b.setEnabled(true);
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
        textView = findViewById(R.id.textView);
        nextButton = findViewById(R.id.nextButton);

        // Set textView and nextButton invisible
        nextButton.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);

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


        // Load a random animal to start
        showRandAnimal();

    }
}