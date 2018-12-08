package sjsu.cloud.cohort10.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import sjsu.cloud.cohort10.dao.BusinessCardDAO;
import sjsu.cloud.cohort10.dto.BusinessCardInput;
import sjsu.cloud.cohort10.dto.BusinessCardOutput;
import sjsu.cloud.cohort10.dto.UserLoginRequest;
import sjsu.cloud.cohort10.dto.UserSignInRequest;
import sjsu.cloud.cohort10.helper.AWSDetectTextRekognitionHelper;
import sjsu.cloud.cohort10.helper.AWSSimpleEmailServiceHelper;
import sjsu.cloud.cohort10.service.BusinessCardService;

@Component
public class BusinessCardServiceImpl implements BusinessCardService
{
    private String awsS3AudioBucket;
    private AmazonS3 amazonS3;
    private static final Logger logger = LoggerFactory.getLogger(BusinessCardServiceImpl.class);

    @Autowired
    public BusinessCardServiceImpl(Region awsRegion, AWSCredentialsProvider awsCredentialsProvider, String awsS3AudioBucket)
    {
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(awsRegion.getName()).build();
        this.awsS3AudioBucket = awsS3AudioBucket;
    }
    
    @Autowired
    AWSDetectTextRekognitionHelper rekognitionHelper;
    
    @Autowired
    AWSSimpleEmailServiceHelper simpleEmailService;
    
    @Autowired
    BusinessCardDAO businessCardDAO;
    
    @Async
    public Map<String, String> uploadBusinessCardToS3(MultipartFile multipartFile, String emailId, 
    		String fileName, String fileDescription, boolean enablePublicReadAccess) throws ValidationException
    {
        String uploadFileName = multipartFile.getOriginalFilename();
        
        Map<String, String> outputMap = new HashMap<>();

        try {
            File file = new File(uploadFileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
            
            String key = emailId + "/" + uploadFileName;

            PutObjectRequest putObjectRequest = new PutObjectRequest(this.awsS3AudioBucket, key, file);

            if (enablePublicReadAccess) {
                putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            }
            this.amazonS3.putObject(putObjectRequest);
            
            //call helper class to detect text
            BusinessCardInput businessCardInput = rekognitionHelper.detectText(this.awsS3AudioBucket, key);
            //set the file name and file description values
            businessCardInput.setFileName(fileName);
            businessCardInput.setFileDescription(fileDescription);
            businessCardInput.setEmailId(emailId);
            
            //call the private method to set the business card values to DB
            outputMap = insertCardDetails(businessCardInput);
            
            file.delete();
            
        } catch (IOException | AmazonServiceException ex) {
            logger.error("error [" + ex.getMessage() + "] occurred while uploading [" + uploadFileName + "] ");
        }
		return outputMap;
    }
    
    private Map<String, String> insertCardDetails(BusinessCardInput businessCardInput) {
    	
    	//logic to insert the card details into DB
    	Map<String, String> outputMap = businessCardDAO.insertCardDetails(businessCardInput);
		return outputMap;
    	
    }
    
    @Override
	public Map<String, String> newUserSignInRequest(UserSignInRequest userRequest) {
		
    	//logic to create user in DB
    	Map<String, String> outputMap = businessCardDAO.createUser(userRequest);
		return outputMap;
	}
    
    @Override
	public Map<String,String> userLogin(UserLoginRequest userLoginRequest){
    	
    	//logic to get the user details to verify login information
    	Map<String, String> outputMap = businessCardDAO.getUserDetails(userLoginRequest);
		return outputMap;
	}

	@Override
	public List<BusinessCardOutput> getUserFiles(String emailId) {
		List<BusinessCardOutput> businessCardList = businessCardDAO.getUserFiles(emailId);
		return businessCardList;
	}

	@Override
	public Map<String, String> updateFileDescription(String emailId, String fileName, String fileDescription) {
		Map<String, String> outputMap = businessCardDAO.updateFileDescription(emailId, fileName, fileDescription);
		return outputMap;
	}

	@Override
	public Map<String, String> deleteFileAndUpdateDB(Integer id) {
		
		//DB call to get the file name and users email id to delete the details from S3
		BusinessCardOutput businessCardDetails = businessCardDAO.getFileDetailsBasedOnId(id);
		
		
		String filePath = businessCardDetails.getEmailId() + "/" + businessCardDetails.getFileName();
            amazonS3.deleteObject(new DeleteObjectRequest(awsS3AudioBucket, filePath));
            
            Map<String, String> outputMap = businessCardDAO.deleteCard(id);
            return outputMap;
	}

	@Override
	public List<BusinessCardOutput> searchBusinessCard(String userEmailId, Integer searchType, String searchInput) {
		List<BusinessCardOutput> businessCardList = businessCardDAO.searchBusinessCard(userEmailId, searchType,
				searchInput);
		return businessCardList;
	}

	@Override
	public Map<String, String> businessCardReferral(String toEmail, String firstName, String lastName, String contactName,
			String contactEmailId, String organization, String contactNumber) {
		
		Map<String, String> outputMap = simpleEmailService.businessCardReferral(toEmail, firstName, lastName, contactName,
					contactEmailId, organization, contactNumber);
		return outputMap;
	}
    
}