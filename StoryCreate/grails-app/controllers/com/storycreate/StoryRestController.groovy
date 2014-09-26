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
	
    StoryRestController() {
		super(Story)
	}
	
	def index(Integer max){
		params.max = Math.min(max ?: 10, 100)
		JSON.use("storyList") {
			respond Story.list(params), model: ["storyCount" : Story.count ]
		} 			
	}
	
	def show() {
		def story = Story.get(params.id)
		if (story == null){
			respondError(404,"Story not found")
			return
		}
		if( SpringSecurityUtils.ifAllGranted("ROLE_admin")){
			respond story
			return
		}
		if(story.isPublic){
			respond story
		} else {
			def currentUser = springSecurityService.currentUser;
			if (currentUser.id == story.owner.id) {
				respond story
			} else {
				def error = ["errors":["message" : "Story is set to private"]]
				respond error, [status : 405] //Method not allowed on resource
			}
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
		def currentUser = springSecurityService.currentUser;
		if(story.owner.id != currentUser.id && !SpringSecurityUtils.ifAllGranted("ROLE_admin")){
			respondError(405,"Story owner must equal to the user logged in")
			return
		}

		if (storyContent != null){
			println("there is storycontent")
			// test to make sure user saving is only saving content with user as currentuser
			// All content must be same owner
//			if (storyContent*.user.id.unique() != [currentUser.id]){
//				respondError(405,"Cannot save storyContent as other users")
//				return
//			}
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
//		def updatedStory = createResource()
		println("${updatedStory}")
		def story = Story.get(params.id)
		if (story == null){
			respondError(404,"Story ${id} was not found")
			return
		}
		println(updatedStory.dump())
		println(updatedStory.owner.dump())
		
		// if a user comes in with a nonexistent id, Grails creates an unsaved User and makes the id = null
		if (updatedStory.owner.id == null && updatedStory.owner != null){
			respondError(400, "Story owner does not exist")  //Bad Request
			return
		}
		
		if (!SpringSecurityUtils.ifAllGranted('ROLE_admin')){
			def currentUser = springSecurityService.currentUser;
			if (updatedStory.owner != null) {
				if (updatedStory.owner.id != story.owner.id || updatedStory.owner.id != currentUser.id) {
					respondError(405,"Story owner must equal to the user logged in")
					return
				} else {
					if(story.owner.id != currentUser.id ){
						respondError(405,"Story owner must equal to the user logged in 2")
						return
					}
				}
			}
		}
		story.properties = updatedStory
		story.validate()
		if( story.hasErrors() ) {
			response.status = 422
			respond story.errors
			return
		}
		
		story.save(flush: true)
		
		response.status = 200
		respond story
	}
	@Transactional
	def delete() {
		def story = Story.get(params.id)
		if (story == null){
			def message = "Story " + params.id+ " was not found."
			respondError(404,message)
			return
		}
		def title = story.title
		def storyId = story.id
		
		// if not admin user, then make sure owner of story is same as logged in user
		if (!SpringSecurityUtils.ifAllGranted('ROLE_admin')){
			def currentUser = springSecurityService.currentUser;
			if(story.owner.id != currentUser.id ){
				respondError(405,"Story owner must equal to the user logged in when deleting story")
				return
			}
		}
		
		story.delete(flush: true)
		def message = "Story deleted. id: " + storyId + ", Title: " + title
		respondSuccess(200, message)
		
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