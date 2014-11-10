package com.storycreate

import grails.rest.RestfulController
import grails.converters.*
import grails.transaction.Transactional
import static org.springframework.http.HttpStatus.*
import grails.plugin.springsecurity.SpringSecurityUtils

// TODO : create service for database transactions, simplify controller

class StoryRestController extends RestfulController{

	static responseFormats = ['json']
	def springSecurityService
	def storySecurelyService
	
    StoryRestController() {
		super(Story)
	}
	
	def index(Integer max){
		params.max = Math.min(max ?: 10, 100)
		JSON.use("storyList") {
			respond storySecurelyService.list(params) //, model: ["storyCount" : Story.count ]
		} 			
	}
	
	def show() {
		def story = Story.get(params.id)
		if (story == null){
			respondError(404,"Story not found")
			return
		}
		if( storySecurelyService.retrieve(story)){
			respond story
			return
		} else {
			respond error, [status : 405]
		}
	}  // def show()
	
	
	@Transactional
	def save() {
		def story = createResource()
		log.debug(story)
		
		story.validate()
		if(story.hasErrors()){
			response.status = 422
			respond story.errors
			return
		}
		
		//if contains storyContent and not ROLE_admin - error
		def storyContent = null
		if(story.storyContent != null && story.storyContent != []){
			storyContent = story.storyContent
			story.storyContent = null
			
			// not ROLE_admin - error.
			if (!SpringSecurityUtils.ifAllGranted("ROLE_admin")) {
				respondError(405, "StoryContent should be added via /api/storyContent")
				return
			}
		}
		
		// if story owner not currentUser and currentUser not ROLE_admin - error
		if( ! storySecurelyService.create(story)) {
			respondError(405,"Permissions issue")
			return
		}

		if (storyContent != null){
			println("there is storycontent")
			storyContent.each {sc ->
				story.addToStoryContent(sc)
			}
		}
		story.save(flush: true)
		response.status = 200
		respond story
	}

@Transactional
	def update(StoryCommand updatedStory) {

		def story = Story.get(params.id)
		if (story == null){
			respondError(404,"Story ${id} was not found")
			return
		}
		
		// if a user comes in with a nonexistent id, Grails creates an unsaved User and makes the id = null
		if (updatedStory.owner.id == null && updatedStory.owner != null){
			respondError(400, "Story owner does not exist")  //Bad Request
			return
		}
		
		story.properties = updatedStory
		story.validate()
		if( story.hasErrors() ) {
			response.status = 422
			respond story.errors
			return
		}
		
		if(storySecurelyService.update(story)){
			story.save(flush: true)
			response.status = 200
			respond story
		} else {
		respondError(405, "Permissions issue")
		}

	}
	
	@Transactional
	def delete() {
		def story = Story.get(params.id)
		if (story == null){
			def message = "Story " + params.id+ " was not found."
			respondError(404,message)
			return
		}

		if(storySecurelyService.delete(story)) {
			story.delete(flush: true)
			respondSuccess(200, "Story deleted")
		} else {
			respondError(405, "Permissions issue")
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

class StoryCommand {
	String title
	User owner
	String description
	Boolean isPublic
}