package com.ar.afip.afip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.springframework.stereotype.Service;

@Service
public class LoginTicketRequestGenerator {

    public String generateLoginTicketRequest(String service) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));

        Date now = new Date();
        long uniqueId = now.getTime() / 1000;
        Date generationTime = new Date(now.getTime() - 600000); // -10 minutos
        Date expirationTime = new Date(now.getTime() + 600000); // +10 minutos

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<loginTicketRequest version=\"1.0\">\n" + "  <header>\n" + "    <uniqueId>" + uniqueId + "</uniqueId>\n" +
               "    <generationTime>" + sdf.format(generationTime) + "</generationTime>\n" +
               "    <expirationTime>" + sdf.format(expirationTime) + "</expirationTime>\n" +
               "  </header>\n" +
               "  <service>" + service + "</service>\n" +
               "</loginTicketRequest>";
    }
}