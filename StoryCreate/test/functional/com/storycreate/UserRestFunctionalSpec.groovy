package com.storycreate


import spock.lang.*
import grails.plugins.rest.client.RestBuilder
import org.codehaus.groovy.grails.web.json.JSONObject


class UserRestFunctionalSpec extends Specification{
	@Shared
	def rest = new RestBuilder()
	def adminCreds = ["username":"admin","password":"password"]
	def userCreds = ["username":"joe","password":"password"]

	void "GET as ROLE_admin returns list of Users JSON with email"() {
		when: "Obtaining access_token for username: admin from api/login"
		def resp = login(adminCreds)
		def access_token = resp.json.access_token
		println(resp.json.access_token)
		
		and: "accessing GET from /api/user with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/user"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "view list of users with email addresses"
		resp.json[0].email == 'j.hammer@yahoo.com'
		resp.json[1].email == 'joe@yahoo.com'
		
	}
	
	void "GET as ROLE_user should NOT return a list, returns 404"() {
		when: "Obtaining access_token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token
		println(resp.json.access_token)
		
		and: "accessing GET from /api/user with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/user"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		println (resp.json)
		
		then: "Status 404 should be responded"
		resp.status == 404
	}
	
	void "GET not logged in should return status 500"() {
		when: "accessing GET from /api/user without access_token"
		def resp = rest.get("http://localhost:8080/StoryCreate/api/user")

		then: "Status 500 should be responded"
		resp.status == 500
	}
	
	void "Show - api\\users\\2 - as ROLE_admin should return User JSON with email"(){

		when:"Obtaining access token for username: admin from api/login"
		def resp = login(adminCreds)
		def access_token = resp.json.access_token

			
		and: "accessing show() from /api/user/2 with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/user/2"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		
		then: "JSON of User 2 should be returned including email address"
		resp.json.username == "joe"
		resp.json.email == "joe@yahoo.com"
	}
	
	void "Show - api\\users\\2 - as ROLE_user should return User JSON with email if logged in as same user"(){

		when:"Obtaining access token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token

			
		and: "accessing show() from /api/user/2 with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/user/2"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		
		then: "JSON of User 2 should be returned including email address"
		resp.json.username == "joe"
		resp.json.email == "joe@yahoo.com"
	}
	
	void "Show - api\\users\\1 - as ROLE_user should return User JSON without email"(){
		
		when:"Obtaining access token for username: joe from api/login"
		def resp = login(userCreds)
		def access_token = resp.json.access_token

			
		and: "accessing show() from /api/user/1 with access_token"
		resp = rest.get("http://localhost:8080/StoryCreate/api/user/1"){
			header 'Authorization', 'Bearer ' + access_token
		}
		resp.json instanceof JSONObject
		
		then: "JSON of User 2 should be returned including email address"
		resp.json.username == "admin"
		resp.json.email == null
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