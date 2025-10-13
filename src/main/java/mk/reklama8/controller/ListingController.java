package mk.reklama8.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import mk.reklama8.model.User;
import mk.reklama8.repository.UserRepository;
import mk.reklama8.service.impl.ListingService;
import mk.reklama8.service.impl.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/listings")
class ListingController {
    @Autowired
    private ListingService listingService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserRepository userRepository;

//    @GetMapping
////    @CrossOrigin(origins = "http://localhost:4200")
//    public ResponseEntity<JsonNode> getAllListings() {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(listingService.fetchRawJson());
//            return ResponseEntity.ok(jsonNode);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body(new ObjectMapper().createObjectNode().put("error", "Could not parse JSON"));
//        }
//    }

    @GetMapping
    public ResponseEntity<JsonNode> getListings(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(listingService.fetchListingsJson());

            ArrayNode filtered = objectMapper.createArrayNode();
            if (root.isArray()) {
                for (JsonNode item : root) {
                    boolean matchesSearch = (search == null) ||
                            (item.has("title") && item.get("title").asText().toLowerCase().contains(search.toLowerCase()));

                    boolean matchesCity = (location == null || location.isBlank()) ||
                            (item.has("location") && item.get("location").asText().toLowerCase().contains(location.toLowerCase()));

                    if (matchesSearch && matchesCity) {
                        filtered.add(item);
                    }
                }
            }

            int total = filtered.size();

            int fromIndex = Math.max(0, (page - 1) * pageSize);
            int toIndex = Math.min(fromIndex + pageSize, total);

            ArrayNode paginated = objectMapper.createArrayNode();
            for (int i = fromIndex; i < toIndex; i++) {
                paginated.add(filtered.get(i));
            }

            var response = objectMapper.createObjectNode();
            response.set("data", paginated);
            response.put("total", total);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ObjectMapper().createObjectNode().put("error", "Could not parse JSON"));
        }
    }

    @PostMapping("/notify")
    public ResponseEntity<String> notifyMe(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized: Please log in");
        }

        User user = userRepository.findByUsername(userDetails.getUsername());
        notificationService.subscribe(user, search, location);
        return ResponseEntity.ok("Notification subscribed successfully. You will be notified if a similar listing appears within 30 days. ");
    }

    @GetMapping("/locations")
    public ResponseEntity<JsonNode> getCities() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String citiesJson = listingService.fetchLocations();

            JsonNode cities = objectMapper.readTree(citiesJson);
            return ResponseEntity.ok(cities);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ObjectMapper().createObjectNode().put("error", "Could not load locations.json"));
        }
    }
}