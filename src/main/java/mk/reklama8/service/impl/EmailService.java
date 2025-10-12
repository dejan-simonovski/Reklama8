package mk.reklama8.service.impl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import mk.reklama8.model.Listing;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String toEmail, Listing listing) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("New Listing Matches Your Criteria!");

        String htmlContent = """
            <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f5f5f5;
                        padding: 20px;
                        color: #333333;
                    }
                    .container {
                        background-color: #ffffff;
                        border-radius: 8px;
                        box-shadow: 0 2px 6px rgba(0,0,0,0.1);
                        padding: 20px;
                        max-width: 600px;
                        margin: auto;
                    }
                    h2 {
                        color: #007BFF;
                        margin-bottom: 10px;
                    }
                    p {
                        color: #444444;
                        line-height: 1.5;
                    }
                    .listing {
                        margin-top: 15px;
                        padding: 10px;
                        border-top: 1px solid #eee;
                        background-color: #f9f9f9;
                        border-radius: 6px;
                    }
                    .listing p {
                        margin: 5px 0;
                    }
                    a {
                        color: #007BFF;
                        text-decoration: none;
                        font-weight: bold;
                    }
                    a:hover {
                        text-decoration: underline;
                    }
                    .footer {
                        margin-top: 20px;
                        font-size: 0.9em;
                        color: #777777;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Hi!</h2>
                    <p>A new listing matches your criteria:</p>
                    <div class="listing">
                        <p><strong>Title:</strong> %s</p>
                        <p><strong>Price:</strong> %s</p>
                        <p><strong>Location:</strong> %s</p>
                        <p><strong>Category:</strong> %s</p>
                        <p><a href="%s" target="_blank">View Listing</a></p>
                    </div>
                    <p class="footer">Cheers! 👋<br>— The Reklama8 Team</p>
                </div>
            </body>
            </html>
            """.formatted(
                listing.getTitle(),
                listing.getPrice(),
                listing.getLocation(),
                listing.getCategory(),
                listing.getLink()
        );


        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
