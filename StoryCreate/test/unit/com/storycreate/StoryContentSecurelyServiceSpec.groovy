package com.storycreate

import grails.test.mixin.TestFor
import spock.lang.*
import grails.test.mixin.Mock
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(StoryContentSecurelyService)
@Mock([StoryContent,Story, User, UserRole, Role, StoryRole, Editor, Viewer])
class StoryContentSecurelyServiceSpec extends Specification {

	
    def setup() {
		def adminUser = new User(username:"admin", password: "password").save(failOnError:true)
		def userUser = new User(username:"joe", password: "password").save(failOnError:true)
		def user2User = new User(username:"jane", password: "password").save(failOnError:true)
		def user3User = new User(username:"bill", password: "password").save(failOnError:true, flush: true)
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
		new StoryContent(story: story1, author:adminUser, content:"apple").save(failOnError: true)
		new StoryContent(story: story1, author:adminUser, content:"banana").save(failOnError: true)
		new StoryContent(story: story1, author:userUser, content:"cranberry").save(failOnError: true)
		new StoryContent(story: story2, author:userUser, content:"asparagus").save(failOnError: true)
		new StoryContent(story: story2, author:adminUser, content:"carrot").save(failOnError: true)
		new StoryContent(story: story4, author:user2User, content:"beach").save(failOnError: true, flush: true)
    }

