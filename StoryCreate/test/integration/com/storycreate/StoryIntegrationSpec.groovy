package com.storycreate



import spock.lang.*
import org.hibernate.Criteria
/**
 *
 */
class StoryIntegrationSpec extends Specification {

    def setup() {
			println("adding data for Integration Test")
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
			new Profile( user: adminUser, email:'j.hammer@yahoo.com', bio:'I created this', fullName: 'Jerod Hammerstein').save(failOnError: true)
			new Profile (user: userUser, email:'joe@yahoo.com').save(failOnError:true, flush: true)

				def story1 = new Story(title: "Story1", isPublic : true, owner: adminUser, description:"The first is usually the best").save(failOnError: true)
				def story2 = new Story(title: "Story2", isPublic : true, owner: userUser, description:"It all started on dark and stormy night").save(failOnError: true)
				def story3 = new Story(title: "Story3", isPublic : false, owner: userUser, description:"The drums were loud....very loud").save(failOnError: true, flush: true)
				def story4 = new Story(title: "Story4", isPublic : true, owner: user2User, description: "Desc 4").save(failOnError: true, flush: true)
				new StoryContent(story: story1, author:adminUser, content:"Nam eleifend libero quis feugiat vehicula. Curabitur et cursus nulla. Maecenas cursus volutpat turpis at consequat. In eget facilisis leo, scelerisque rhoncus sem. Mauris pulvinar luctus fringilla. Quisque sit amet tortor viverra, commodo urna luctus, varius purus. Donec cursus faucibus felis id molestie. Maecenas eget semper nibh. Suspendisse sit amet diam eget risus tristique faucibus non a tellus. ").save(failOnError: true)
				new StoryContent(story: story1, author:adminUser, content:"Nulla id magna nec enim aliquam mollis in sit amet est. Nulla facilisi. In hac habitasse platea dictumst. Phasellus commodo tellus ligula, vel egestas quam malesuada ut. Pellentesque sollicitudin nulla sed diam consectetur, sed posuere enim vehicula. Morbi in suscipit urna. Proin sagittis lobortis nibh, vel tincidunt ligula condimentum eu. In hac habitasse platea dictumst. Duis orci urna, accumsan vitae maximus ac, elementum non nunc. Sed magna mi, dignissim sit amet eros eget, luctus scelerisque dui. Interdum et malesuada fames ac ante ipsum primis in faucibus.  ").save(failOnError: true)
				new StoryContent(story: story1, author:userUser, content:"Proin ut maximus lectus. Ut tempor euismod egestas. Fusce nec congue turpis, id dictum ex. Proin pulvinar enim tellus, et imperdiet dolor lacinia at. Proin ullamcorper eu tortor nec faucibus. Nulla et condimentum mi. Nulla odio lectus, viverra sed sapien id, eleifend lacinia ex. Suspendisse nisi felis, dapibus a euismod at, tempus vitae nisl.  ").save(failOnError: true)
				new StoryContent(story: story2, author:userUser, content:"Sed ultricies semper sodales. Aenean interdum lorem libero, nec vulputate mauris facilisis id. Phasellus in nisi nec felis blandit volutpat. Aenean nec dolor id lacus porta sagittis. Quisque dignissim blandit interdum. Aliquam erat volutpat. Sed quis quam aliquam, semper erat eget, pellentesque ante. Duis ut nibh ex. Etiam vel arcu molestie, vehicula lectus id, mattis felis. Curabitur in convallis justo. ").save(failOnError: true)
				new StoryContent(story: story2, author:adminUser, content:"Nam eleifend libero quis feugiat vehicula. Curabitur et cursus nulla. Maecenas cursus volutpat turpis at consequat. In eget facilisis leo, scelerisque rhoncus sem. Mauris pulvinar luctus fringilla. Quisque sit amet tortor viverra, commodo urna luctus, varius purus. Donec cursus faucibus felis id molestie. Maecenas eget semper nibh. Suspendisse sit amet diam eget risus tristique faucibus non a tellus. ").save(failOnError: true, flush: true)
				new Editor(user: userUser, story: story3).save(flush:true)
				story1.addToEditors(new Editor(user:user2User)).save(failOnError: true)
				story1.addToEditors(new Editor(user:user3User)).save(failOnError: true)
				story1.addToViewers(new Viewer(user:user3User)).save(failOnError: true, flush: true)
	
    }

    def cleanup() {
    }

    void "Testing removal of a user and editors"() {
    given: " Users from bootstrap and new Story"
	def joe = User.findByUsername('joe')
	def jane = User.findByUsername('jane')
	def bill = User.findByUsername('bill')
    def story1 = new Story(owner: joe, title:"Story1 Title", isPublic: true).save(failOnError: true)   
	def story1Id = story1.id
	
    when: "let's add some editors and then delete a user"
	def editors = Editor.list()
	println("Initial editors: ${editors*.user.username}")
    story1.addToEditors(new Editor(user: jane))
    story1.addToEditors(new Editor(user: bill))
    story1.save(failOnError: true, flush: true)
    // assert story1.editors.size() == 2
    println("before bill deleted " + story1.dump())
	editors = Editor.list()
	println("Adding editors: ${editors*.user.username}")
    bill.delete(flush: true)  
	
    then: "Should only be one editor now"
	story1.editors.clear()   //removes the proxies to Editor table since one proxy points to nonexistent bill and would produce a "UnresolvableObjectException" with hibernate
	story1.refresh()
    println("after bill deleted:" + story1.dump())
	def story2 = Story.where { id == story1Id }.get()
	println("After bill deleted editors: ${editors*.user.username}")
	println("after bill deleted new instance of story (story2):" + story2.dump())
	story2.editors.each {e -> println("Editor: ${e.id}: User ${e.user.dump()}")}
    story2.errors.errorCount == 0
    story2.editors.size() == 1
    }
	
