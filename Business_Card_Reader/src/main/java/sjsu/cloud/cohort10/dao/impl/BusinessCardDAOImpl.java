
package sjsu.cloud.cohort10.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import sjsu.cloud.cohort10.dao.BusinessCardDAO;
import sjsu.cloud.cohort10.dto.UserDetailsDTO;
import sjsu.cloud.cohort10.dto.UserLoginRequest;
import sjsu.cloud.cohort10.dto.UserSignInRequest;

@Repository("mysql")
public class BusinessCardDAOImpl implements BusinessCardDAO{
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Map<String, String> createUser(UserSignInRequest userRequest){
		
		HashMap<String, String> outputMap = new HashMap<>();
		
		try {
			String sql = "INSERT INTO CUSTOMER_INFO (FirstName, LastName, EmailId, Password, SecurityQ1, SecurityQ2, SecurityQ3) VALUES (?, ?, ?, ?, ?, ?, ?)";
			
			jdbcTemplate.update(sql, userRequest.getFirstName(), userRequest.getLastName(), 
					userRequest.getEmailId(), userRequest.getPassword(), userRequest.getSecurityQ1(), userRequest.getSecurityQ2(), userRequest.getSecurityQ3());
			
			outputMap.put("status", "true");
			
		}catch (Exception e)
		{
			outputMap.put("status", "false");
		}
		return outputMap;
	}
	
	@Override
	public Map<String, String> getUserDetails(UserLoginRequest userLoginRequest){
		
		HashMap<String, String> outputMap = new HashMap<>();
		
		try {
			String sql = "SELECT * FROM CUSTOMER_INFO WHERE EmailId = ? AND Password = ?";
			
			UserDetailsDTO userDetailsDTO = (UserDetailsDTO) jdbcTemplate.queryForObject(
					sql, new Object[] { userLoginRequest.getEmailId(), userLoginRequest.getPassword() }, 
					new BeanPropertyRowMapper(UserDetailsDTO.class));
			
			outputMap.put("status", "true");
			outputMap.put("firstname", userDetailsDTO.getFirstName());
			outputMap.put("lastname", userDetailsDTO.getLastName());
			outputMap .put("emailid", userDetailsDTO.getEmailId());
		}catch (Exception e)
		{
			outputMap.put("status", "false");
		}
			
		return outputMap;
	}

}
