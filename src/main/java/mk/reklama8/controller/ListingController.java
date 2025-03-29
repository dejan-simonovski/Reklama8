package mk.reklama8.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mk.reklama8.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/listings")
class ListingController {
    @Autowired
    private ListingService listingService;

    @GetMapping
    public ResponseEntity<JsonNode> getAllListings() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(listingService.fetchRawJson());
            return ResponseEntity.ok(jsonNode);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ObjectMapper().createObjectNode().put("error", "Could not parse JSON"));
        }
    }
}