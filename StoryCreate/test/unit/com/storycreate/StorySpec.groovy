package com.storycreate

import grails.test.mixin.TestFor
import spock.lang.*
import grails.test.mixin.Mock

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Story)
@Mock([StoryContent,Story, User, UserRole, Role, StoryRole, Editor, Viewer])
class StorySpec extends Specification {
	def numOfStories = 6
	def numOfEditors = 1
	def numOfViewers = 1
	def numOfUsers = 6
	
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

    void "Number of Stories"() {
		expect:
		numOfStories == Story.count()
    }
	void "Number of Users"() {
		expect:
		numOfUsers == User.count()
	}
	void "Number of Editors"() {
		expect:
		numOfEditors == Editor.count()
	}
	void "Number of Viewers"() {
		expect:
		numOfViewers == Viewer.count()
	}
	
	void "We can retrieve Story5's Editor"(){
		when: "A story retrieved from database"
		def story = Story.findByTitle("My Secret2")
		assert story.title == "My Secret2"
		
		then: "We can list the editor"
		story.editors?.collect() {e-> e.user.username}.sort() == ['panda']
	}

	 
	@Unroll
	void "We can use isEditor to determine if user is editor of a story"() {
		
		when: "A story is retrieved from database"
		def story = Story.findByTitle("My Secret2")
		assert story.title == "My Secret2"
		def user = User.findByUsername(userW)
		assert user.username == userW
		
		then: "we can use isEditor to test if #userW is an editor"
		story.isEditor(user) == resultW
		
		where:
		userW	|	resultW
		"joe"	|	false
		"bill"	|	false
		"panda"	|	true
		"ann"	|	false
	} 
	@Unroll
	void "We can use isViewer to determine if user is viewer of a story"() {
		
		when: "A story is retrieved from database"
		def story = Story.findByTitle("My Secret3")
		assert story.title == "My Secret3"
		def user = User.findByUsername(userW)
		assert user.username == userW
		
		then: "we can use isViewer to test if #userW is a viewer"
		story.isViewer(user) == resultW
		
		where:
		userW	|	resultW
		"joe"	|	false
		"bill"	|	false
		"panda"	|	false
		"ann"	|	true
	}
}
