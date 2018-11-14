package sjsu.cloud.cohort10.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehend.model.DetectEntitiesResult;
import com.amazonaws.services.comprehend.model.Entity;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sjsu.cloud.cohort10.dto.AnalyzeRekognitionOutput;
import sjsu.cloud.cohort10.dto.BusinessCardInput;


@Component
public class AWSDetectTextRekognitionHelper {
	
	public BusinessCardInput detectText (String bucketName, String filePath) throws JsonProcessingException {
	
      AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

      DetectTextRequest request = new DetectTextRequest()
              .withImage(new Image()
              .withS3Object(new S3Object()
              .withName(filePath)
              .withBucket(bucketName)));
    
      Integer contactNbrParentId = 0;
      Integer i=0;
      BusinessCardInput businessCardInput = new BusinessCardInput();

      try {
         DetectTextResult result = rekognitionClient.detectText(request);
         List<TextDetection> textDetections = result.getTextDetections();
         List<AnalyzeRekognitionOutput> rekognitionOutputList = new ArrayList<AnalyzeRekognitionOutput>();
       
         for (TextDetection text: textDetections) {
        	 
        	 //logic to get the type "line" into object to send it to AWS comprehend to analyze the output
        	 if (text.getType().equalsIgnoreCase("LINE"))
        	 {
        		 AnalyzeRekognitionOutput rekognitionOutput = new AnalyzeRekognitionOutput();
        		 
        		 rekognitionOutput.setDetectedText(text.getDetectedText());
        		 rekognitionOutputList.add(rekognitionOutput);
        		 
        		 //logic to find the numbers in a string value
        		 String currentDetectedText = text.getDetectedText();
        		 int count = 0;
        		 for (int j = 0, len = currentDetectedText.length(); j < len; j++) {
        		     if (Character.isDigit(currentDetectedText.charAt(j))) {
        		         count++;
        		     }
        		 }
        		 
        		//logic to get the contact number from the aws rekognition output
        		 if (text.getDetectedText().contains("direct") || text.getDetectedText().contains("Phone")
        				 || text.getDetectedText().contains("Mob") || count >= 10)
        		 {
        			 contactNbrParentId = text.getId();
        		 }
        	 }
        	 
        	 //logic to get the email address from the aws rekognition output
        	 if (text.getType().equalsIgnoreCase("WORD"))
        	 {
        		 if (text.getDetectedText().contains("@") && text.getDetectedText().contains("."))
        		 {
        			 businessCardInput.setContactEmailId(text.getDetectedText());
        		 }
        		 
        		 //logic to get various formats of phone numbers in business cards
        		 if ((text.getDetectedText().contains(".") || 
        				text.getDetectedText().contains("(") ||
        				text.getDetectedText().contains("-") ||
        				text.getDetectedText().contains(" ")) && text.getParentId() == contactNbrParentId)
        		 {
        			 if (i==2) {
        				 String tempContactNumber = businessCardInput.getContactNumber();
        				 businessCardInput.setContactNumber(tempContactNumber + text.getDetectedText());
        				 i++;
        			 }
        			 if (i==1) {
        				 String tempContactNumber = businessCardInput.getContactNumber();
        				 businessCardInput.setContactNumber(tempContactNumber + text.getDetectedText());
        				 i++;
        			 }
        			 if (i == 0) {
        				 businessCardInput.setContactNumber(text.getDetectedText());
        				 i++;
        			 }
        		 }
        	 }
         }
         
         //logic to convert the rekognition output list into JSON
         ObjectMapper mapper = new ObjectMapper();
         String output = mapper.writeValueAsString(rekognitionOutputList);
         
         //logic to call the AWS Comprehend API
         analyzeDetectedText(output, businessCardInput);
         
      } catch(AmazonRekognitionException e) {
         e.printStackTrace();
      }
	return businessCardInput;
}
	
	//method to analyze the card information using AWS comprehend
	public BusinessCardInput analyzeDetectedText (String analyzeInput, BusinessCardInput businessCardInput) {
		
		
		
		AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
		 
        AmazonComprehend comprehendClient =
            AmazonComprehendClientBuilder.standard()
                                         .withCredentials(awsCreds)
                                         .withRegion("us-west-2")
                                         .build();
        
     // Call detectEntities API
        DetectEntitiesRequest detectEntitiesRequest = new DetectEntitiesRequest().withText(analyzeInput)
                                                                                 .withLanguageCode("en");
        DetectEntitiesResult detectEntitiesResult  = comprehendClient.detectEntities(detectEntitiesRequest);
       // detectEntitiesResult.getEntities().forEach(System.out::println);
        for (Entity entityValues : detectEntitiesResult.getEntities())
        {
        	if (entityValues.getType().equalsIgnoreCase("PERSON"))
        	{
        		if (!(entityValues.getText().contains("@")))
        		{
        			businessCardInput.setContactName(entityValues.getText());
        		}
        	}
        	if (entityValues.getType().equalsIgnoreCase("ORGANIZATION"))
			{
        		businessCardInput.setOrganization(entityValues.getText());
			}
        }
		return businessCardInput;
	}

}