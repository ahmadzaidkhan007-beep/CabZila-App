package tech.codehunt.service;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.codehunt.dao.AdminDao;
import tech.codehunt.model.Admin;
@Service
public class AdminCredentialsServiceImpl implements AdminCredentialsService {
	
	private AdminDao adminDao;
	private PasswordEncoder passwordEncoder;
	
	@Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Autowired
	public void setAdminDao(AdminDao adminDao) {
		this.adminDao = adminDao;
	}

	@Override
	public String checkAdminCredentials(String oldusername, String oldpassword) {
		// TODO Auto-generated method stub
		
		Optional<Admin> byUsername = adminDao.findByUsername(oldusername);
		if(byUsername.isPresent()) {
			
			Admin admin = byUsername.get();//sn password username
			boolean matches = passwordEncoder.matches(oldpassword, admin.getPassword());
			if(matches) {
				return "SUCCESS";
			}else {
			    return "Wrong Old Credentials";
			}
		}else {
			return "Wrong Old Credentials";
		}
	}

	@Override
	public String updateAdminCredentials(String newusername, String newpassword, String oldusername) {
		// TODO Auto-generated method stub
		int unpdateCredentials = adminDao.unpdateCredentials
				(newusername,passwordEncoder.encode(newpassword) , oldusername);
		
		if(unpdateCredentials==1) {
			return "CREDENTIALS UPDATED SUCCESSFULLY";
		}
		else {
			return "FAILED TO UPDATE";
		}
	}
}
