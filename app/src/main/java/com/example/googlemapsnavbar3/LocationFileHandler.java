package com.example.googlemapsnavbar3;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class LocationFileHandler {

    /**
     * <p>Reads a file of locations and returns an arrayList of those locations</p>
     * @param filename The name of the file to be read from.
     * @param context  The context of the application, passed from an activity.
     * @return         An arrayList of locations read from the file
     * @throws IOException
     * @author Ricky Chu
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static ArrayList<Location> loadLocationsFromFile(String filename ,Context context) throws IOException {
        //Loads a file of locations, and returns an array of safe locations

        //Taken from https://developer.android.com/training/data-storage/app-specific#java
        FileInputStream fis = context.openFileInput(filename);
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        Location location;
        ArrayList<Location> locationsList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (null != line) {
                location = stringToLocation(line);
                locationsList.add(location);
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        }
        return locationsList;
    }

    /**
     * <p>Takes in a string in the format <b>Latitude Longitude</b> and returns a location object</p>
     * @param line The string
     * @return     A location object
     * @author Ricky Chu
     */
    private static Location stringToLocation(String line){
        Log.i("Reader", "line: " + line);
        String[] split = line.split(" ");
        float lat = Float.parseFloat(split[0]);
        float lon = Float.parseFloat(split[1]);
        Location location = new Location(lat, lon);
        return location;
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
    public static String parseLocationsXML(String xml) throws ParserConfigurationException, IOException, SAXException {
        //Parses an xml string. Returns a list of longitudes and latitudes.
        //TODO refactor so that this is private.

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
    public static void saveLocationsToFile(String fileName, Context context){
        new CheckpointParser(context).execute();
    }
}
