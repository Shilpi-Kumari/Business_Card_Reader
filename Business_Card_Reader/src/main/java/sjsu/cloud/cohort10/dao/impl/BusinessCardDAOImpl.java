
package sjsu.cloud.cohort10.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import sjsu.cloud.cohort10.dao.BusinessCardDAO;
import sjsu.cloud.cohort10.dto.BusinessCardInput;
import sjsu.cloud.cohort10.dto.BusinessCardOutput;
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
			String sql = "INSERT INTO CUSTOMER_INFO (FirstName, LastName, EmailId, Password, SecurityQ1, SecurityQ2, SecurityV1, SecurityV2) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			
			jdbcTemplate.update(sql, userRequest.getFirstName(), userRequest.getLastName(), 
					userRequest.getEmailId(), userRequest.getPassword(), userRequest.getSecurityQ1(), 
					userRequest.getSecurityQ2(), userRequest.getSecurityV1(), userRequest.getSecurityV2());
			
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

	@Override
	public Map<String, String> insertCardDetails(BusinessCardInput businessCardInput) {
		
		HashMap<String, String> outputMap = new HashMap<>();
		
		try {
			String sql = "INSERT INTO BUSINESS_CARD_DETAILS (EmailId, FileName, FileDescription, CardContactName,"
					+ " CardContactEmail, CardContactMobile, CardOrganization, CreatedTime) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP())";
			
			jdbcTemplate.update(sql, businessCardInput.getEmailId(), businessCardInput.getFileName(), businessCardInput.getFileDescription(),
					businessCardInput.getContactName(), businessCardInput.getContactEmailId(), businessCardInput.getContactNumber(),
					businessCardInput.getOrganization());
			
			outputMap.put("status", "true");
			
		}catch (Exception e)
		{
			outputMap.put("status", "false");
		}
		return outputMap;
	}

	@Override
	public List<BusinessCardOutput> getUserFiles(String emailId) {
		
		String sql = "SELECT * FROM BUSINESS_CARD_DETAILS where EmailId = ?";
		
		List<BusinessCardOutput> businessCardList = new ArrayList<BusinessCardOutput>();
		
		List<java.util.Map<String, Object>> result = jdbcTemplate.queryForList(sql, emailId);
		
		for(java.util.Map<String, Object> obj : result)
		{
			BusinessCardOutput businessCardDetails = new BusinessCardOutput();
			
			businessCardDetails.setContactEmailId((String)obj.get("CardContactEmail"));
			businessCardDetails.setContactName((String)obj.get("CardContactName"));
			businessCardDetails.setContactNumber((String)obj.get("CardContactMobile"));
			businessCardDetails.setOrganization((String)obj.get("CardOrganization"));
			businessCardDetails.setFileName((String)obj.get("FileName"));
			businessCardDetails.setFileDescription((String)obj.get("FileDescription"));
			businessCardList.add(businessCardDetails);
		}
		
		return businessCardList;
	}

	@Override
	public Map<String, String> updateFileDescription(String emailId, String fileName, String fileDescription) {
		
		HashMap<String, String> outputMap = new HashMap<>();
		
		try {
			String sql = "UPDATE BUSINESS_CARD_DETAILS SET FileDescription = ? "
					+ " WHERE EmailId = ? AND FileName = ?";
			
			jdbcTemplate.update(sql, fileDescription, emailId, fileName);
			
			outputMap.put("status", "true");
			
		}catch (Exception e)
		{
			outputMap.put("status", "false");
		}
		return outputMap;
	}

}
