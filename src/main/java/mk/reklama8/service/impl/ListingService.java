package mk.reklama8.service.impl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mk.reklama8.model.Listing;
import mk.reklama8.model.NotificationSubscription;
import mk.reklama8.repository.ListingRepository;
import mk.reklama8.repository.NotificationRepository;
import mk.reklama8.service.IListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListingService implements IListingService {
    @Autowired
    private ListingRepository repository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private EmailService emailService;

//    public void saveListings(List<Listing> listings) {
//        repository.saveAll(listings);
//    }

    @Scheduled(fixedDelay = 14400000) // Every 4 hours 14400000
    public void fetchAndSaveListings() {
        try {
            Process process = new ProcessBuilder("python", "listings.py")
                    .directory(new File("crawler"))
                    .start();
            process.waitFor();
            List<Listing> listings = parseListings();
            //saveListings(listings);

            processNotifications(listings);
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("Error parsing listings");
        }
    }

    public List<Listing> parseListings() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("crawler/listings.json")), StandardCharsets.UTF_8);
        return new ObjectMapper().readValue(content, new TypeReference<>() {});
    }

    public String fetchListingsJson() {

        if (!Files.exists(Paths.get("crawler/listings.json"))) {
            System.out.println("listings.json not found, fetching...");
            fetchAndSaveListings();
        }

        try {
            byte[] jsonData = Files.readAllBytes(Paths.get("crawler/listings.json"));

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            return objectMapper.writeValueAsString(jsonNode);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"Could not read JSON file\"}";
        }
    }

    @Override
    public String fetchLocations() {
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get("locations/locations.json"));

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            return objectMapper.writeValueAsString(jsonNode);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"Could not read locations.json\"}";
        }
    }

    private void processNotifications(List<Listing> listings) {
        List<NotificationSubscription> subscriptions = notificationRepository.findAll();

        for (NotificationSubscription sub : subscriptions) {
            String search = sub.getSearchQuery();
            String location = sub.getLocation();

            if (search == null || search.isBlank())
                continue;

            boolean matched = listings.stream().anyMatch(listing -> {
                boolean matchesSearch = listing.getTitle() != null &&
                        listing.getTitle().toLowerCase().contains(search.toLowerCase());

                boolean matchesLocation = location == null || location.isBlank() ||
                        (listing.getLocation() != null &&
                                listing.getLocation().toLowerCase().contains(location.toLowerCase()));

                return matchesSearch && matchesLocation;
            });

            boolean expired = sub.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30));

            if (matched) {
                listings.stream()
                        .filter(listing -> {
                            boolean matchesSearch = listing.getTitle() != null &&
                                    listing.getTitle().toLowerCase().contains(search.toLowerCase());

                            boolean matchesLocation = location == null || location.isBlank() ||
                                    (listing.getLocation() != null &&
                                            listing.getLocation().toLowerCase().contains(location.toLowerCase()));

                            return matchesSearch && matchesLocation;
                        })
                        .forEach(listing -> emailService.sendEmail(sub.getUserId(), listing));
            }

            if (matched || expired) {
                notificationRepository.delete(sub);
            }
        }
    }

}