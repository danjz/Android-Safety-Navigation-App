
package com.example.googlemapsnavbar3.places;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.googlemapsnavbar3.HttpHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public final class PlaceFileHandler {
    //https://stackoverflow.com/questions/31409982/java-best-practice-class-with-only-static-methods

    /**
     * <p>Reads a file of locations and returns an arrayList of those locations</p>
     * @param filename The name of the file to be read from.
     * @param context  The context of the application, passed from an activity.
     * @return         An arrayList of locations read from the file
     * @throws IOException There is no file to read. Call saveLocationsToFile first.
     * @author Ricky Chu
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static PlaceList loadPlacesFromFile(String filename ,Context context) throws IOException {
        //Loads a file of locations, and returns an array of safe locations

        //Taken from https://developer.android.com/training/data-storage/app-specific#java
        FileInputStream fis = context.openFileInput(filename);
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        Place place;
        PlaceList placeList = new PlaceList();

        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (null != line) {
                place = stringToPlace(line);
                placeList.add(place);
                line = reader.readLine();
                /*
                if(line == null){
                    Log.i("reader", "null");
                }
                else{
                    Log.i("reader", line);
                }
                 */
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        }
        return placeList;
    }

    /**
     * <p>Takes in a string in the format <b>Latitude Longitude</b> and returns a location object</p>
     * @param line The string
     * @return     A location object
     * @author Ricky Chu
     */
    private static Place stringToPlace(String line){
        String[] split = line.split(" ");
        Double lat = Double.parseDouble(split[0]);
        Double lon = Double.parseDouble(split[1]);
        Place place = new Place(lat, lon);
        return place;
    }

    /**
     * <p>Takes in OpenStreetmap (OSM) XML and returns a string of locations</p>
     * @param xml XML taken from the OSM API
     * @return A string containing all the locations in the format of <b>Longitude Latitude</b>
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @author Ricky Chu
     */
    private static String parseLocationsXML(String xml) throws ParserConfigurationException, IOException, SAXException {
        //Parses an xml string. Returns a list of longitudes and latitudes.
        //TODO refactor so that this is private coz it breaks encapsulation

        // Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Generating readable xml
        StringBuilder xmlStringBuilder = new StringBuilder();
        xmlStringBuilder.append(xml);
        ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
        Document doc = builder.parse(input);

        // Get way elements
        NodeList nList = doc.getElementsByTagName("way");

        String locationString = "";
        for (int i = 0; i < nList.getLength(); i++) {
            Element way = (Element) nList.item(i);
            Element center = (Element) way.getElementsByTagName("center").item(0);

            String lat = center.getAttribute("lat");
            String lon = center.getAttribute("lon");

            if (null != lat && null != lon){
                locationString = locationString + lat + " " + lon + "\n";
            }
        }
        return locationString;
    }

    /**
     * <p>Wrapper around the OpenStreetMap API caller.<b>Please don't spam this coz the API owners will get mad</b></p>
     * @param fileName The name of the file to be written to.
     * @param context The context of the application, passed from an activity.
     * @author Ricky Chu
     */
    public static void savePlacesToFile(String fileName, Context context){

        class CheckpointParser extends AsyncTask<Void, Void, String> {

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
                    String fileContents = parseLocationsXML(xml);
                    String filename = "safePlaces.txt";

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
        new CheckpointParser().execute();
    }
}
