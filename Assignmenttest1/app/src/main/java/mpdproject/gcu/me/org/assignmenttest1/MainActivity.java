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

import android.content.ClipData;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private String url1 = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private String url2 = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String url3 = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private TextView urlInput;
    private Button startButton;
    private String result = "";
    private ListView dataListView;
    private Button searchButton;
    private EditText searchBar;
    private String initSearch;
    ItemListAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);
        searchBar = (EditText) findViewById(R.id.searchField);
        initSearch = searchBar.getText().toString();

        dataListView = (ListView) findViewById(R.id.itemListView);
        searchBar.setEnabled(false);
        dataListView.setEnabled(false);

    } // End of onCreate

    public void onClick(View aview)
    {
        if (aview == startButton) {
            dataListView.setEnabled(true);
            startProgress();
        }
        if (aview == searchButton)
        {
            searchBar.setEnabled(true);
        }
    }

    public void startProgress() {
        // Run network access on a separate thread;

        new Thread(new Task(url2)).start();
    } //

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
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

            // (could be from a resource or ByteArrayInputStream or ...)




            Log.e("MyTag", "in run");

            try {
                Log.e("MyTag", "in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                //
                // Throw away the first 2 header lines before parsing
                //
                //
                //
                while ((inputLine = in.readLine()) != null) {
                    result = result + inputLine;
                    Log.e("MyTag", inputLine);

                }
                writeToFile(result);
                ParseXML(getBaseContext().openFileInput("newDat.xml"));
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception");
            }

            //
            // Now that you have the xml data you can parse it
            //

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    dataListView.setAdapter(adapter);
                }
            });
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
            int eventType = parser.getEventType();
            Item currentItem = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String eltName = null;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        eltName = parser.getName();
                        if ("item".equals(eltName)) {
                            currentItem = new Item();
                            items.add(currentItem);
                        } else if (currentItem != null) {
                            if ("title".equals(eltName)) {
                                currentItem.title = parser.nextText();
                            } else if ("description".equals(eltName)) {
                                currentItem.description = parser.nextText();
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
            StringBuilder builder = new StringBuilder();

            String[] listItems = new String[items.size()];
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                listItems[i] = item.title;
            }
            if (searchBar.getText().toString() == initSearch)
            {
                searchString = null;
            }
            else
            {
                searchString = searchBar.getText().toString();
            }
            items = FindDate(items, searchString);

            adapter = new ItemListAdapter(getBaseContext(), items);
        }

        private void writeToFile(String data) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getBaseContext().openFileOutput("newDat.xml", Context.MODE_PRIVATE));
                outputStreamWriter.write(data);
                outputStreamWriter.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

        private  ArrayList<Item> FindDate(ArrayList<Item> haystack, String needle)
        {
            ArrayList<Item> refinedList = new ArrayList<Item>();
            if (needle != null)
            {
                for (Item i : haystack)
                {
                    if (i.title.contains(needle))
                    {
                        refinedList.add(i);
                    }
                    if (i.description.contains(needle))
                    {
                        refinedList.add(i);
                    }
                }
            }
            else {refinedList = haystack;}
            return refinedList;
            }

        }
    }

