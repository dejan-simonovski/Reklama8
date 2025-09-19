package mk.reklama8.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mk.reklama8.model.Listing;
import mk.reklama8.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ListingService {
    @Autowired
    private ListingRepository repository;

    public void saveListings(List<Listing> listings) {
        repository.saveAll(listings);
    }

    @Scheduled(fixedRate = 14400000) // Every 4 hours 14400000
    public void fetchAndSaveListings() {
        try {
            Process process = new ProcessBuilder("python", "crawler/listings.py").start();
            process.waitFor();
            List<Listing> listings = parseListings();
            saveListings(listings);
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("Error parsing listings");
        }
    }

    private List<Listing> parseListings() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("crawler/listings.json")), StandardCharsets.UTF_8);
        return new ObjectMapper().readValue(content, new TypeReference<>() {});
    }

    public String fetchRawJson() {
        try {
//            System.out.println("Current Working Directory: " + new java.io.File(".").getAbsolutePath());
            byte[] jsonData = Files.readAllBytes(Paths.get("crawler/listings.json"));

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            return objectMapper.writeValueAsString(jsonNode);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"Could not read JSON file\"}";
        }
    }

}