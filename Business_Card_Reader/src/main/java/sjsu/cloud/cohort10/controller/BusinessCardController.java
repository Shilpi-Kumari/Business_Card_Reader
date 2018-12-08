package sjsu.cloud.cohort10.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import sjsu.cloud.cohort10.dto.BusinessCardOutput;
import sjsu.cloud.cohort10.dto.UserLoginRequest;
import sjsu.cloud.cohort10.dto.UserSignInRequest;
import sjsu.cloud.cohort10.service.BusinessCardService;

@RestController
public class BusinessCardController {

    @Autowired
    private BusinessCardService businessCardService;

    //upload business card image and store the card information in DB
    @RequestMapping(value = "/uploadBusinessCardToS3", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, String> uploadBusinessCardToS3(
			@RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "emailId", required = true) String emailId,
			@RequestParam(value = "fileName", required = true) String fileName,
			@RequestParam(value = "fileDescription", required = true) String fileDescription) {

    	Map<String, String> responseMap = null;
		try {
			responseMap = this.businessCardService.uploadBusinessCardToS3(file, emailId, fileName, fileDescription, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseMap;
	}
    
    //update file description in DB
    @RequestMapping(value = "/updateFileDescription", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, String> updateFileDescription(
			@RequestParam(value = "emailId", required = true) String emailId,
			@RequestParam(value = "fileName", required = true) String fileName,
			@RequestParam(value = "fileDescription", required = true) String fileDescription) {

    	Map<String, String> responseMap = null;
		try {
			responseMap = this.businessCardService.updateFileDescription(emailId, fileName, fileDescription);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseMap;
	}
    
    //User sign up request mapping
    @RequestMapping(value = "/userSignUp", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, String> userSignUp(@RequestBody UserSignInRequest userSignUpRequest) {
    	Map<String, String> responseMap = null;
		try {
			responseMap = this.businessCardService.newUserSignInRequest(userSignUpRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseMap;
	}
    
    //User login in request mapping
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST, produces = "application/json")
   	@ResponseBody
   	public Map<String, String> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
    	Map<String, String> responseMap = null;
   		try {
   			responseMap = this.businessCardService.userLogin(userLoginRequest);
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		return responseMap;
   	}
    
    //get the list of business card information for the user
    @RequestMapping(value = "/getUserFiles", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<List<BusinessCardOutput>> getUserFiles(@RequestParam String emailId) {
    	ResponseEntity<List<BusinessCardOutput>> responseEntity = null;
		try {
			
			List<BusinessCardOutput> businessCardOutputList = this.businessCardService.getUserFiles(emailId);
			responseEntity = new ResponseEntity<List<BusinessCardOutput>>(businessCardOutputList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			responseEntity = new ResponseEntity<List<BusinessCardOutput>>(HttpStatus.EXPECTATION_FAILED);
		}
		return responseEntity;
	}
    
  //delete file from S3 and DB
    @RequestMapping(value = "/deleteFileAndUpdateDB", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, String> deleteFileAndUpdateDB(@RequestParam Integer id ) {
    	Map<String, String> responseMap = null;
    	try {
   			responseMap = this.businessCardService.deleteFileAndUpdateDB(id);
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		return responseMap;
	}
    
  //get the business card information based on search input
    @RequestMapping(value = "/searchBusinessCard", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<List<BusinessCardOutput>> searchBusinessCard(@RequestParam String userEmailId,
			@RequestParam Integer searchType, @RequestParam String searchInput) {
    	ResponseEntity<List<BusinessCardOutput>> responseEntity = null;
		try {
			
			List<BusinessCardOutput> businessCardOutputList = this.businessCardService.searchBusinessCard(userEmailId,
					searchType, searchInput);
			responseEntity = new ResponseEntity<List<BusinessCardOutput>>(businessCardOutputList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			responseEntity = new ResponseEntity<List<BusinessCardOutput>>(HttpStatus.EXPECTATION_FAILED);
		}
		return responseEntity;
	}
    
    //send email to friend for referral scenario
    @RequestMapping(value = "/businessCardReferral", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, String> businessCardReferral(@RequestParam String referEmailId,
			@RequestParam String firstName, @RequestParam String lastName, @RequestParam String contactName,
			@RequestParam String contactEmailId, @RequestParam String organization, @RequestParam String contactNumber) {
    	Map<String, String> responseMap = null;
    	try {
   			responseMap = this.businessCardService.businessCardReferral(referEmailId, firstName, lastName, contactName,
   					contactEmailId, organization, contactNumber);
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		return responseMap;
	}
    
    //get urls of Cloud front and Cognito Ids 
    @RequestMapping(value = "/getBusinessCardUrls", method = RequestMethod.GET, produces = "application/json")
   	@ResponseBody
   	public Map<String, String> getBusinessCardUrls() {
    	Map<String, String> responseMap = null;
   		try {
   			responseMap = this.businessCardService.getBusinessCardUrls();
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		return responseMap;
   	}
}