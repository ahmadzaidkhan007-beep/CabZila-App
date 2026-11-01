package tech.codehunt.service;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import tech.codehunt.model.ServiceForm;

public interface ServiceFormService  {
	
	public ServiceForm addService(ServiceForm serviceForm, MultipartFile multipartFile) throws Exception;
	
	public List<ServiceForm> readAllServices();

}
