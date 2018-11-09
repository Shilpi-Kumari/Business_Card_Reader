package sjsu.cloud.cohort10.service;

import java.util.List;

import javax.xml.bind.ValidationException;

import org.springframework.web.multipart.MultipartFile;

import sjsu.cloud.cohort10.dto.GenericFileResponse;

public interface BusinessCardService
{
    GenericFileResponse uploadBusinessCardToS3(MultipartFile multipartFile, String firstName, String lastName, String emailId, 
    		String fileName, String fileDescription, boolean enablePublicReadAccess) throws ValidationException ;
    
}