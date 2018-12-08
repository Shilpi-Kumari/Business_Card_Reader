package sjsu.cloud.cohort10.service;

import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationException;

import org.springframework.web.multipart.MultipartFile;

import sjsu.cloud.cohort10.dto.BusinessCardOutput;
import sjsu.cloud.cohort10.dto.UserLoginRequest;
import sjsu.cloud.cohort10.dto.UserSignInRequest;

public interface BusinessCardService
{
	 Map<String, String> uploadBusinessCardToS3(MultipartFile multipartFile,String emailId, 
    		String fileName, String fileDescription, boolean enablePublicReadAccess) throws ValidationException ;
    
    Map<String, String> newUserSignInRequest(UserSignInRequest userRequest);
    
    Map<String,String> userLogin(UserLoginRequest userLoginRequest) throws ValidationException;
    
    List<BusinessCardOutput> getUserFiles(String emailId);
    
    Map<String, String> updateFileDescription(String emailId, 
    		String fileName, String fileDescription);
    		
    Map<String, String> deleteFileAndUpdateDB(Integer id);
    
    List<BusinessCardOutput> searchBusinessCard(String userEmailId, Integer searchType, String searchInput);
    
    Map<String, String> businessCardReferral(String toEmail, String firstName, String lastName, String contactName,
				String contactEmailId, String organization, String contactNumber);
    
    Map<String, String> getBusinessCardUrls();
    
}