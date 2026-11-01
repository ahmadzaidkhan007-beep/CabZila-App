package tech.codehunt.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import tech.codehunt.model.AppStatus;
import tech.codehunt.model.BookingForm;
import tech.codehunt.model.ServiceForm;
import tech.codehunt.model.SiteSettings;
import tech.codehunt.repository.AppStatusRepository;
import tech.codehunt.repository.SiteSettingsRepository;
import tech.codehunt.service.AdminCredentialsService;
import tech.codehunt.service.BookingFormService;
import tech.codehunt.service.ContactFormService;
import tech.codehunt.service.ServiceFormService;

@Controller
@RequestMapping("admin")
public class AdminController {

	private ContactFormService contactFormService;
	private AdminCredentialsService adminCredentialsService;
	private BookingFormService bookingFormService;
	private ServiceFormService serviceFormService;
	private AppStatusRepository appStatusRepository;
	private SiteSettingsRepository siteSettingsRepository;

	// Used only for the accept/decline status update, so your existing service/repository stay untouched
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	public void setSiteSettingsRepository(SiteSettingsRepository siteSettingsRepository) {
		this.siteSettingsRepository = siteSettingsRepository;
	}

	@Autowired
	public void setAppStatusRepository(AppStatusRepository appStatusRepository) {
		this.appStatusRepository = appStatusRepository;
	}

	@Autowired
	public void setServiceFormService(ServiceFormService serviceFormService) {
		this.serviceFormService = serviceFormService;
	}

	@Autowired
	public void setBookingFormService(BookingFormService bookingFormService) {
		this.bookingFormService = bookingFormService;
	}

	@Autowired
	public void setAdminCredentialsService(AdminCredentialsService adminCredentialsService) {
		this.adminCredentialsService = adminCredentialsService;
	}

	@Autowired
	public void setContactFormService(ContactFormService contactFormService) {
		this.contactFormService = contactFormService;
	}

	@GetMapping("dashboard")
	public String adminDashboard() {
		return "admin/dashboard";
	}

	@GetMapping("readAllContacts")
	public String readAllContacts(Model model) {

		model.addAttribute("allcontacts",contactFormService.readAllContactsService());
		return "admin/readallcontacts";
	}

	@GetMapping("deleteContact/{id}")
	public String deleteContact( @PathVariable int id,RedirectAttributes redirectAttributes) {

		contactFormService.deleteContactService(id);
		redirectAttributes.addFlashAttribute("message","CONTACT DELETED SUCCESSFULLY");
		return "redirect:/admin/readAllContacts";
	}

	@GetMapping("changeCredentials")
	public String changeCredentialsView() {
		return "admin/changecredentials";
	}

	@PostMapping("changeCredentials")
	public String changeCredentials(
			@RequestParam("oldusername") String oldusername,
			@RequestParam("oldpassword") String oldpassword,
			@RequestParam("newusername") String newusername,
			@RequestParam("newpassword") String newpassword,
			RedirectAttributes redirectAttributes
	) {

		String result = adminCredentialsService.checkAdminCredentials(oldusername, oldpassword);
		if(result.equals("SUCCESS")) {
			//PASSWORD UPDATE
			result=adminCredentialsService.updateAdminCredentials(newusername, newpassword, oldusername);
			redirectAttributes.addFlashAttribute("message",result);
		}
		else {
			redirectAttributes.addFlashAttribute("message",result);
		}
		return "redirect:/admin/dashboard";
	}

	@GetMapping("readAllBookings")
	public String readAllBookings(Model model) {

		List<BookingForm> allBookingsService = bookingFormService.readAllBookingsService();
		System.out.println(allBookingsService);

		model.addAttribute("allBookings",allBookingsService);
		model.addAttribute("taxiAvailable", getOrCreateAppStatus().isAvailable());
		return "admin/readallbookings";
	}

	@GetMapping("deleteBooking/{id}")
	public String deleteBooking( @PathVariable int id,RedirectAttributes redirectAttributes) {

		bookingFormService.deleteBookingtService(id);
		redirectAttributes.addFlashAttribute("message","BOOKING DELETED SUCCESSFULLY");
		return "redirect:/admin/readAllBookings";
	}

	// ---------- BOOKING APPROVAL ----------

