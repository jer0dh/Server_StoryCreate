package com.storycreate


class Story {
	def storyRoleService
	
	String title
	Date dateCreated
	Date lastUpdated
	String description
	Boolean isPublic
	List storyContent = []
	User owner
//	List editors = []
//	List contributors = []
//	SortedSet<User> viewers
	
	static belongsTo = [owner: User]
	static hasMany = [storyContent : StoryContent] //,  viewers : User] //, editors : User, contributors : User]


    static constraints = {
		title blank: false, nullable: false
		description nullable: true, size: 1..1024
		isPublic defaultValue: true
		owner nullable: false
//		editors nullable: true
//		contributors nullable: true
//		viewers nullable: true
    }
	
	static mapping = {
		autoTimestamp true
//		viewers joinTable: [name: "viewers"]

	}
	
	// Start of methods for Roles
	
	def beforeDelete() {
		def story = this
		Story.withNewSession {
			println("beforeDelete: " + story.dump())
			Editor.removeAll(story, true)
			Viewer.removeAll(story, true)
			Author.removeAll(story, true)
		}
	}
	
	// Editors
	
	def getEditors() {
		storyRoleService.findAllByStory(this, StoryRole.EDITOR)
	}
	
	def addToEditors(User u) {
		storyRoleService.addToStoryRole(u, this, StoryRole.EDITOR)
	}
	
	def removeEditor(User u){
		storyRoleService.removeStoryRole(u, this, StoryRole.EDITOR)
	}
	
	// Viewers
	
	def getViewers() {
		storyRoleService.findAllByStory(this, StoryRole.VIEWER)
	}
	
	def addToViewers(User u) {
		storyRoleService.addToStoryRole(u, this, StoryRole.VIEWER)
	}
	
	def removeViewer(User u){
		storyRoleService.removeStoryRole(u, this, StoryRole.VIEWER)
	}
//	// Authors
//	
//	def getAuthors() {
//		storyRoleService.findAllByStory(this, StoryRole.AUTHOR)
//	}
//	
//	def addToAuthors(User u) {
//		storyRoleService.addToStoryRole(u, this, StoryRole.AUTHOR)
//	}
//	
//	def removeAuthor(User u){
//		storyRoleService.removeStoryRole(u, this, StoryRole.AUTHOR)
//	}
	//TODO add getters and setters for Viewers and Authors
}
