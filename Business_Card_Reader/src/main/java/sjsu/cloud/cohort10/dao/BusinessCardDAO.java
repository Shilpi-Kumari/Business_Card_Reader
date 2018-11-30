
package sjsu.cloud.cohort10.dao;

import java.util.List;
import java.util.Map;

import sjsu.cloud.cohort10.dto.BusinessCardInput;
import sjsu.cloud.cohort10.dto.BusinessCardOutput;
import sjsu.cloud.cohort10.dto.UserLoginRequest;
import sjsu.cloud.cohort10.dto.UserSignInRequest;

public interface BusinessCardDAO {
	
	Map<String, String> createUser(UserSignInRequest userRequest);
	
	Map<String, String> getUserDetails(UserLoginRequest userLoginRequest);
	
	Map<String, String> insertCardDetails(BusinessCardInput businessCardInput);
	
	List<BusinessCardOutput> getUserFiles(String emailId);
	
	Map<String, String> updateFileDescription(String emailId, String fileName, String fileDescription);
	
	BusinessCardOutput getFileDetailsBasedOnId(Integer id);
	
	Map<String, String> deleteCard(Integer id);
	
}
