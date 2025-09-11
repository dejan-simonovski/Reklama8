package mk.reklama8.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import mk.reklama8.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/listings")
class ListingController {
    @Autowired
    private ListingService listingService;

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
            @RequestParam(required = false) String search
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(listingService.fetchRawJson());

            ArrayNode filtered = objectMapper.createArrayNode();
            if (root.isArray()) {
                for (JsonNode item : root) {
                    boolean matchesSearch = (search == null) ||
                            (item.has("title") && item.get("title").asText().toLowerCase().contains(search.toLowerCase()));

                    if (matchesSearch) {
                        filtered.add(item);
                    }
                }
            }

            var response = objectMapper.createObjectNode();
            response.set("data", filtered);
            response.put("total", filtered.size());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ObjectMapper().createObjectNode().put("error", "Could not parse JSON"));
        }
    }
}