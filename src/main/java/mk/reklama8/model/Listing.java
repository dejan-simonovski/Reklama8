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
}