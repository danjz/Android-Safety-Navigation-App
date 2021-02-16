package com.example.googlemapsnavbar3;

import android.content.Context;
import android.os.AsyncTask;

import org.xml.sax.SAXException;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public class CheckpointParser extends AsyncTask<Void, Void, String> {

    Context context;

    public CheckpointParser(Context context) {
        this.context = context;
    }

    /**
     * <p>Runs on a background thread. Calls the HttpHandler API caller.<p>
     * @param voids The parameters of the task.
     * @return A string containing the XML from the HttpHandler API call.
     * @see #onPostExecute
     * @author Ricky Chu
     */
    @Override
    protected String doInBackground(Void... voids) {
        HttpHandler handler = new HttpHandler();
        String xml = handler.getLocations("Cardiff");
        return xml;
    }

    /**
     * <p>Ran after doInBackground. If the xml is valid then save it to a file.</p>
     * @param xml The XML from the HttpHandler API call.
     * @author Ricky Chu
     */
    @Override
    protected void onPostExecute(String xml) {
        try {
            String fileContents = LocationFileHandler.parseLocationsXML(xml);
            String filename = "safeLocations.txt";

            //taken from https://developer.android.com/training/data-storage/app-specific#java
            try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
                fos.write(fileContents.getBytes());
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}

