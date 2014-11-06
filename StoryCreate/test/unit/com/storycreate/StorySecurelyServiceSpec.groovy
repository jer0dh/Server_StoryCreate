package com.storycreate

import grails.test.mixin.TestFor
import spock.lang.*
import grails.test.mixin.Mock
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(StorySecurelyService)
@Mock([StoryContent,Story, User, UserRole, Role, StoryRole, Editor, Viewer])
class StorySecurelyServiceSpec extends Specification {

    def setup() {
		def adminUser = new User(username:"admin", password: "password").save(failOnError:true)
		def userUser = new User(username:"joe", password: "password").save(failOnError:true)
		def user2User = new User(username:"jane", password: "password").save(failOnError:true)
		def user3User = new User(username:"bill", password: "password").save(failOnError:true, flush: true)
		def user4User = new User(username:"panda",password:"password").save(failOnError:true, flush:true)
		def user5User = new User(username:"ann",password:"password").save(failOnError:true, flush:true)
		def adminRole = new Role(authority: "ROLE_admin").save(failOnError: true)
		def userRole = new Role(authority: "ROLE_user").save(failOnError: true)
		new UserRole(user: adminUser, role: adminRole).save(failOnError:true)
		new UserRole(user: userUser, role: userRole).save(failOnError:true)
		new UserRole(user: user2User, role: userRole).save(failOnError:true)
		new UserRole(user: user3User, role: userRole).save(failOnError:true, flush:true)
		def story1 = new Story(title: "The very First Story", isPublic : true, owner: adminUser, description:"The first is usually the best").save(failOnError: true)
		def story2 = new Story(title: "The Storm", isPublic : true, owner: userUser, description:"It all started on dark and stormy night").save(failOnError: true)
		def story3 = new Story(title: "Jamaica at Night", isPublic : true, owner: userUser, description:"The drums were loud....very loud").save(failOnError: true)
		def story4 = new Story(title: "My Secret", isPublic: false, owner: user2User, description:"Hidden from public eyes").save(failOnError: true, flush :true)
		def story5 = new Story(title: "My Secret2", isPublic: false, owner: user2User, description:"Hidden from public eyes").save(failOnError: true, flush :true)
		def story6 = new Story(title: "My Secret3", isPublic: false, owner: user2User, description:"Hidden from public eyes").save(failOnError: true, flush :true)
		new StoryContent(story: story1, author:adminUser, content:"apple").save(failOnError: true)
		new StoryContent(story: story1, author:adminUser, content:"banana").save(failOnError: true)
		new StoryContent(story: story1, author:userUser, content:"cranberry").save(failOnError: true)
		new StoryContent(story: story2, author:userUser, content:"asparagus").save(failOnError: true)
		new StoryContent(story: story2, author:adminUser, content:"carrot").save(failOnError: true)
		new StoryContent(story: story4, author:user2User, content:"beach").save(failOnError: true, flush: true)
		story5.addToEditors(new Editor(user: user4User)).save(failOnError:true)
		story6.addToViewers(new Viewer(user: user5User)).save(failOnError:true, flush:true)
    }

    def cleanup() {
    }
	/* Had to comment this out when I changed the list function to use hql instead of createCriteria
	 * String based queries are not supported in Unit testing
	 * String-based queries like [executeQuery] are currently not supported in this implementation of GORM
	 */
//	@Unroll
//	void "Testing List function"() {
//		given: "Mocked security functions, setting logged in user"
//		def lu = User.findByUsername(luW)
//		assert lu.username == luW
//		
//		def springSecurityService = Mock(SpringSecurityService)
//		invW * springSecurityService.getCurrentUser() >> User.findByUsername(luW)
//		service.springSecurityService = springSecurityService
//		
//		SpringSecurityUtils.metaClass.'static'.ifAllGranted = { String role ->
//			return isAdminW }
//		
//		when: "function list() is called"
//		def result = service.list()
//		
//		then: "proper number of stories are returned"
//		result.size == theResultW
//		
//		
//		where:
//		luW		|	invW 	|	isAdminW	|	theResultW
//		"joe"	|	1		|	false		|	3
//		"admin"	|	1		|	true		|	6
//	
//		"jane"	|	1		|	false		|	6			//Jane is owner of private story
//		"bill"	|	1		|	false		|	3
//		"panda" |	1		|	false		|	4     		//panda is editor of a private story
//		"ann"	|	1		|	false		| 	4			//ann is a viewer of a private story
//
//	}
@Unroll
    void "Testing Create Permission rules"() {
		
		given: "Mocked security functions, setting logged in user: #luW and story with owner: #ownW"
		def lu = User.findByUsername(luW)
		def user = User.findByUsername(ownW)
		def story = new Story(title: "Test Story", owner: user, isPublic: true, description:"yo").save(failOnError:true, flush: true)

		def springSecurityService = Mock(SpringSecurityService)
		1 * springSecurityService.getCurrentUser() >> User.findByUsername(luW) 
		service.springSecurityService = springSecurityService
		SpringSecurityUtils.metaClass.'static'.ifAllGranted = { String role ->
            return isAdminW } 

		when: "Create function is called with story"
		def result = service.create(story)
		
		then: "proper boolean value returned"
		result == theResultW

		where:
		luW		|		ownW		|	isAdminW		|	theResultW
		"joe"	|		"joe"		|		false		|		true			//
		"joe"	|		"admin"		|		false		|		false			//
		"admin"	|		"joe"		|		true		|		true			//
				
	}
	
