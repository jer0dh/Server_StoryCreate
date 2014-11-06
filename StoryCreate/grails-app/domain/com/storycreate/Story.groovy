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
	
	static belongsTo = [owner: User]
	static hasMany = [storyContent : StoryContent, viewers : Viewer, editors : Editor] //, contributors : User]


    static constraints = {
		title blank: false, nullable: false
		description nullable: true, size: 1..1024
		isPublic defaultValue: true
		owner nullable: false

    }
	
	static mapping = {
		autoTimestamp true
		storyContent cascade: "all-delete-orphan"
		editors cascade: "all-delete-orphan"
		viewers cascade: "all-delete-orphan"

	}
	
	def isOwner(User u){
		return (this.owner.id == u.id)	
	}
	def isEditor(User u){
		def thisStory = this
		def query = Editor.where {
			(story.id == thisStory.id) && (user.id == u.id )
		}
		return query.count() > 0
		
		//this did not work
		//		def ed = this.editors?.findByUser(u)
		//		return this.editors?.findByUser(u) !=  null
			
	}
	def isViewer(User u){
		def thisStory = this
		def query = Viewer.where {
			(story.id == thisStory.id) && (user.id == u.id )
		}
		return query.count() > 0
	}
	
	// Start of methods for Roles
	
//	def beforeDelete() {
//		def story = this
//		Story.withNewSession {
//			println("beforeDelete: " + story.dump())
//	//		Editor.removeAll(story, true)
//			println("Returned from Editor.removeAll in Story.beforeDelete()")
//			Viewer.removeAll(story, true)
//			println("Returned from Viewer.removeAll in Story.beforeDelete()")
//		}
//	}
	
//	// Editors
//	
//	def getEditors() {
//		storyRoleService.findAllByStory(this, StoryRole.EDITOR)
//	}
//	
//	def addToEditors(User u) {
//		storyRoleService.addToStoryRole(u, this, StoryRole.EDITOR)
//	}
//	
//	def removeEditor(User u){
//		storyRoleService.removeStoryRole(u, this, StoryRole.EDITOR)
//	}
//	
//	def removeEditors() {
//		Editor.removeAll(this,true)
//	}
//	
//	def isEditor(User u){
//		return storyRoleService.isStoryRole(u, this, StoryRole.EDITOR)
//	}
//	// Viewers
//	
//	def getViewers() {
//		storyRoleService.findAllByStory(this, StoryRole.VIEWER)
//	}
//	
//	def addToViewers(User u) {
//		storyRoleService.addToStoryRole(u, this, StoryRole.VIEWER)
//	}
//	
//	def removeViewer(User u){
//		storyRoleService.removeStoryRole(u, this, StoryRole.VIEWER)
//	}
//	
//	def removeViewers() {
//		Viewer.removeAll(this, true)
//	}
//	
//	def isViewer(User u){
//		return storyRoleService.isStoryRole(u, this, StoryRole.VIEWER)
//	}
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
