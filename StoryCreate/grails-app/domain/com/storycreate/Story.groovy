package com.storycreate

class Story {
	String title
	Date dateCreated
	Date lastUpdated
	String description
	Boolean isPublic
	List storyContent = []
	User owner
	
	static hasMany = [storyContent : StoryContent ]
	
    static constraints = {
		title blank: false, nullable: false
		description nullable: true, size: 1..1024
		isPublic defaultValue: true
		owner nullable: false
    }
	
	static mapping = {
		autoTimestamp true
	}
}
