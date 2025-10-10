package mk.reklama8.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String price;
    private String location;
    private String category;
    private String image;
    private String link;
    private String time;
    private String source;

    public Listing(Long id, String title, String price, String location, String category, String image, String link, String time, String source) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.location = location;
        this.category = category;
        this.image = image;
        this.link = link;
        this.time = time;
        this.source = source;
    }

    public Listing() {

    }
}