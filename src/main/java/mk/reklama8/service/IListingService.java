package mk.reklama8.service;
import mk.reklama8.model.Listing;

import java.io.IOException;
import java.util.List;

public interface IListingService {
    public void fetchAndSaveListings() ;
    public List<Listing> parseListings() throws IOException;
    public String fetchListingsJson();
    public String fetchLocations();
}
