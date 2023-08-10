package ru.team38.userservice.services.task;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneOffset;
import java.util.Objects;
@Slf4j
@Component
public class LocationBasedUtilities {
    @Value("${geonames.userName}")
    private String userName;

    public String getTimeZoneByCoordinates(double latitude, double longitude) {
        try {
            String apiUrl = "http://api.geonames.org/timezoneJSON?lat=" + latitude + "&lng=" + longitude + "&username=" + userName;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject jsonResponse = (JSONObject) new org.json.simple.parser.JSONParser().parse(response.toString());
            String timeZone = (String) jsonResponse.get("timezoneId");

            return Objects.requireNonNullElse(timeZone, ZoneOffset.UTC.getId());
        } catch (Exception e) {
            log.error("Ошибка c определением временной зоной", e);
            return ZoneOffset.UTC.getId();
        }
    }
}
