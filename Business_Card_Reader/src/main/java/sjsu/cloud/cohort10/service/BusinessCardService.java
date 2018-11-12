package sjsu.cloud.cohort10.service;

import java.util.Map;

import javax.xml.bind.ValidationException;

import org.springframework.web.multipart.MultipartFile;

import sjsu.cloud.cohort10.dto.GenericFileResponse;
import sjsu.cloud.cohort10.dto.UserLoginRequest;
import sjsu.cloud.cohort10.dto.UserSignInRequest;

public interface BusinessCardService
{
    GenericFileResponse uploadBusinessCardToS3(MultipartFile multipartFile,String emailId, 
    		String fileName, String fileDescription, boolean enablePublicReadAccess) throws ValidationException ;
    
    Map<String, String> newUserSignInRequest(UserSignInRequest userRequest);
    
    Map<String,String> userLogin(UserLoginRequest userLoginRequest) throws ValidationException;
    
}