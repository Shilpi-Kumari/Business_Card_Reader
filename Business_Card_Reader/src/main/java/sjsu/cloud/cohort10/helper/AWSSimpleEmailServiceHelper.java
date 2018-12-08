package sjsu.cloud.cohort10.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailIdentityRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailIdentityResult; 


@Component
public class AWSSimpleEmailServiceHelper {
	
	public Map<String, String> businessCardReferral (String toEmail, String firstName, String lastName, String contactName,
			String contactEmailId, String organization, String contactNumber)  {
		

		HashMap<String, String> outputMap = new HashMap<>();
		
		String FROM = "BusinessCardDirectory@harshit-sjsu.com";

		String TO = toEmail;
		
		// The subject line for the email.
		 String SUBJECT = "BUSINESS CARD DIRECTORY: CARD REFERRAL";
		  
		  // The email body for recipients with non-HTML email clients.
		  String TEXTBODY = " A business card has been referred to you by "+firstName+" "+lastName+" using Business"
		  		+ " Card Directory Application.\r\n\r\n"
		  		+ "Please find the card details below:\r\n"
		  		+ "Name: "+contactName+"\r\n"
		  				+ "Email Id: "+contactEmailId+"\r\n"
		  						+ "Contact: "+contactNumber+"\r\n"
		  								+ "Organization: "+organization+"";
		  
		  try {
		      AmazonSimpleEmailService client = 
		          AmazonSimpleEmailServiceClientBuilder.standard()
		          // Replace US_WEST_2 with the AWS Region you're using for
		          // Amazon SES.
		            .withRegion(Regions.US_EAST_1).build();
		      SendEmailRequest request = new SendEmailRequest()
		          .withDestination(
		              new Destination().withToAddresses(TO))
		          .withMessage(new Message()
		              .withBody(new Body()
		                  .withText(new Content()
		                      .withCharset("UTF-8").withData(TEXTBODY)))
		              .withSubject(new Content()
		                  .withCharset("UTF-8").withData(SUBJECT)))
		          .withSource(FROM);
		      client.sendEmail(request);
		      System.out.println("Email sent!");
		      outputMap.put("status", "true");
		    } catch (Exception ex) {
		      System.out.println("The email was not sent. Error message: " 
		          + ex.getMessage());
		      outputMap.put("status", "false");
		    }
		return outputMap;
	}
}
