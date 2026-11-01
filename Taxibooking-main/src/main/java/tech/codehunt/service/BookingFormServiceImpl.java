package tech.codehunt.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.codehunt.dao.BookingFormCrud;
import tech.codehunt.model.BookingForm;

@Service
public class BookingFormServiceImpl implements BookingFormService {
	
	private BookingFormCrud bookingFormCrud;
	
    @Autowired
	public void setBookingFormCrud(BookingFormCrud bookingFormCrud) {
		this.bookingFormCrud = bookingFormCrud;
	}

	@Override
	public BookingForm saveBookingFormService(BookingForm bookingForm) {
		// TODO Auto-generated method stub
		
		return bookingFormCrud.save(bookingForm);
	}

	@Override
	public List<BookingForm> readAllBookingsService() {
		// TODO Auto-generated method stub
		return bookingFormCrud.findAll();
	}

	@Override
	public void deleteBookingtService(int id) {
		
		bookingFormCrud.deleteById(id);
	}
}
