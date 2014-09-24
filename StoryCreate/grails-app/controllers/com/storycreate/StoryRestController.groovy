package com.storycreate

import grails.rest.RestfulController
import grails.converters.*
import grails.transaction.Transactional
import static org.springframework.http.HttpStatus.*
import grails.plugin.springsecurity.SpringSecurityUtils

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
			respond status = 404  // Not Found
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
		def currentUser = springSecurityService.currentUser;
		if(story.owner != currentUser && !SpringSecurityUtils.ifAllGranted("ROLE_admin")){
			def error = ["errors" : ["message" :"Story owner must equal to the user logged in"]]
		
			response.status = 405 //Method not allowed on resource
			respond error 
			return
		}
		def storyContent
		if(story.storyContent != null){
			storyContent = story.storyContent
			story.storyContent = null
		}

		if (storyContent != null){
			println("there is storycontent")
			// test to make sure user saving is only saving content with user as currentuser
			// All content must be same owner
			if (storyContent*.user.id.unique() != [currentUser.id]){
				def error = ["errors" : ["message" :"Cannot save storyContent as other users"]]
				response.status = 405 //Method not allowed on resource
				respond error
				return
			}
			storyContent.each {sc ->
				story.addToStoryContent(sc)
			}
		}
		story.save()
		response.status = 200
		respond story
	}

	def update() {
		def story = Story.get(id)
		if (story == null){
			def error = ["errors" : ["message" :"Story ${id} was not found"]]
			response.status = 404 //Resource not found
			respond error
			return
		}
		
		
	}

}
