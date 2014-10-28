package com.storycreate



import spock.lang.*

/**
 *
 */
class StoryIntegrationSpec extends Specification {

    def setup() {
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
		def story3 = Story.findByTitle("Jamaica at Night")
		assert story3 != null
		def editorCount = Editor.count()
		
		when: "We delete story3"
		story3.delete(flush: true)
		story3.editors.clear()
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
		def story = Story.findByTitle("The very very First Story")
		assert story.title ==  "The very very First Story"
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
}
