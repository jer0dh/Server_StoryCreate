package com.storycreate



import spock.lang.*
import grails.plugin.springsecurity.SpringSecurityUtils

/**
 *
 */
class StorySecurelyServiceIntegrationSpec extends Specification {
	def storySecurelyService
	
	 void setup() {


	   def adminUser = new User(username:"admin", password: "password").save(failOnError:true)
	   def userUser = new User(username:"joe", password: "password").save(failOnError:true)
	   def user2User = new User(username:"jane", password: "password").save(failOnError:true)
	   def user3User = new User(username:"bill", password: "password").save(failOnError:true, flush: true)
	   def user4User = new User(username:"panda", password: "password").save(failOnError:true, flush: true)
	   def user5User = new User(username:"jack", password: "password").save(failOnError:true, flush: true)
		def adminRole = new Role(authority: "ROLE_admin").save(failOnError: true)
	   def userRole = new Role(authority: "ROLE_user").save(failOnError: true)
	   new UserRole(user: adminUser, role: adminRole).save(failOnError:true)
	   new UserRole(user: userUser, role: userRole).save(failOnError:true)
	   new UserRole(user: user2User, role: userRole).save(failOnError:true)
	   new UserRole(user: user3User, role: userRole).save(failOnError:true, flush:true)
	   new Profile( user: adminUser, email:'j.hammer@yahoo.com', bio:'I created this', fullName: 'Jerod Hammerstein').save(failOnError: true)
	   new Profile (user: userUser, email:'joe@yahoo.com').save(failOnError:true, flush: true)

	   def story1 = new Story(title: "Story1", isPublic : true, owner: adminUser, description:"The first is usually the best").save(failOnError: true)
	   def story2 = new Story(title: "Story2", isPublic : true, owner: userUser, description:"It all started on dark and stormy night").save(failOnError: true)
	   def story3 = new Story(title: "Story3", isPublic : true, owner: userUser, description:"The drums were loud....very loud").save(failOnError: true, flush: true)
	   def story4 = new Story(title: "Story4", isPublic : false, owner: userUser, description:"Description for story 4").save(failOnError: true, flush: true)
	   def story5 = new Story(title: "Story5", isPublic : false, owner: user2User, description:"Description for story 5").save(failOnError: true, flush: true)
	   new StoryContent(story: story1, author:adminUser, content:"StoryContent1 Nam eleifend libero quis feugiat vehicula. ").save(failOnError: true)
	   new StoryContent(story: story1, author:adminUser, content:"StoryContent2 ulla id magna nec enim aliquam mollis in sit amet est. ").save(failOnError: true)
	   new StoryContent(story: story1, author:userUser, content:"StoryContent3 Proin ut maximus lectus.").save(failOnError: true)
	   new StoryContent(story: story2, author:userUser, content:"Sed ultricies semper sodales. Aenean interdum lorem libero, nec vulputate mauris facilisis id. Phasellus in nisi nec felis blandit volutpat. Aenean nec dolor id lacus porta sagittis. Quisque dignissim blandit interdum. Aliquam erat volutpat. Sed quis quam aliquam, semper erat eget, pellentesque ante. Duis ut nibh ex. Etiam vel arcu molestie, vehicula lectus id, mattis felis. Curabitur in convallis justo. ").save(failOnError: true)
	   new StoryContent(story: story2, author:adminUser, content:"Nam eleifend libero quis feugiat vehicula. Curabitur et cursus nulla. Maecenas cursus volutpat turpis at consequat. In eget facilisis leo, scelerisque rhoncus sem. Mauris pulvinar luctus fringilla. Quisque sit amet tortor viverra, commodo urna luctus, varius purus. Donec cursus faucibus felis id molestie. Maecenas eget semper nibh. Suspendisse sit amet diam eget risus tristique faucibus non a tellus. ").save(failOnError: true, flush: true)

	   story4.addToEditors(new Editor(user:user4User)).save(failOnError: true)
	   story5.addToViewers(new Viewer(user:user3User)).save(failOnError: true, flush: true)
	   story4.addToViewers(new Viewer(user:user4User)).save(failOnError: true, flush: true)

    }

    def cleanup() {
    }

    void "Testing LU as Joe"() {
		when: "Listing stories as Joe" 
			def results
			def stories
			def params 

		    SpringSecurityUtils.doWithAuth("joe") {
		        results = storySecurelyService.list(params)
		    }
			println(results*.title)
			
		then: ""
			results.size() == 4
			results*.title == ["Story1", "Story2", "Story3", "Story4"]

    }
	void "Testing LU as Jane"() {
		when: "Listing stories as Joe"
			def results
			def stories
			def params

			SpringSecurityUtils.doWithAuth("jane") {
				results = storySecurelyService.list(params)
			}
			println(results*.title)
			
		then: ""
			results.size() == 4
			results*.title == ["Story1", "Story2", "Story3", "Story5"]

	}
	
	void "Testing LU as Bill"() {
		when: "Listing stories as Joe"
			def results
			def stories
			def params

			SpringSecurityUtils.doWithAuth("bill") {
				results = storySecurelyService.list(params)
			}
			println(results*.title)
			
		then: ""
			results.size() == 4
			results*.title == ["Story1", "Story2", "Story3", "Story5"]

	}
	void "Testing LU as Jack"() {
		when: "Listing stories as Joe"
			def results
			def stories
			def params

			SpringSecurityUtils.doWithAuth("jack") {
				results = storySecurelyService.list(params)
			}

			println(results*.title)
			
		then: ""
			results.size() == 3
			results*.title == ["Story1", "Story2", "Story3"]

	}
	void "Testing LU as admin"() {
		when: "Listing stories as Joe"
			def results
			def stories
			def params

			SpringSecurityUtils.doWithAuth("admin") {
				results = storySecurelyService.list(params)
			}

			println(results*.title)
			
		then: ""
			results.size() == 5
			results*.title == ["Story1", "Story2", "Story3", "Story4", "Story5"]

	}
}
