package sjsu.cloud.cohort10.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

import sjsu.cloud.cohort10.dto.BusinessCardOutput;
import sjsu.cloud.cohort10.dto.GenericFileResponse;
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
    
    @Async
    public GenericFileResponse uploadBusinessCardToS3(MultipartFile multipartFile, String firstName, String lastName, String emailId, 
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
            
            file.delete();
            
        } catch (IOException | AmazonServiceException ex) {
            logger.error("error [" + ex.getMessage() + "] occurred while uploading [" + uploadFileName + "] ");
        }
		return response;
    }
    
}