	void "Confirming that changes from previous test are not persistant"(){
		given: "Search for story from previous test and a list of stories by Joe"
		def story1 = Story.findByTitle("Story1 Title")
		def joe = User.findByUsername("joe")
		def joeStoryCount = Story.countByOwner(joe)
		
		expect: "we hold these to be true"
		story1 == null
		joe.username == "joe"
		joeStoryCount == 2
	}
	
	void "When removing a Story, its editors should also be removed"() {
		given: "We obtain story3 and the current number of editors"
		def story3 = Story.findByTitle("Story3")
		assert story3 != null
		def editorCount = Editor.count()
		
		when: "We delete story3"
		story3.delete(flush: true)
		//story3.editors.clear() - nPE after adding back Editors.removeAll after Hibernate upgrade
		// a story3.refresh() causes error since story3 cannot be reloaded from database
		
		then: "we hold these to be true"
		Editor.count() == editorCount - 1
		println(story3.dump())
	}
	
	void "Testing adding and deleting Editors and Viewers from Story"() {
		given: " Users from bootstrap, new Story, current Editor and viewer counts"
		def joe = User.findByUsername('joe')
		def jane = User.findByUsername('jane')
		def bill = User.findByUsername('bill')
		def story1 = new Story(owner: joe, title:"Story1 Title", isPublic: true).save(failOnError: true)
		def story2 = new Story(owner: jane, title:"Story2 Title", isPublic: true).save(failOnError: true, flush:true)
		def story1Id = story1.id
		def originalEditorCount = Editor.count()
		def originalViewerCount = Viewer.count()
		
		when: "we add and delete editors and viewers"
		story1.addToEditors(new Editor(user: joe))
		story1.addToEditors(new Editor(user: jane))
		story1.addToEditors(new Editor(user: bill))
		story1.addToViewers(new Viewer(user: bill))
		story2.addToViewers(new Viewer(user: joe))
		story2.addToEditors(new Editor(user: bill))
		
		def joeEd = story1.editors.find { it.user.id == joe.id }
		story1.removeFromEditors(joeEd)
		
		then: "we hold these to be true"
		Editor.count() == originalEditorCount + 4 - 1
		Viewer.count() ==  originalViewerCount + 2
		story1.editors.size() == 2
		story1.editors.collect() {e-> e.user.username}.sort() == ['bill','jane']
		
	}
	
	void "When deleting a story, the story content should also be deleted"() {
		given: "A story with story content"
		def story = Story.findByTitle("Story1")
		assert story.title ==  "Story1"
		def numOfEditors = story.editors.size()
		def totalNumOfEditors = Editor.count()
		def numOfSC = story.storyContent.size()
		def totalNumOfSC = StoryContent.count()
		
		when: "We delete the story"
		story.delete(flush: true)
//		story.editors.clear()
//		story.storyContent.clear()
		
		then: "We hold these to be true"
		StoryContent.count() == totalNumOfSC - numOfSC
		Editor.count() == totalNumOfEditors - numOfEditors
		
	}
	
	void "Testing isEditor function"() {
		given: "Users and stories from bootstrap"

		
		when: "A story is retrieved from database"
		def story = Story.findByTitle(titleW)
		assert story.title == titleW
		def user = User.findByUsername(userW)
		assert user.username == userW
		
		then: "isEditor returns the correct value"
		story.isEditor(user) == resultW
		
		where:
		userW	|	titleW				|	resultW
		"joe"	|	"Story1"			|	false
		"jane"	|	"Story1"			|	true
		"bill"	|	"Story1"			|	true

	}
	
	void "Testing where"() {
		
		when:
		def query = Story.where {
			(isPublic == true) 
		}
		
		then:
		query.list().size() == 3
		
	}
	void "Testing where user"() {
		
		when:
		def query = Story.where {
			(owner.username == 'joe')
		}
		
		then:
		query.list().size() == 2
		
	}
	void "Testing where editor"() {
		
		when:
		def query = Story.where {
			editors {user.username == "jane"}
		}
		
		then:
		query.list().size() == 1
		
	}
	
/*	This produces org.hibernate.QueryException: duplicate alias: editors_alias1 
 *  with or without the extra parens
 */
//	void "Testing where user or editor"() {
//		
//		when:
//		def query = Story.where {
//			(owner.username == 'joe') ||
//			(editors {user.username == "jane"})
//		}
//		
//		then:
//		query.list().size() == 3
//		
//	}
	void "Testing createCriteria editor"() {
		
		when:
		def c = Story.createCriteria()
		def results = c.list {
			editors {
				user { eq('username', "jane") }
			}
		}
		
		then:
		results.size() == 1
		
	}
	void "Testing createCriteria owner"() {
		
		when:
		def c = Story.createCriteria()
		def results = c.list {
			owner {
				eq('username','joe')
			}
		}
		
		then:
		results.size() == 2
		
	}
	
//	void "Testing createCriteria owner or editor"() {
//		
//		when:
//		def c = Story.createCriteria()
//		def results = c.list {
//
//			or{
//				owner {
//					eq('username','joe')
//				}
//				editors {
//					user { eq('username', "jane") }
//				}
//			}
//		}
//		
//		then:
//		results.size() == 3
//		
//	}
//	void "Testing createCriteria owner or editor with join"() {
//		
//		when:
//		def c = Story.createCriteria()
//		def results = c.list {
//			createAlias('editors','editors',Criteria.LEFT_JOIN)
//			or{
//				owner {
//					eq('username','joe')
//				}
//				 editors { eq('user.username', "jane")}
//				
//			}
//		}
//		
//		then:
//		results.size() == 3
//		
//	}

}
