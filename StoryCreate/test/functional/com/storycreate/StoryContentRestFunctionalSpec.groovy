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
		
	}
	void "GET as user returns error 401"() {
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
		
		then: "error"
		resp.status == 401
		resp.json[0]==null
		
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
