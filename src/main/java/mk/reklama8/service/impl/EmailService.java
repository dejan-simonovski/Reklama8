package mk.reklama8.service.impl;
import mk.reklama8.model.Listing;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String toEmail, Listing listing) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("New listing matches your subscription!");
        message.setText(
                "Hi!\n\nA new listing matches your criteria:\n\n" +
                        "Title: " + listing.getTitle() + "\n" +
                        "Price: " + listing.getPrice() + "\n" +
                        "Location: " + listing.getLocation() + "\n" +
                        "Category: " + listing.getCategory() + "\n" +
                        "Link: " + listing.getLink() + "\n\n" +
                        "Cheers!"
        );
        mailSender.send(message);
    }
}
