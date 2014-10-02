package com.storycreate

class Story {
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
}