	@Unroll
	void "Testing Update Permission rules"() {
		
		given: "Mocked security functions, setting logged in user: #luW and story with owner: #ownW"
		def lu = User.findByUsername(luW)
		def owner = User.findByUsername(ownW)
		def story = Story.findByOwner(owner)
	
		Story.metaClass.static.isEditor = {User u -> return isEditorW}

		def springSecurityService = Mock(SpringSecurityService)
		1 * springSecurityService.getCurrentUser() >> User.findByUsername(luW)
		service.springSecurityService = springSecurityService
		SpringSecurityUtils.metaClass.'static'.ifAllGranted = { String role ->
			return isAdminW }

		when: "Update function is called with story"
		def result = service.update(story)
		
		then: "proper boolean value returned"
		result == theResultW

		where:
		luW		|		ownW		|		isEditorW		|		isAdminW		|		theResultW
		"joe"	|		"joe"		|		false			|		false			|			true			//when lu is Owner of story and Author of SC
		"joe"	|		"admin"		|		true			|		false			|			true			//when lu is Editor of story and Author of SC
		"admin"	|		"joe"		|		false			|		true			|			true			//when lu is Admin and not Author of SC
		
		"joe"	|		"admin"		|		false			|		false			|			false			//when lu is Owner of story but not Author and not admin
		
	}
	
	@Unroll
	void "Testing Delete Permission rules"() {
		
		given: "Mocked security functions, setting logged in user: #luW and story with owner: #ownW"
		def lu = User.findByUsername(luW)
		def user = User.findByUsername(ownW)
		def story = Story.findByOwner(user)
		
		def springSecurityService = Mock(SpringSecurityService)
		1 * springSecurityService.getCurrentUser() >> User.findByUsername(luW)
		service.springSecurityService = springSecurityService
		SpringSecurityUtils.metaClass.'static'.ifAllGranted = { String role ->
			return isAdminW }

		when: "Create function is called with story"
		def result = service.delete(story)
		
		then: "proper boolean value returned"
		result == theResultW

		where:
		luW		|		ownW		|	isAdminW		|		theResultW
		"joe"	|		"joe"		|		false		|		true			//
		"joe"	|		"admin"		|		false		|		false			//
		"admin"	|		"joe"		|		true		|		true			//
				
	}
	
	@Unroll
	void "Testing Retrieve Permission rules"() {
		
		given: "Mocked security functions, setting logged in user: #luW and story with owner: #ownW"
		def lu = User.findByUsername(luW)
		def owner = User.findByUsername(ownW)
		def story = Story.findByOwner(owner)

		
		Story.metaClass.static.isEditor = {User u -> return isEditorW}
		Story.metaClass.static.isViewer = {User u -> return isViewerW}
		
		def springSecurityService = Mock(SpringSecurityService)
		invW * springSecurityService.getCurrentUser() >> User.findByUsername(luW)
		service.springSecurityService = springSecurityService
		
		SpringSecurityUtils.metaClass.'static'.ifAllGranted = { String role ->
			return isAdminW }
		
		when: "function retrieve() is called with sc"
		def result = service.retrieve(story)
		
		then: "proper boolean value returned"
		result == theResultW
		
		where:
		luW		|	ownW	|	invW |	isEditorW	|	isAdminW	|	isViewerW	|	theResultW
		"joe"	|	"joe"	|	0	|	false		|	false		|	false		|	true			//isPublic is true
		"joe"	|	"admin"	|	0	|	true		|	false		|	false		|	true			//isPublic is true
		"admin"	|	"joe"	|	0	|	false		|	true		|	false		|	true			//isPublic is true
	
		"jane"	|	"jane"	|	1	|	false		|	false		|	false		|	true			//isPublic is false but owner of story
		"admin"	|	"jane"	|	1	|	false		|	true		|	false		|	true			//isPublic is false but admin
		"joe"	|	"jane"	|	1	|	true		|	false		|	false		|	true			//isPublic is false but is Editor
		"joe"	|	"jane"	|	1	|	false		|	false		|	true		|	true			//isPublic is false but is Viewer
		"bill"	|	"jane"	|	1	|	false		|	false		|	false		|	false			//isPublic is false not admin, not editor, not viewer
		}

	
	
}
