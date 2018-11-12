
package sjsu.cloud.cohort10.dao;

import java.util.Map;

import sjsu.cloud.cohort10.dto.UserDetailsDTO;
import sjsu.cloud.cohort10.dto.UserLoginRequest;
import sjsu.cloud.cohort10.dto.UserSignInRequest;

public interface BusinessCardDAO {
	
	Map<String, String> createUser(UserSignInRequest userRequest);
	
	Map<String, String> getUserDetails(UserLoginRequest userLoginRequest);
	
}
