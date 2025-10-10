package mk.reklama8.service.impl;

import mk.reklama8.model.NotificationSubscription;
import mk.reklama8.model.User;
import mk.reklama8.repository.NotificationRepository;
import mk.reklama8.service.INotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationService implements INotificationService {
    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    public NotificationSubscription subscribe(String user, String searchQuery, String location) {
        NotificationSubscription sub = new NotificationSubscription();
        sub.setUserId(user);
        sub.setSearchQuery(searchQuery);
        sub.setLocation(location);
        return repo.save(sub);
    }
}
