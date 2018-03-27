//
//
// Starter code for the Mobile Platform Development Assignment
// Seesion 2017/2018
//
//

package mpdproject.gcu.me.org.assignmenttest1;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
//David Hesketh Mobile Platform Development Matric No:S1437170
import android.content.ClipData;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//David Hesketh Mobile Platform Development Matric No:S1437170
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private String url1 = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private String url2 = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private Button startButton;
    private String result = "";

    private ExpandableListView fullListView;
    private CustomExpandableListAdapter customExpandableAdapter;
    private HashMap<String, List<String>> listHash;
    ArrayList<String> headerDataList;
    ArrayList<String> childDataList;

    private Button searchButton;
    private EditText searchBar;
    private String initSearch;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);

        searchBar = (EditText) findViewById(R.id.searchField);

        listHash = new HashMap<>();
        fullListView = (ExpandableListView) findViewById(R.id.itemListView);

    } // End of onCreate

    public void onClick(View aview) {
        result = "";
        if (aview == startButton) {
            startProgress(url2);
        }
        if (aview == searchButton) {
            startProgress(url1);
        }
    }

    public void startProgress(String activeUrl) {
        // Run network access on a separate thread;
        new Thread(new Task(activeUrl)).start();
        Log.d(TAG, "startProgress: ");
    } //

    class Task implements Runnable {
        private String url;

        public Task(String aurl) {
            url = aurl;
        }

        @Override
        public void run() {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));


                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                    Log.e("MyTag", inputLine);

                }
                writeToFile(result);
                in.close();


            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }
            try {
                ParseXML(openFileInput("newData.xml"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");

                    fullListView.setAdapter(customExpandableAdapter);
                }
            });
        }
    }
    public void ParseXML(InputStream is) {
        InputStream input = is;
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(input, null);

            ProcessParsing(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void ProcessParsing(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList<Item> items = new ArrayList<>();

        ArrayList<String> headerList = new ArrayList<>();
        ArrayList<String> childViewText = new ArrayList<>();

        int eventType = parser.getEventType();
        Item currentItem = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String eltName = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();
                    if ("item".equals(eltName)) {
                        currentItem = new Item(getBaseContext());
                        items.add(currentItem);
                    } else if (currentItem != null) {
                        if ("title".equals(eltName)) {
                            currentItem.title = parser.nextText();
                        } else if ("description".equals(eltName)) {
                            currentItem.description = parser.nextText();
                            currentItem.CalculateDate(currentItem.start);
                            currentItem.CalculateDate(currentItem.end);
                        } else if ("link".equals(eltName)) {
                            currentItem.link = parser.nextText();
                        } else if ("point".equals(eltName)) {
                            currentItem.geoRssPoint = parser.nextText();
                        } else {
                            break;
                        }

                    }
                    break;
            }
            eventType = parser.next();
        }
        PrintItems(items);
    }

    private void PrintItems(ArrayList<Item> items) {
        String searchString;

        headerDataList = new ArrayList<String>();
        for (int i = 0; i < items.size(); i++) {
            childDataList = new ArrayList<String>();
            Item item = items.get(i);
            headerDataList.add(item.title);
            childDataList.add(item.description);
            listHash.put(headerDataList.get(i), childDataList);

        }

        if (searchBar.getText().toString() == "") {
            searchString = null;
        } else {
            searchString = searchBar.getText().toString();
        }
        FindDate(items, searchString);


    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getBaseContext().openFileOutput("newData.xml", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void FindDate(ArrayList<Item> haystack, String needle)
    {

        ArrayList<Item> refinedList = new ArrayList<Item>();
        if (needle != null) {
            listHash.clear();
            ArrayList<String> headerDataList = new ArrayList<String>();
            for (int i = 0; i < haystack.size(); i++) {
                if (haystack.get(i).title.contains(needle)|| haystack.get(i).description.contains(needle)) {
                    refinedList.add(haystack.get(i));
                    ArrayList<String> childDataList = new ArrayList<String>();
                    Item item = haystack.get(i);
                    headerDataList.add(item.title);
                    childDataList.add(item.description);
                    int j = headerDataList.size() -1;
                    listHash.put(headerDataList.get(j), childDataList);
                }
            }
        }

        headerDataList = new ArrayList<String>(listHash.keySet());
        customExpandableAdapter = new CustomExpandableListAdapter(getBaseContext(), headerDataList, listHash, refinedList);
        Log.d(TAG, "FindDate: ");

    }

}

