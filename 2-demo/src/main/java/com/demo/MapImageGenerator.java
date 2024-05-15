package com.demo;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class MapImageGenerator {

    public static JsonNode get(URL url) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(url);
    }

    public static Image getMapImage(JsonNode coordinates) throws IOException {
        String endpoint = "https://api.mapbox.com/styles/v1/mapbox/dark-v11/static/"+ coordinates.get(0) + "," +  coordinates.get(1) + ",9.59,0/300x300?access_token=pk.eyJ1IjoibmFlZW1hcnNhbGFuIiwiYSI6ImNsdWxmMGdsaTBoaWoybG10NHV4YWYwbzYifQ.M_ZPK8gsKTMNaWaLgz2vQw"; 
        URL url = new URL(endpoint);
        Image image = ImageIO.read(url);
        return image;
    }

    public static JsonNode getCoordinates(String city, String country) throws IOException  {
        String endpoint = "https://api.mapbox.com/geocoding/v5/mapbox.places/"+ city +"%2C%20" + country + ".json?language=en&access_token=" + System.getenv("MAPBOX_ACCESS_TOKEN");
        URL url = new URL(endpoint);
        JsonNode obj = get(url);
        JsonNode coordinates = obj.get("features").get(0).get("geometry").get("coordinates");
        return coordinates;
    }



   
}
