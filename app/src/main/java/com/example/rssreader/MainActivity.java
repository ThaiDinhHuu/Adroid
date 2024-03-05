package com.example.rssreader;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;
import android.widget.AdapterView;
import android.content.Intent;
import android.net.Uri;

public class MainActivity extends AppCompatActivity implements RssDownloader.RssDownloadListener {

    private EditText rssUrlEditText;
    private ListView rssListView;
    private RssAdapter rssAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rssUrlEditText = findViewById(R.id.rssUrlEditText);
        rssListView = findViewById(R.id.rssListView);

        ArrayList<RssItem> rssList = new ArrayList<>();
        rssAdapter = new RssAdapter(this, rssList);
        rssListView.setAdapter(rssAdapter);
        rssListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RssItem clickedItem = rssAdapter.getItem(position);
                if (clickedItem != null) {
                    String link = clickedItem.getLink();
                    if (!link.isEmpty()) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        startActivity(browserIntent);
                    }
                }
            }
        });
    }

    public void loadRss(View view) {
        String rssUrl = rssUrlEditText.getText().toString().trim();
        if (!rssUrl.isEmpty()) {
            new RssDownloader(this).execute(rssUrl);
        } else {
            rssAdapter.clear();
            rssAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRssDownloaded(String rss) {
        ArrayList<RssItem> rssItemList = parseRss(rss);
        rssAdapter.clear();
        rssAdapter.addAll(rssItemList);
        rssAdapter.notifyDataSetChanged();
    }

    private ArrayList<RssItem> parseRss(String rss) {
        ArrayList<RssItem> rssItemList = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(rss));

            int eventType = parser.getEventType();
            RssItem currentItem = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("item")) {
                            currentItem = new RssItem("", "", "");
                        } else if (currentItem != null) {
                            if (tagName.equalsIgnoreCase("title")) {
                                currentItem.setTitle(parser.nextText().trim());
                            } else if (tagName.equalsIgnoreCase("description")) {
                                currentItem.setDescription(parser.nextText().trim());
                            } else if (tagName.equalsIgnoreCase("link")) {
                                currentItem.setLink(parser.nextText().trim());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("item") && currentItem != null) {
                            rssItemList.add(currentItem);
                            currentItem = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rssItemList;
    }
}