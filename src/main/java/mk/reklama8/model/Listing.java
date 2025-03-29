package mk.reklama8.model;

import jakarta.persistence.*;

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

    public Listing(Long id, String title, String price, String location, String category, String image, String link) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.location = location;
        this.category = category;
        this.image = image;
        this.link = link;
    }

    public Listing() {

    }
}