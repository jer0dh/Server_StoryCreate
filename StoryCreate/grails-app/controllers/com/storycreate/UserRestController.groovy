package com.storycreate

import grails.rest.RestfulController
import grails.converters.*

class UserRestController extends RestfulController{

	static responseFormats = ['json']
	def springSecurityService
	
    UserRestController() {
		super(User)
	}
	// TODO - need to add if user logged in is equal to request id, then return email address as well
	
	def index() {
		if(params.id == null){
			response.status = 404;		
		} else {
			super.index(params)
		}
	}
}
