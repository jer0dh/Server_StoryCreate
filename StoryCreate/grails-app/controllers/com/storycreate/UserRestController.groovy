package com.storycreate

import grails.rest.RestfulController
import grails.converters.*
import grails.plugin.springsecurity.SpringSecurityUtils

class UserRestController extends RestfulController{

	static responseFormats = ['json']
	def springSecurityService
    UserRestController() {
		super(User)
	}
	// TODO - need to add if user logged in is equal to request id, then return email address as well
	
	@Override
	def index(Integer max) { 
		if( SpringSecurityUtils.ifAllGranted("ROLE_admin")){
			log.debug("has ROLE_admin")
			JSON.use("userListForAdmin") {
				respond User.list(max: max)
			}
		} else {
			response.status = 404;
		}
	}
	@Override
	def show() {
		log.debug("In show()")
		def currentUser = springSecurityService.currentUser;
		if(params.int('id') == currentUser.id) {
			log.debug("user id's are equal")
			JSON.use("userListForAdmin") {
			respond currentUser
			}
		} else {
			respond User.get(params.id)
		}
	}
	
	
}
