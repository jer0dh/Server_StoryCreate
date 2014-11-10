package com.storycreate

import java.util.Date;

import grails.rest.RestfulController
import grails.converters.*
import grails.transaction.Transactional
import static org.springframework.http.HttpStatus.*
import grails.plugin.springsecurity.SpringSecurityUtils

class StoryContentRestController extends RestfulController{

	static responseFormats = ['json']
	def springSecurityService
	def storyContentSecurelyService
	
    StoryContentRestController() {
		super(StoryContent)
	}
	
	def index(Integer max){
		params.max = Math.min(max ?: 10, 100)
		respond storyContentSecurelyService.list(params)
	}
	
	@Transactional
	def show() {
		def storyContent = StoryContent.get(params.id)
		if (storyContent == null){
			respondError(404,"Story not found")
			return
		}
		if( storyContentSecurelyService.retrieve(storyContent)){
			response.status = 200
			respond storyContent
			return
		} else {
			respond error, [status : 405]
		}
	}
	
	
	@Transactional
	def save() {
		def storyContent = createResource()
		log.debug(storyContent)
		//TODO  Do we need to test if storyContent already exists?
		
		storyContent.validate()
		if(storyContent.hasErrors()){
			response.status = 422
			respond storyContent.errors
			return
		}
		if(storyContentSecurelyService.create(storyContent)){
			storyContent.save()
			response.status = 200
			respond storyContent
		} else {
			respondError(405,"Permissions issue")
			return
		}
		
	}
	
	@Transactional
	def update(StoryContentCommand scc) {
		println("${scc}")
		def storyContent = StoryContent.get(params.id)
		if (storyContent == null){
			respondError(404,"StoryContent ${id} was not found")
			return
		}
		storyContent.properties = scc
		storyContent.validate()
		if( storyContent.hasErrors() ) {
			response.status = 422
			respond storyContent.errors
			return
		}
		
		if (storyContentSecurelyService.update(storyContent)){
			storyContent.save(flush: true)		
			response.status = 200
			respond storyContent
		} else {
			respondError(405,"Permissions issue")
			return
		}
	}
	
	def delete() {
		def storyContent = StoryContent.get(params.id)
		if (storyContent == null){
			def message = "Story Content" + params.id+ " was not found."
			respondError(404,message)
			return
		}

		if(storyContentSecurelyService.delete(storyContent)){
			storyContent.delete(flush: true)
			respondSuccess(200, "storyContent deleted")
		} else {
			respondError(403, "permissions issue")
		}		
	}
	
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
// for update - only content can change
class StoryContentCommand {
	String content
	
	static contraints = {

		content nullable:false

	}
}