package com.storycreate

class Profile {

	String email
	String fullName
	String bio
	String homepage
	String country
	byte[] photo
	
	static belongsTo = [user : User ]
	
    static constraints = {
		fullName nullable: true
		bio nullable: true
		homepage nullable: true, url: true
		email blank: false, email: true
		photo nullable: true, maxSize: 128000
		country nullable: true
		
    }
}
