package com.example.Courier.service.CronJobs;


import com.example.Courier.models.WeatherInput;
import com.example.Courier.repositories.WeatherRepository;
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
public class WeatherInformationFetcher {

    private final WeatherRepository weatherRepository;

    @Autowired //standard constructor
    public WeatherInformationFetcher(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @Scheduled(cron = "0 15 * * * *") // Run every hour, 15 minutes and 0 seconds into that hour
    public void updateDatabase(){
        try {
            URL url = new URL("https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (InputStream inputStream = connection.getInputStream()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();// Making parser to read XML.
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(inputStream);// Parsing XML to get it readable for code.

                NodeList stationNodes = document.getElementsByTagName("station");// Separating data by different stations.
                String timestampValue = document.getDocumentElement().getAttribute("timestamp");

                long unixTimestamp = Long.parseLong(timestampValue);
                Date date = new Date(unixTimestamp * 1000L);  // Convert to milliseconds.
                Timestamp timestamp = new Timestamp(date.getTime());

                for (int i = 0; i < stationNodes.getLength(); i++) {// Getting elements from each station to fill the database.

                    Element stationElement = (Element) stationNodes.item(i);// Get element with index i in a station.
                    String name = trygetTextContent(stationElement, "name");

                    if (name.equals("Tallinn-Harku") || name.equals("Tartu-Tõravere") || name.equals("Pärnu")){// Saves into the database only if stations can be used by the app.
                        // This saves database space and makes the process of saving faster overall.


                        Integer wmoCode = trygetIntegerContent(stationElement, "wmocode");// Getting other variables.
                        Float airTemperature = trygetFloatContent(stationElement, "airtemperature");
                        Float windSpeed = trygetFloatContent(stationElement, "windspeed");
                        String phenomenon = trygetTextContent(stationElement, "phenomenon");

                        this.weatherRepository.save(new WeatherInput(name,wmoCode,airTemperature,windSpeed,phenomenon,timestamp));// Inputting to a database.
                        }

                }
            }
            finally {
                connection.disconnect();// Disposes the connection after losing the need for it.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String trygetTextContent(Element element, String tagName) {
        Node node = element.getElementsByTagName(tagName).item(0);
        return (node != null) ? node.getTextContent() : null;
    }

    private static Integer trygetIntegerContent(Element element, String tagName) {
        String content = trygetTextContent(element, tagName);
        return (content != null && !content.isEmpty()) ? Integer.parseInt(content) : null;
    }

    private static Float trygetFloatContent(Element element, String tagName) {
        String content = trygetTextContent(element, tagName);
        return (content != null && !content.isEmpty()) ? Float.parseFloat(content) : null;
    }
}