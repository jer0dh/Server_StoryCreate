package com.storycreate

import grails.rest.RestfulController
import grails.converters.*
import grails.transaction.Transactional
import static org.springframework.http.HttpStatus.*
import grails.plugin.springsecurity.SpringSecurityUtils

class StoryContentRestController extends RestfulController{

	static responseFormats = ['json']
	def springSecurityService
	
    StoryContentRestController() {
		super(StoryContent)
	}
	
	def index(Integer max){
		log.debug("In index()")
		if( SpringSecurityUtils.ifAllGranted("ROLE_admin")){
			params.max = Math.min(max ?: 10, 100)
			respond listAllResources(params), model: [("${resourceName}Count".toString()): countResources()]
		} else {
			respondError(401, "Not Authorized")
		}
	}
	
//	def show(){
//		
//		def storyContent = StoryContent.get(params.id)
//		if (storyContent == null){
//			respondError(404,"StoryContent was not found")
//			return
//		}
//		if( SpringSecurityUtils.ifAllGranted("ROLE_admin")){
//			respond storyContent
//			return
//		}
//	}
	
	def respondError(status, message){
		def error = ["errors" : [["message" : message]]]
		log.debug (message)
		response.status = status
		respond error
		return
	}
	def respondSuccess(status, message){
		def success = ["success" : [["message" : message]]]
		log.debug (message)
		response.status = status
		respond success
		return
	}
}
