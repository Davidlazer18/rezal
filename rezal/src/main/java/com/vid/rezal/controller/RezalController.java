package com.vid.rezal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vid.rezal.model.Customer;
import com.vid.rezal.model.ViewData;
import com.vid.rezal.services.ServiceLocator;

@RestController
public class RezalController {
	@Autowired
	ServiceLocator service;

	@PostMapping("/addCustomers")
	public ViewData<Customer> addCustomer(@RequestBody Customer customer){
		return service.addCustomer(customer);
	}

	@GetMapping("/getAllCustomers")
	public ViewData<List<Customer>> getAllCustomer(){
		return service.getAllCustomer();
	}

	@GetMapping("/getByName")
	public ViewData<Customer> getCustomerByName(@RequestParam String name){
		return service.getCustomerByName(name);
	}
	
	@GetMapping("/sendMessageToUser")
	public ViewData<String> sendMessageToUser(
			@RequestParam String name,
			@RequestParam String text){
		return service.sendMessageToUser(name,text);
	}
	@GetMapping("/getChat")
	public ViewData<String> getChat(@RequestParam String text){
		return service.getChat(text);
	}
}

