package com.vid.rezal.services;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vid.rezal.model.Customer;
import com.vid.rezal.model.ErrorCodes;
import com.vid.rezal.model.ErrorDescriptions;
import com.vid.rezal.model.Statics;
import com.vid.rezal.model.ViewData;
import com.vid.rezal.repository.CustomerRepository;
import com.vid.rezal.utility.RezalException;


@Service
public class ServiceLocator {
	private final Log log = LogFactory.getLog(ServiceLocator.class);
	@Autowired
	private CustomerRepository customerRepo;
	
	public ViewData<Customer> addCustomer(Customer customer) {
		ViewData<Customer> view = new ViewData<>();
		try {
			if(customer!=null && !customerRepo.existsByEmail(customer.getEmail())) {
				customerRepo.save(customer);
				view.setData(customer);
			}else {
				throw new RezalException(ErrorDescriptions.CUSTOMEREXISTALREADY);
			}
		}catch (RezalException e) {
			view.setError(e.getErrorMsg());
			view.setErrorCode(ErrorCodes.SOMETHING_WENT_WRONG);
			view.setResponse(Statics.FAILURE);
		} catch (Exception e) {
			view.setError(ErrorDescriptions.SOMETHING_WENT_WRONG);
			view.setErrorCode(ErrorCodes.SOMETHING_WENT_WRONG);
			view.setResponse(Statics.FAILURE);
		} 
		return view;
	}
	
	public ViewData<List<Customer>> getAllCustomer() {
		ViewData<List<Customer>> view = new ViewData<>();
		try {
			List<Customer> customers = customerRepo.findAll();
			if(customers.size() > 0) {
				view.setData(customers);
			}else {
				throw new RezalException(ErrorDescriptions.NOCUSTOMERAVAILABLE);
			}
		}catch (RezalException e) {
			view.setError(e.getErrorMsg());
			view.setErrorCode(ErrorCodes.SOMETHING_WENT_WRONG);
			view.setResponse(Statics.FAILURE);
		} catch (Exception e) {
			view.setError(ErrorDescriptions.SOMETHING_WENT_WRONG);
			view.setErrorCode(ErrorCodes.SOMETHING_WENT_WRONG);
			view.setResponse(Statics.FAILURE);
		} 
		return view;
	}

	public ViewData<Customer> getCustomerByName(String name) {
		log.warn(name);
		ViewData<Customer> view = new ViewData<>();
		try {
			if(name!=null) {
				Customer customer = customerRepo.findByName(name);
				if (customer==null) throw new RezalException(ErrorDescriptions.NOCUSTOMERAVAILABLE);
				view.setData(customer);
			}else {
				throw new RezalException(ErrorDescriptions.NOCUSTOMERAVAILABLE);
			}
		}catch (RezalException e) {
			view.setError(e.getErrorMsg());
			view.setErrorCode(ErrorCodes.SOMETHING_WENT_WRONG);
			view.setResponse(Statics.FAILURE);
		} catch (Exception e) {
			view.setError(ErrorDescriptions.SOMETHING_WENT_WRONG);
			view.setErrorCode(ErrorCodes.SOMETHING_WENT_WRONG);
			view.setResponse(Statics.FAILURE);
		} 
		return view;
	}
	

	public ViewData<String> sendMessageToUser(String name, String text) {
		ViewData<String> view = new ViewData<>();
		String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
        String apiToken = "6160419969:AAHvxFn2seQQH6EBfA7D5nVKtgQFi32UnhE";
        if(!customerRepo.existsByName(name)) {
        	view.setData(ErrorDescriptions.NOCUSTOMERAVAILABLE);
        	return view;
        }
        Customer customer = customerRepo.findByName(name);
        String chatId = Long.toString(customer.getChatId());
        urlString = String.format(urlString, apiToken, chatId, text);
        RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(urlString, String.class);
	//	System.out.println(response.getBody());
		view.setData("Message sent to "+name+"   your text is "+text);
	return view;
	}
}