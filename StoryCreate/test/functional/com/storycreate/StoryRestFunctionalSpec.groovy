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
	
	void "GET as ROLE_admin returns list of Story JSON without storyContent"() {
		when: "Obtaining access_token for username: admin from api/login"
		def resp = login(adminCreds)
		def access_token = resp.json.access_token
		
		and: "accessing GET from /api/story with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/story"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "view list of Storys without storyContent"
		resp.json[0].title == 'The very First Story'
		resp.json[1].title == 'The Storm'
		resp.json[0].storyContent == null
		resp.json[1].storyContent == null
		
	}
	
	void "GET as ROLE_user returns list of Story JSON without storyContent"() {
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		
		and: "accessing GET from /api/story with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/story"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "view list of Storys without storyContent"
		resp.json[0].title == 'The very First Story'
		resp.json[1].title == 'The Storm'
		resp.json[0].storyContent == null
		resp.json[1].storyContent == null
		
	}
	
	void "SHOW as ROLE_user returns a Story JSON with storyContent"() {
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		
		and: "accessing GET from /api/story with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/story/1"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "view list of Storys without storyContent"
		resp.json.title == 'The very First Story'
		resp.json.storyContent != null
		resp.json.storyContent[0].content.contains("Nam eleifend libero quis feugiat") 
		
	}
	void "SHOW a nonexistant story should produce error"() {
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		
		and: "accessing GET from /api/story with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/story/5644"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "error returned"
		resp.json.title == null
		resp.status == 404
		
	}
	void "save() creates a new story by joe as joe"() {
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		
		and: "Saving new Story as joe"
		def joeId = 2
		def storyJSON = newStoryJSON(joeId)
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
		
		then: "story created"
		resp.status == 200
		resp.json.id != null
		resp.json.title == "new Story"
	}
	void "save() creates a new story by joe as admin"() {
		when: "Obtaining access_token for username: admin from api/login"
		def resp = login(adminCreds)
		def access_token = resp.json.access_token
		
		and: "Saving new Story by joe"
		def joeId = 2
		def storyJSON = newStoryJSON(joeId)
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
		
		then: "story created"
		resp.json.id != null
		resp.json.title == "new Story"
		resp.status == 200
	}
	
	void "save() does NOT create a new story by admin as joe"() {
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		
		and: "Saving new Story by admin"
		def adminId = 1
		def storyJSON = newStoryJSON(adminId)
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
		
		then: "Errors returned"
		resp.json.id == null
		resp.json.title == null
		resp.status == 405
	}
	void "save() request contains a new incomplete story so does NOT create"() {
		when: "Obtaining access_token for username: admin from api/login"
		def resp = login(adminCreds)
		def access_token = resp.json.access_token
		
		and: "Saving new incomplete story by admin"
		def adminId = 1
		def storyJSON = newIncompleteStoryJSON(adminId)
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
		
		then: "Errors returned"
		resp.json.id == null
		resp.json.title == null
		resp.status == 422
	}
	void "save() does NOT create new story when request has storyContent when logged in user"() {
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
		
		then: "new story NOT created"
		resp.json.id == null
		resp.json.title == null
		resp.status == 405
	}
	void "save() creates new story with storyContent when logged in as admin"() {
		when: "Obtaining access_token for username: admin from api/login"
		def resp = login(adminCreds)
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
		
		then: "new story created"
		resp.json.id != null
		resp.json.title != null
		resp.status == 200
	}
	void "update() the title of an existing story as admin"() {
		when: "Obtaining access_token for username: admin from api/login"
		def resp = login(adminCreds)
		def access_token = resp.json.access_token
		
		and: "updating existing story with new title"
		def storyJSON = story1JSON(2)
		storyJSON.title = "I've changed"
		println("storyJSON: ${storyJSON}")
		resp = rest.put("http://localhost:8080/StoryCreate/api/story/1"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				storyJSON
			}
		}

		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "story updated"
		resp.json.id == 1
		resp.json.title == "I've changed"
		resp.status == 200
	}
	void "update() the title of an existing story as admin where request has invalid user ID in story owner."() {
		when: "Obtaining access_token for username: admin from api/login"
		def resp = login(adminCreds)
		def access_token = resp.json.access_token
		
		and: "updating existing story with new title and an invalid user id in requesting JSON"
		def storyJSON = story1JSON(44)
		storyJSON.title = "I've changed"
		println("storyJSON: ${storyJSON}")
		resp = rest.put("http://localhost:8080/StoryCreate/api/story/1"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				storyJSON
			}
		}

		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "Error is received"
		resp.json.id == null
		resp.json.title == null
		resp.status == 400
		resp.json.errors[0].message == "Story owner does not exist"
	}
	
	void "update() the title of an existing story by joe as joe"() {
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		
		and: "updating existing story with new title"
		def storyJSON = story2JSON(2)
		storyJSON.title = "I've changed"
		println("storyJSON: ${storyJSON}")
		resp = rest.put("http://localhost:8080/StoryCreate/api/story/2"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				storyJSON
			}
		}

		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "story updated"
		resp.json.id == 2
		resp.json.title == "I've changed"
		resp.status == 200
	}
	void "update() the title of an existing story by admin as joe"() {
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		
		and: "updating existing story with new title"
		def storyJSON = story1JSON(1)
		storyJSON.title = "I've changed"
		println("storyJSON: ${storyJSON}")
		resp = rest.put("http://localhost:8080/StoryCreate/api/story/1"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				storyJSON
			}
		}

		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "Errors "
		resp.json.id == null
		resp.json.title == null
		resp.status == 405
		resp.json.errors[0].message == "Story owner must equal to the user logged in"
	}
	void "update() the title of an existing story with StoryContent by admin as admin"() {
		when: "Obtaining access_token for username: admin from api/login"
		def resp = login(adminCreds)
		def access_token = resp.json.access_token
		
		and: "updating existing story with new title"
		def storyJSON = story1JSON(1)
		storyJSON.title = "I've changed2"
		storyJSON.storyContent = newStoryContent(1,1)
		println("storyJSON: ${storyJSON}")
		resp = rest.put("http://localhost:8080/StoryCreate/api/story/1"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				storyJSON
			}
		}

		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "Story updated but not StoryContent"
		resp.json.id != null
		resp.json.title == "I've changed2"
		resp.status == 200
	}
	void "update() change title back with StoryContent by admin as admin"() {
		when: "Obtaining access_token for username: admin from api/login"
		def resp = login(adminCreds)
		def access_token = resp.json.access_token
		
		and: "updating existing story with new title"
		def storyJSON = story1JSON(1)
		storyJSON.title = "I've changed2"
		storyJSON.storyContent = newStoryContent(1,1)
		println("storyJSON: ${storyJSON}")
		resp = rest.put("http://localhost:8080/StoryCreate/api/story/1"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				storyJSON
			}
		}

		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "Story updated but not StoryContent"
		resp.json.id != null
		resp.json.title == "I've changed2"
		resp.status == 200
	}
	void "delete() an existing story as ROLE_Admin should be success"()	{
		when: "Obtaining access_token for username: admin from api/login"
		def resp = login(adminCreds)
		def access_token = resp.json.access_token
		
		and: "deleting story written by joe"
		resp = rest.delete("http://localhost:8080/StoryCreate/api/story/2"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				storyJSON
			}
		}
		resp.json instanceof JSONObject
		println (resp.json)
		
		and: "requesting story"
		def resp2 = rest.get("http://localhost:8080/StoryCreate/api/story/2"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				storyJSON
			}
		}
		resp2.json instanceof JSONObject
		println (resp2.json)
		
		then: "delete() response should be successful and second request should be error"
		resp.status == 200
		resp2.status == 404 // Not Found
	}
	
	void "delete() an existing story by admin as joe should be error"()	{
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		
		and: "deleting story written by admin"
		resp = rest.delete("http://localhost:8080/StoryCreate/api/story/1"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				storyJSON
			}
		}
		
		and: "requesting story"
		def resp2 = rest.get("http://localhost:8080/StoryCreate/api/story/1"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				storyJSON
			}
		}
		resp2.json instanceof JSONObject
		println (resp2.json)
		
		then: "delete() response should be error and second request should be success since story still exists"
		resp.status == 405
		resp2.status == 200 
	}
	
	
	def newStoryWithStoryContent(userId, userId2){
		def story = newStoryJSON(userId)
		story['storyContent'] = newStoryContent(userId, userId2)
		return story
	}
		
	def newStoryContent(userId, userId2){
		return [[
			content			:		"This is the first chapter of my story.  Wow!",
			user			:		[id  :  userId],
			],
		[
			content			:		"This is the second chapter of my story.  cool!",
			user			:		[id  :  userId2],
			]]
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
	
	def story1JSON(userId) {
		return [
				title				:		"The very First Story",
				isPublic			:		true,
				owner				:		[id: userId],
				description			:		"The first is usually the best"
			]
	}
	def story2JSON(userId) {
		return [
				title				:		"The Storm",
				isPublic			:		true,
				owner				:		[id: userId],
				description			:		"It all started on dark and stormy night"
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