	@GetMapping("acceptBooking/{id}")
	@Transactional
	public String acceptBooking(@PathVariable int id, RedirectAttributes redirectAttributes) {
		return updateBookingStatus(id, "ACCEPTED", redirectAttributes);
	}

	@GetMapping("declineBooking/{id}")
	@Transactional
	public String declineBooking(@PathVariable int id, RedirectAttributes redirectAttributes) {
		return updateBookingStatus(id, "DECLINED", redirectAttributes);
	}

	private String updateBookingStatus(int id, String newStatus, RedirectAttributes redirectAttributes) {
		BookingForm booking = entityManager.find(BookingForm.class, id);
		if (booking == null) {
			redirectAttributes.addFlashAttribute("message", "BOOKING NOT FOUND");
		} else if (booking.getStatus() != null && !"PENDING".equals(booking.getStatus())) {
			// Only PENDING bookings can be changed
			redirectAttributes.addFlashAttribute("message", "BOOKING ALREADY " + booking.getStatus());
		} else {
			booking.setStatus(newStatus);
			redirectAttributes.addFlashAttribute("message", "BOOKING " + newStatus + " SUCCESSFULLY");
		}
		return "redirect:/admin/readAllBookings";
	}

	// ---------- TAXI AVAILABILITY TOGGLE ----------

	@PostMapping("toggleAvailability")
	public String toggleAvailability(RedirectAttributes redirectAttributes) {
		AppStatus status = getOrCreateAppStatus();
		status.setAvailable(!status.isAvailable());
		appStatusRepository.save(status);
		redirectAttributes.addFlashAttribute("message",
				status.isAvailable() ? "TAXI SERVICE IS NOW ON" : "TAXI SERVICE IS NOW OFF");
		return "redirect:/admin/readAllBookings";
	}

	// Single-row table (id = 1); created as ON the first time it is needed
	private AppStatus getOrCreateAppStatus() {
		return appStatusRepository.findById(1)
				.orElseGet(() -> appStatusRepository.save(new AppStatus()));
	}

	// ---------- SITE SETTINGS ----------

	@GetMapping("settings")
	public String settingsView(Model model) {
		model.addAttribute("settings", getFirstSiteSettings());
		return "admin/settings";
	}

	@PostMapping("settings")
	public String updateSettings(
			@RequestParam("contactNumber") String contactNumber,
			@RequestParam("email") String email,
			@RequestParam("mapIframeUrl") String mapIframeUrl,
			RedirectAttributes redirectAttributes) {

		if (contactNumber.isBlank() || email.isBlank() || mapIframeUrl.isBlank()) {
			redirectAttributes.addFlashAttribute("message", "ALL FIELDS ARE REQUIRED");
			return "redirect:/admin/settings";
		}

		// Update the existing record (never trust an id coming from the form)
		SiteSettings settings = getFirstSiteSettings();
		settings.setContactNumber(contactNumber.trim());
		settings.setEmail(email.trim());
		settings.setMapIframeUrl(mapIframeUrl.trim());
		siteSettingsRepository.save(settings);

		redirectAttributes.addFlashAttribute("message", "SETTINGS UPDATED SUCCESSFULLY");
		return "redirect:/admin/settings";
	}

	// First record; DataInitializer guarantees one exists, but fall back safely anyway
	private SiteSettings getFirstSiteSettings() {
		return siteSettingsRepository.findAll().stream().findFirst().orElseGet(SiteSettings::new);
	}

	@GetMapping("addService")
	public String addServiceView() {
		return "admin/addservice";
	}

	@InitBinder
	public void stopBinding(WebDataBinder webDataBinder) {
		webDataBinder.setDisallowedFields("image");
	}

	@PostMapping("addService")
	public String addService( @ModelAttribute ServiceForm serviceForm ,
							  @RequestParam("image") MultipartFile multipartFile,RedirectAttributes redirectAttributes  ) {

		String originalFilename = multipartFile.getOriginalFilename();
		serviceForm.setImage(originalFilename);

		try {

			ServiceForm service = serviceFormService.addService(serviceForm, multipartFile);
			if(service!=null) {
				redirectAttributes.addFlashAttribute("msg","Service Added Successfully");
			}else {
				redirectAttributes.addFlashAttribute("msg","Something went wrong");
			}

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("msg","Something went wrong");
		}


		return "redirect:/admin/addService";
	}
}
