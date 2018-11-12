package sjsu.cloud.cohort10.controller;

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

import sjsu.cloud.cohort10.dto.GenericFileResponse;
import sjsu.cloud.cohort10.dto.UserLoginRequest;
import sjsu.cloud.cohort10.dto.UserSignInRequest;
import sjsu.cloud.cohort10.service.BusinessCardService;

@RestController
public class BusinessCardController {

    @Autowired
    private BusinessCardService businessCardService;

    //upload business card image
    @RequestMapping(value = "/uploadBusinessCardToS3", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<GenericFileResponse> uploadBusinessCardToS3(
			@RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "emailId", required = true) String emailId,
			@RequestParam(value = "fileName", required = true) String fileName,
			@RequestParam(value = "fileDescription", required = true) String fileDescription) {

		ResponseEntity<GenericFileResponse> responseEntity = null;
		try {
			GenericFileResponse response = this.businessCardService.uploadBusinessCardToS3(file, emailId, fileName, fileDescription, true);
			responseEntity = new ResponseEntity<GenericFileResponse>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			responseEntity = new ResponseEntity<GenericFileResponse>(new GenericFileResponse(e.getMessage()),
					HttpStatus.EXPECTATION_FAILED);
		}

		return responseEntity;
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
    
}