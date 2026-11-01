package tech.codehunt.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.codehunt.dao.ContactFormCrud;
import tech.codehunt.model.ContactForm;

@Service
public class ContactFormServiceImpl implements ContactFormService {
	
	private ContactFormCrud contactFormCrud;
	
    @Autowired
	public void setContactFormCrud(ContactFormCrud contactFormCrud) {
		this.contactFormCrud = contactFormCrud;
	}

	@Override
	public ContactForm saveContactFormService(ContactForm contactForm) {
		return contactFormCrud.save(contactForm);
	}

	@Override
	public List<ContactForm> readAllContactsService() {
		return contactFormCrud.findAll();
	}

	@Override
	public void deleteContactService(int id) {
		contactFormCrud.deleteById(id);
	}
}
