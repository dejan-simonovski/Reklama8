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
                            background-color: #f9f9f9;
                            padding: 20px;
                            color: #333;
                        }
                        .container {
                            background-color: #fff;
                            border-radius: 8px;
                            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
                            padding: 20px;
                            max-width: 600px;
                            margin: auto;
                        }
                        h2 {
                            color: #007BFF;
                        }
                        p {
                            color: #FFFFFF;
                        }
                        .listing {
                            margin-top: 15px;
                            padding: 10px;
                            border-top: 1px solid #eee;
                        }
                        a {
                            color: #2196F3;
                            text-decoration: none;
                        }
                        a:hover {
                            text-decoration: underline;
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
                            <p><a href="%s">View Listing</a></p>
                        </div>
                        <p>Cheers! 👋</p>
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
