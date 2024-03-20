package com.example.Courier.service;

import com.example.Courier.CourierApplication;
import com.example.Courier.model.WeatherInput;
import com.example.Courier.repository.WeatherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;

@Service
public class CronJobService {

    private final WeatherRepo weatherRepo;

    @Autowired
    public CronJobService(WeatherRepo weatherRepo) {
        this.weatherRepo = weatherRepo;
    }

    @Scheduled(cron = "0 15 * * * *") // Run every hour, 15 minutes and 0 seconds into that hour
    public void updateWeatherData() {
        updateDatabase(weatherRepo, "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php");
    }

    public static void updateDatabase(WeatherRepo repo, String urlString){
        try {
            System.out.println("Updating database");
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (InputStream inputStream = connection.getInputStream()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();//Making parser to read XML
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(inputStream);//parsing XML

                NodeList stationNodes = document.getElementsByTagName("station");//separating data by different stations
                if (stationNodes.getLength() > 0){//if the web page contained weather information
                    repo.deleteAll();//wipe old records
                }
                //getting timestamp
                String timestampValue = document.getDocumentElement().getAttribute("timestamp");
                long unixTimestamp = Long.parseLong(timestampValue);
                Date date = new Date(unixTimestamp * 1000L);  // Convert to milliseconds
                Timestamp timestamp = new Timestamp(date.getTime());

                for (int i = 0; i < stationNodes.getLength(); i++) {//getting elements from each station

                    Element stationElement = (Element) stationNodes.item(i);//get element with index i in a station
                    String name = getTextContent(stationElement, "name");

                    if (name.equals("Tallinn-Harku") || name.equals("Tartu-Tõravere") || name.equals("Pärnu")){//Saves into the database only if stations can be used by the app
                        //this saves database space and makes the process of saving faster overall


                        Integer wmocode = getIntegerContent(stationElement, "wmocode");//getting other variables
                        Float airTemperature = getFloatContent(stationElement, "airtemperature");
                        Float windSpeed = getFloatContent(stationElement, "windspeed");
                        String phenomenon = getTextContent(stationElement, "phenomenon");

                        repo.save(new WeatherInput(name,wmocode,airTemperature,windSpeed,phenomenon,timestamp));//inputting to a database
                        }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String getTextContent(Element element, String tagName) {
        Node node = element.getElementsByTagName(tagName).item(0);
        return (node != null) ? node.getTextContent() : null;
    }

    private static Integer getIntegerContent(Element element, String tagName) {
        String content = getTextContent(element, tagName);
        return (content != null && !content.isEmpty()) ? Integer.parseInt(content) : null;
    }

    private static Float getFloatContent(Element element, String tagName) {
        String content = getTextContent(element, tagName);
        return (content != null && !content.isEmpty()) ? Float.parseFloat(content) : null;
    }
}