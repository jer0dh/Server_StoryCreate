package com.storycreate

import grails.rest.RestfulController
import grails.converters.*

class UserRestController extends RestfulController{

	static responseFormats = ['json']
	
    UserRestController() {
		super(User)
	}
}
