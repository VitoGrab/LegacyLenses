package com.vlousada.legacylenses;

import android.graphics.Bitmap;
import android.widget.Toast;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.AdapterView;
import android.view.View;

import com.github.ma1co.pmcademo.app.BaseActivity;
import com.github.ma1co.pmcademo.app.Logger;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class TabLensesActivity extends BaseActivity{
    private ListView listView;
    List<ParsedDataSet> XmlContentHandler;
    List<ParsedDataSet> data_list1 = null;
    Preferences prefs;
    private ListAdapterLenses adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_lenses);
        //load lenses from profiles
        initControls();
        prefs = new Preferences(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ParsedDataSet myItem = data_list1.get(i);
                Legacy lens = Legacy.getLensFromProfile(myItem.getName(), myItem.getFocal(), myItem.getApertures());
                // 1) check if profile is valid
                if (!lens.isValid()) {
                    Toast.makeText(TabLensesActivity.this, lens.getErrorReason(), Toast.LENGTH_SHORT).show();
                    // NO vALID PROFILE (cancel set new legacy lens)
                } else {

                    //save preferences NAME, FOCALS and APERTURES
                    int n = lens.focalLengthsList.size();
                    if (n>1) //multi focal lengths
                        MainActivity.m_legacyZoom = true;
                    else //single focal length
                        MainActivity.m_legacyZoom = false;
                    prefs.setLegacyLensName(myItem.getName());
                    prefs.setMyApertures(android.text.TextUtils.join(";", lens.aperturesList)); //String of parsed apertures to use in Main Activity
                    prefs.setMaxFocal(lens.focalLengthsList.get(n-1).toString()); // last value from sorted list --> Max Focal Length
                    prefs.setMinFocal(lens.focalLengthsList.get(0).toString()); // first value from sorted list --> Min Focal Length

                    //init Focal and Aperture with first values from array
                    prefs.setLegacyFocal(lens.focalLengthsList.get(0).toString()); //first (wide )focal
                    prefs.setLegacyAperture(lens.aperturesList.get(0).toString()); //first (maxAperture)

                    //Return to Main Activity after lens changed
                    onBackPressed();
                }
            }

        });
    }

    private void initControls() {

        listView=(ListView) findViewById(R.id.lvLenses);
        new BackgroundTask().execute();

    }

    public class BackgroundTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            displayData();

        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                synchronized (this) {
                    saxParser();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }




    private void saxParser() {

        try {
            XmlContentHandler = new ArrayList<ParsedDataSet>();
            data_list1 = new ArrayList<ParsedDataSet>(); //lenses

            // instantiate SAX parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();

            // get the XML reader
            XMLReader xmlReader = saxParser.getXMLReader();

            // prepare and set the XML content or data handler before
            // parsing
            XmlContentHandler xmlContentHandler = new XmlContentHandler();
            xmlReader.setContentHandler(xmlContentHandler);

            // Load an updated SD profile into private storage, or use the cached copy.
            InputStream xmlStream = LLUtils.openProfilesXml(TabLensesActivity.this);
            try {
                xmlReader.parse(new InputSource(xmlStream));
            } finally {
                xmlStream.close();
            }

            // put the parsed data to a List
            List<ParsedDataSet> parsedDataSet = xmlContentHandler.getParsedData();

            // we'll use an iterator so we can loop through the data
            Iterator<ParsedDataSet> i = parsedDataSet.iterator();
            ParsedDataSet dataItem;

            while (i.hasNext()) {

                dataItem = (ParsedDataSet) i.next();
                String parentTag = dataItem.getParentTag();

                if (parentTag.equalsIgnoreCase("LENSES")) {
                    data_list1.add(dataItem);
                }

            }

        } catch (Exception e) {
           Logger.error(e.getMessage());
        }
        //sort list by name if SETTINGS are applied to sort
        if (prefs.getSettingLensesSorted().equalsIgnoreCase("YES")) {
            Collections.sort(data_list1, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    ParsedDataSet p1 = (ParsedDataSet) o1;
                    ParsedDataSet p2 = (ParsedDataSet) o2;
                    return p1.getName().compareToIgnoreCase(p2.getName());
                }
            });
        }
    }

    private void displayData(){


        adapter = new ListAdapterLenses(this,data_list1);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

    }






    @Override
    public void onResume()
    {
        super.onResume();


    }

    @Override
    protected void onPause()
    {
        super.onPause();


    }

@Override
    protected boolean onEnterKeyDown()
    {
        onBackPressed();
        return true;
    }


    @Override
    protected boolean onPlayKeyDown()
    {
       //take screenshot?
        Bitmap bitmap = LLUtils.takeScreenshot(this.findViewById(android.R.id.content).getRootView());
        LLUtils.saveBitmap(bitmap, 1);
        return true;
    }
}
