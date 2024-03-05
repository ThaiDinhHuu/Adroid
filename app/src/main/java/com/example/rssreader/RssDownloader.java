package com.example.rssreader;

import android.os.AsyncTask;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class RssDownloader extends AsyncTask<String, Void, String> {

    private RssDownloadListener listener;

    public RssDownloader(RssDownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... strings) {
        String urlString = strings[0];
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
            if (scanner.hasNext()) {
                return scanner.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s != null) {
            listener.onRssDownloaded(s);
        }
    }

    public interface RssDownloadListener {
        void onRssDownloaded(String rss);
    }
}
