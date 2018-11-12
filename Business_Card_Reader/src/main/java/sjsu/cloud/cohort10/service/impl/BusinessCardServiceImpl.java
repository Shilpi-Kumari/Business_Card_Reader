package sjsu.cloud.cohort10.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import com.amazonaws.services.s3.model.PutObjectRequest;

import sjsu.cloud.cohort10.dao.BusinessCardDAO;
import sjsu.cloud.cohort10.dto.BusinessCardOutput;
import sjsu.cloud.cohort10.dto.GenericFileResponse;
import sjsu.cloud.cohort10.dto.UserLoginRequest;
import sjsu.cloud.cohort10.dto.UserSignInRequest;
import sjsu.cloud.cohort10.helper.AWSDetectTextRekognitionHelper;
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
    BusinessCardDAO businessCardDAO;
    
    @Async
    public GenericFileResponse uploadBusinessCardToS3(MultipartFile multipartFile, String emailId, 
    		String fileName, String fileDescription, boolean enablePublicReadAccess) throws ValidationException
    {
        String uploadFileName = multipartFile.getOriginalFilename();
        
        GenericFileResponse response = null;

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
            BusinessCardOutput businessCardOutput = rekognitionHelper.detectText(this.awsS3AudioBucket, key);
            //set the file name and file description values
            businessCardOutput.setFileName(fileName);
            businessCardOutput.setFileDescription(fileDescription);
            
            //call the private method to set the business card values to DB
            //response = insertAndUpdateCardDetails(businessCardOutput);
            
            file.delete();
            
        } catch (IOException | AmazonServiceException ex) {
            logger.error("error [" + ex.getMessage() + "] occurred while uploading [" + uploadFileName + "] ");
        }
		return response;
    }
    
   /* private GenericFileResponse insertAndUpdateCardDetails(BusinessCardOutput businessCardOutput) {
    	
    	boolean fileMatch = false;
    	List<UserFilesDTO> userFiles = userDAO.getUserFiles(emailId);
    	
    	if (!CollectionUtils.isNullOrEmpty(userFiles)) {
    		
    		for(UserFilesDTO userFilesDTO : userFiles)
        	{
        		fileMatch = userFilesDTO.getFileName().equalsIgnoreCase(fileName);
        	}
    		if (fileMatch) {
    			//update the files table with new data
    			userDAO.updateUserFile(emailId, fileName, fileDescription);
    		}
    		else {
    			//insert the new file data for the existing user 
    			userDAO.insertUserFile(firstName, lastName, emailId, fileName, fileDescription);
    		}
    	}
    	else{
    		//insert query goes here to for files and new user
    		userDAO.insertUserFile(firstName, lastName, emailId, fileName, fileDescription);
    	}
    	
		return new GenericFileResponse("SUCCESS");
    	
    }*/
    
    @Override
	public Map<String, String> newUserSignInRequest(UserSignInRequest userRequest) {
		
    	Map<String, String> outputMap = businessCardDAO.createUser(userRequest);
		return outputMap;
	}
    
    @Override
	public Map<String,String> userLogin(UserLoginRequest userLoginRequest){
    	
    	Map<String, String> outputMap = businessCardDAO.getUserDetails(userLoginRequest);
		return outputMap;
	}
    
}