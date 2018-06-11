package nl.sogeti.reactivespring.ticketshop;

import com.google.common.io.Resources;
import nl.sogeti.reactivespring.model.TicketRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TicketRequestFactory {
    private static final String NAMES = "names_cleaned.txt";
    private static final String BANDS = "bands_cleaned.txt";
    List<String> namesList;
    List<String> bandsList;
    int namesSize;
    int bandsSize;
    Random rand = new Random();

    private TicketRequestFactory(List<String> namesList, List<String> bandsList) {
        this.namesList = namesList;
        this.bandsList = bandsList;
        this.namesSize = namesList.size();
        this.bandsSize = bandsList.size();
    }

    public static TicketRequestFactory getInstance() {
        List<String> namesList = retrieveItems(NAMES);
        List<String> bandsList = retrieveItems(BANDS);
        return new TicketRequestFactory(namesList, bandsList);
    }

    public String getRandomBand() {
        return bandsList.get(rand.nextInt(bandsSize));
    }

    public String getRandomName() {
        return namesList.get(rand.nextInt(namesSize));
    }

    public int getRandomTicketNumber() {
        return rand.nextInt(10) + 1;
    }

    public  String constructRandomTicketRequestJSON() {
///        {"event":"Rolling Stones","amount":5,"customerName":"Kees"}
        return String.format("{\"band\": \"%s\", \"amount\": %s,\"customerName\":\"%s\"}",
                getRandomBand(), getRandomTicketNumber(), getRandomName());
    }

    public TicketRequest constructRandomTicketRequest() {
///        {"event":"Rolling Stones","amount":5,"customerName":"Kees"}
        return new TicketRequest(getRandomBand(), getRandomName(), getRandomTicketNumber());
    }

    public List<String> getNamesList() {
        return namesList;
    }

    public List<String> getBandsList() {
        return bandsList;
    }


    private static List<String> retrieveItems(String file) {
        List<String> namesList = new ArrayList<>();
        try {
            namesList = Files.lines(Paths.get(Resources.getResource(file).toURI()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return namesList;
    }
}