    def cleanup() {
//		User.clear()
//		UserRole.clear()
//		Role.clear()
//		StoryContent.clear()
//		Story.clear()
    }
@Unroll
	void "Testing index"() {
		given: "Mocked security functions, setting logged in user:"
		def lu = User.findByUsername(luW)
		
		def springSecurityService = Mock(SpringSecurityService)
		1 * springSecurityService.getCurrentUser() >> User.findByUsername(luW)
		service.springSecurityService = springSecurityService
		SpringSecurityUtils.metaClass.'static'.ifAllGranted = { String role ->
			return isAdminW }
		when: "List function is called"
		def result = service.list()
		
		then:"the correct number of Story contents are returned"
		result.size() == theResultW
		
		where:
		luW		|		isAdminW		|		theResultW
		"joe"	|		false			|		2
		"admin"	|		true			|		6
	}
@Unroll
    void "Testing Update/Create Permission rules"() {
		
		given: "Mocked security functions, setting logged in user: #luW and story with owner: #ownW"
		def lu = User.findByUsername(luW)
		def owner = User.findByUsername(ownW)
		def story = Story.findByOwner(ownW)
		def sc = StoryContent.findByContent(scW)
	
		Story.metaClass.static.isEditor = {User u -> return isEditorW}

		def springSecurityService = Mock(SpringSecurityService)
		1 * springSecurityService.getCurrentUser() >> User.findByUsername(luW) 
		service.springSecurityService = springSecurityService
		SpringSecurityUtils.metaClass.'static'.ifAllGranted = { String role ->
            return isAdminW } 

		when: "Update function is called with sc"
		def result = service.update(sc)
		
		then: "proper boolean value returned"
		result == theResultW

		where:
		luW		|		ownW		|		isEditorW		|		isAdminW		|		scW			|		theResultW
		"joe"	|		"joe"		|		false			|		false			|		"asparagus"	|			true			//when lu is Owner of story and Author of SC
		"joe"	|		"admin"		|		true			|		false			|		"cranberry"	|			true			//when lu is Editor of story and Author of SC
		"admin"	|		"joe"		|		false			|		true			|		"cranberry"	|			true			//when lu is Admin and not Author of SC
		"joe"	|		"joe"		|		false			|		false			|		"carrot"	|			false			//when lu is Owner of story but not Author and not admin
		"joe"	|		"admin"		|		false			|		false			|		"cranberry"	|			true			//when lu is not Owner, not Editor, not Admin and is Author of SC
		
	}
	
@Unroll
	void "Testing Retrieve Permission rules"() {
		
		given: "Mocked security functions, setting logged in user: #luW and story with owner: #ownW"
		def lu = User.findByUsername(luW)
		def owner = User.findByUsername(ownW)
		def sc = StoryContent.findByContent(scW)
		
		Story.metaClass.static.isEditor = {User u -> return isEditorW}
		Story.metaClass.static.isViewer = {User u -> return isViewerW}
		
		def springSecurityService = Mock(SpringSecurityService)
		invW * springSecurityService.getCurrentUser() >> User.findByUsername(luW)
		service.springSecurityService = springSecurityService
		
		SpringSecurityUtils.metaClass.'static'.ifAllGranted = { String role ->
			return isAdminW }
		
		when: "function retrieve() is called with sc"
		def result = service.retrieve(sc)
		
		then: "proper boolean value returned"
		result == theResultW
		
		where:
		luW		|	ownW	|	invW |	isEditorW	|	isAdminW	|	isViewerW	|	scW			|	theResultW
		"joe"	|	"joe"	|	0	|	false		|	false		|	false		|	"asparagus"	|		true			//isPublic is true
		"joe"	|	"admin"	|	0	|	true		|	false		|	false		|	"cranberry"	|		true			//isPublic is true
		"admin"	|	"joe"	|	0	|	false		|	true		|	false		|	"cranberry"	|		true			//isPublic is true
	
		"jane"	|	"jane"	|	1	|	false		|	false		|	false		|	"beach"		|		true			//isPublic is false but owner of story
		"admin"	|	"jane"	|	1	|	false		|	true		|	false		|	"beach"		|		true			//isPublic is false but admin
		"joe"	|	"jane"	|	1	|	true		|	false		|	false		|	"beach"		|		true			//isPublic is false but is Editor
		"joe"	|	"jane"	|	1	|	false		|	false		|	true		|	"beach"		|		true			//isPublic is false but is Editor
		"bill"	|	"jane"	|	1	|	false		|	false		|	false		|	"beach"		|		false			//isPublic is false but is Editor
		}
	
	
	@Unroll
	void "Testing Delete Permission rules"() {
		
		given: "Mocked security functions, setting logged in user: #luW and story with owner: #ownW"
		def lu = User.findByUsername(luW)
		def owner = User.findByUsername(ownW)
		def story = Story.findByOwner(ownW)
		def sc = StoryContent.findByContent(scW)
			
		def springSecurityService = Mock(SpringSecurityService)
		invW * springSecurityService.getCurrentUser() >> User.findByUsername(luW)
		service.springSecurityService = springSecurityService
		
		SpringSecurityUtils.metaClass.'static'.ifAllGranted = { String role ->
			return isAdminW }
		
		when: "function retrieve() is called with sc"
		def result = service.delete(sc)
		
		then: "proper boolean value returned"
		result == theResultW
		
		where:
		luW		|	ownW	|	invW |	isAdminW	|	scW			|	theResultW
		"joe"	|	"joe"	|	1	|	false		|	"asparagus"	|		true			//Joe is owner of story
		"joe"	|	"admin"	|	1	|	false		|	"cranberry"	|		false			//Joe is not owner yet author
		"admin"	|	"joe"	|	1	|	true		|	"cranberry"	|		true			//admin can delite
	
		"jane"	|	"jane"	|	1	|	false		|	"beach"		|		true			//isPublic is false but owner of story
		"admin"	|	"jane"	|	1	|	true		|	"beach"		|		true			//isPublic is false but admin
		"joe"	|	"jane"	|	1	|	false		|	"beach"		|		false			//isPublic is false. Is Editor but not owner
		}
//things we tried to prevent NPE when calling mocked Story class method isEditor
// which calls an injected service 
		
		//		def mockStoryRoleService = Mock(StoryRoleService)
		//		mockStoryRoleService.isStoryRole(_) >> {true}
		//		Story.metaClass.'static'.storyRoleService = mockStoryRoleService

		//		def storyMock = mockFor(Story)
		//		storyMock.demand.isEditor{ arg1 -> return true }
		
		//		Story.metaClass.isEditor = { return true }
		
	//	THIS WORKED: 
	//	Story.metaClass.static.isEditor = {User u -> return true}
		
		



}
