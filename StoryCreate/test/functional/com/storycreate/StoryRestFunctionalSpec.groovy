package com.storycreate

import spock.lang.*
import grails.plugins.rest.client.RestBuilder
import static org.springframework.http.HttpStatus.*
import org.codehaus.groovy.grails.web.json.JSONObject

class StoryRestFunctionalSpec extends Specification{
	@Shared
	def rest = new RestBuilder()
	def adminCreds = ["username":"admin","password":"password"]
	def userCreds = ["username":"joe","password":"password"]
	
//	void "GET as ROLE_admin returns list of Story JSON without storyContent"() {
//		when: "Obtaining access_token for username: admin from api/login"
//		def resp = login(adminCreds)
//		def access_token = resp.json.access_token
//		
//		and: "accessing GET from /api/story with access_token"
//		resp = rest.get("http://localhost:8080/StoryCreate/api/story"){
//			header 'Authorization', 'Bearer ' + access_token
//		}
//		resp.json instanceof JSONObject
//		println (resp.json)
//		
//		then: "view list of Storys without storyContent"
//		resp.json[0].title == 'The very First Story'
//		resp.json[1].title == 'The Storm'
//		resp.json[0].storyContent == null
//		resp.json[1].storyContent == null
//		
//	}
//	
//	void "GET as ROLE_user returns list of Story JSON without storyContent"() {
//		when: "Obtaining access_token for username: joe from api/login"
//		def resp = login(userCreds)
//		def access_token = resp.json.access_token
//		
//		and: "accessing GET from /api/story with access_token"
//		resp = rest.get("http://localhost:8080/StoryCreate/api/story"){
//			header 'Authorization', 'Bearer ' + access_token
//		}
//		resp.json instanceof JSONObject
//		println (resp.json)
//		
//		then: "view list of Storys without storyContent"
//		resp.json[0].title == 'The very First Story'
//		resp.json[1].title == 'The Storm'
//		resp.json[0].storyContent == null
//		resp.json[1].storyContent == null
//		
//	}
//	
//	void "SHOW as ROLE_user returns a Story JSON with storyContent"() {
//		when: "Obtaining access_token for username: joe from api/login"
//		def resp = login(userCreds)
//		def access_token = resp.json.access_token
//		
//		and: "accessing GET from /api/story with access_token"
//		resp = rest.get("http://localhost:8080/StoryCreate/api/story/1"){
//			header 'Authorization', 'Bearer ' + access_token
//		}
//		resp.json instanceof JSONObject
//		println (resp.json)
//		
//		then: "view list of Storys without storyContent"
//		resp.json.title == 'The very First Story'
//		resp.json.storyContent != null
//		resp.json.storyContent[0].content.contains("Nam eleifend libero quis feugiat") 
//		
//	}
//
//	void "save() creates a new story as joe by joe"() {
//		when: "Obtaining access_token for username: joe from api/login"
//		def resp = login(userCreds)
//		def access_token = resp.json.access_token
//		
//		and: "Saving new Story as joe"
//		def joeId = 2
//		def storyJSON = newStoryJSON(joeId)
//		println("storyJSON: ${storyJSON}")
//		resp = rest.post("http://localhost:8080/StoryCreate/api/story"){
//			header 'Authorization', 'Bearer ' + access_token
//			contentType "application/json"
//			json {
//				storyJSON
//			}
//		}
//
//		resp.json instanceof JSONObject
//		println (resp.json)
//		
//		then: "story created"
//		resp.json.id != null
//		resp.json.title == "new Story"
//		resp.status == 200
//	}
//	void "save() creates a new story as admin by joe"() {
//		when: "Obtaining access_token for username: admin from api/login"
//		def resp = login(adminCreds)
//		def access_token = resp.json.access_token
//		
//		and: "Saving new Story by joe"
//		def joeId = 2
//		def storyJSON = newStoryJSON(joeId)
//		println("storyJSON: ${storyJSON}")
//		resp = rest.post("http://localhost:8080/StoryCreate/api/story"){
//			header 'Authorization', 'Bearer ' + access_token
//			contentType "application/json"
//			json {
//				storyJSON
//			}
//		}
//
//		resp.json instanceof JSONObject
//		println (resp.json)
//		
//		then: "story created"
//		resp.json.id != null
//		resp.json.title == "new Story"
//		resp.status == 200
//	}
//	
//	void "save() creates a new story as joe by admin"() {
//		when: "Obtaining access_token for username: joe from api/login"
//		def resp = login(userCreds)
//		def access_token = resp.json.access_token
//		
//		and: "Saving new Story by admin"
//		def adminId = 1
//		def storyJSON = newStoryJSON(adminId)
//		println("storyJSON: ${storyJSON}")
//		resp = rest.post("http://localhost:8080/StoryCreate/api/story"){
//			header 'Authorization', 'Bearer ' + access_token
//			contentType "application/json"
//			json {
//				storyJSON
//			}
//		}
//
//		resp.json instanceof JSONObject
//		println (resp.json)
//		
//		then: "Errors returned"
//		resp.json.id == null
//		resp.json.title == null
//		resp.status == 405
//	}
//	void "save() request contains a new incomplete story"() {
//		when: "Obtaining access_token for username: admin from api/login"
//		def resp = login(adminCreds)
//		def access_token = resp.json.access_token
//		
//		and: "Saving new incomplete story by admin"
//		def adminId = 1
//		def storyJSON = newIncompleteStoryJSON(adminId)
//		println("storyJSON: ${storyJSON}")
//		resp = rest.post("http://localhost:8080/StoryCreate/api/story"){
//			header 'Authorization', 'Bearer ' + access_token
//			contentType "application/json"
//			json {
//				storyJSON
//			}
//		}
//
//		resp.json instanceof JSONObject
//		println (resp.json)
//		
//		then: "Errors returned"
//		resp.json.id == null
//		resp.json.title == null
//		resp.status == 422
//	}
	void "save() new story with storyContent by logged in user"() {
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		
		and: "Saving new story with storyContent created by joe"
		def userId = 2
		def storyJSON = newStoryWithStoryContent(userId, userId)
		println("storyJSON: ${storyJSON}")
		resp = rest.post("http://localhost:8080/StoryCreate/api/story"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				storyJSON
			}
		}

		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "new story created"
		resp.json.id != null
		resp.json.title == "new Story"
		resp.status == 200
	}
	void "save() new story with storyContent with different userId"() {
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		
		and: "Saving new story with storyContent created by joe"
		def userId = 2
		def adminId = 1
		def storyJSON = newStoryWithStoryContent(userId, adminId)
		println("storyJSON: ${storyJSON}")
		resp = rest.post("http://localhost:8080/StoryCreate/api/story"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				storyJSON
			}
		}

		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "Error thrown"
		resp.json.id == null
		resp.json.title == null
		resp.status == 405
	}
	
	def newStoryWithStoryContent(userId, userId2){
		def story = newStoryJSON(userId)
		story['storyContent'] = [[
			content			:		"This is the first chapter of my story.  Wow!",
			user			:		[id  :  userId],
			],
		[
			content			:		"This is the second chapter of my story.  cool!",
			user			:		[id  :  userId2],
			]]
		
		return story
	}
	def newStoryJSON(userId) {
		return [ 	title			:		"new Story",
					description		:		"added by JSON",
					owner			:		[id 	:		userId],
					isPublic		:		true 	
			
			]
	}
	
	def newIncompleteStoryJSON(userId) {
		return [ 	
					description		:		"added by JSON",
					owner			:		[id 	:		userId],
					isPublic		:		true
			
			]
	}
	def login(creds) {
		def resp = rest.post("http://localhost:8080/StoryCreate/api/login"){
			contentType "application/json"
			json {
				creds
			}
		}
		resp.json instanceof JSONObject
		return resp
	}

}
