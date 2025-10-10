package mk.reklama8.service;

import mk.reklama8.model.NotificationSubscription;
import mk.reklama8.model.User;

public interface INotificationService {
    public NotificationSubscription subscribe(String user, String searchQuery, String location);
}
