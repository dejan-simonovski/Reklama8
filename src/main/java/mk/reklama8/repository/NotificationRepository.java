package mk.reklama8.repository;

import mk.reklama8.model.NotificationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationSubscription, Long> {}
