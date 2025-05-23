package com.vid.rezal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "email",nullable = false)
    private String email;
    
    @Column(name = "chatId",nullable = false)
    private Long chatId;
    
    @Column(nullable = true)
    private String whatsappPhoneNumber;
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public String getWhatsappPhoneNumber() {
		return whatsappPhoneNumber;
	}

	public void setWhatsappPhoneNumber(String whatsappPhoneNumber) {
		this.whatsappPhoneNumber = whatsappPhoneNumber;
	}

	
    // Getters and setters
    
    
}






