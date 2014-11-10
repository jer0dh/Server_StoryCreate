package com.storycreate

import spock.lang.*
import grails.plugins.rest.client.RestBuilder
import static org.springframework.http.HttpStatus.*

import org.codehaus.groovy.grails.web.json.JSONObject

class StoryContentRestFunctionalSpec extends Specification {
	@Shared
	def rest = new RestBuilder()
	def adminCreds = ["username":"admin","password":"password"]
	def userCreds = ["username":"joe","password":"password"]
	def user2Creds = ["username":"jane", "password" : "password"]
	
	void "GET as ROLE_admin returns list of StoryContent JSON"() {
		when: "Obtaining access_token for username: admin from api/login"
		def resp = login(adminCreds)
		def access_token = resp.json.access_token
		println(access_token)
		and: "accessing GET from /api/storyContent with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/storyContent"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "view list of storyContent"
		resp.json[0].content.contains('Nam eleifend libero')
		resp.json[0].author.id == 1
		resp.json[1].content.contains('Nulla id magna nec enim')
		resp.json.size() == 5
		
	}
	void "GET as user returns only their StoryContent"() {
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		println(access_token)
		and: "accessing GET from /api/storyContent with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/storyContent"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "list of Joe's storyContent"
		resp.status == 200
		resp.json[0]!=null
		resp.json.size() == 2
		
	}
	
	void "GET/Show returns StoryContent"() {
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		println(access_token)
		
		and: "accessing GET from /api/storyContent with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/storyContent/1"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "storyContent of id 1"
		resp.status == 200
		resp.json !=null
		resp.json.content.contains("Nam eleifend libero") == true
		
	}
	
	void "POST/save"(){
		when: "Obtaining access_token for username: admin from api/login"
		def resp = login(adminCreds)
		def access_token = resp.json.access_token
		println(access_token)
		
		and: "Creating new StoryContent for story 1"
		def sc = newStoryContent()
		resp = rest.post("http://localhost:8080/StoryCreate/api/storyContent"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				sc
			}
		}
		resp.json instanceof JSONObject
		
		then: "should have new sc ID"
		resp.status == 200
		resp.json.id != null
		
		
	}
	void "Put/update "(){
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		println(access_token)
		and: "accessing GET from /api/storyContent with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/storyContent"){
			header 'Authorization', 'Bearer ' + access_token
		}
		and: "we obtain json list of Joe's StoryContent"
		resp.json instanceof JSONObject
		println (resp.json)
		def newSc = resp.json[0]
		
		
		and: "we alter and send an updated storycontent"
		newSc.content = "This is new StoryContent"
		resp = rest.put("http://localhost:8080/StoryCreate/api/storyContent/3"){
			header 'Authorization', 'Bearer ' + access_token
			contentType "application/json"
			json {
				newSc
			}
		}
		resp.json instanceof JSONObject

		then: "Status 200 and new instance of storycontent returned"
		resp.status == 200
		resp.json.content == "This is new StoryContent"
	}

	void "DELETE StoryContent test"() {
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		println(access_token)
		and: "accessing GET from /api/storyContent with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/storyContent"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		println (resp.json)
		def numOfSC = resp.json.size()
		assert numOfSC > 1
		def scToDelete = resp.json[1].id
		println("scToDelete = ${scToDelete}")
		
		and: "we DELETE one of the StoryContent as Joe"
		resp = rest.delete("http://localhost:8080/StoryCreate/api/storyContent/${scToDelete}"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		assert resp.status == 200
		
		and: "we get a new list of Story Content"
		resp = rest.get("http://localhost:8080/StoryCreate/api/storyContent"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "one of the StoryContents should be deleted"
		resp.json.size() == numOfSC - 1
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
	
	def newStoryContent() {
		return [
				content		:		"New Story content",
				author		:		[id : 1],
				story		:		[id : 1]
			]
		
	}
	
}
