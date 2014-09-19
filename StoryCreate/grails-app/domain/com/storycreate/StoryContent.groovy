package com.storycreate

class StoryContent {
	User user
	String content
	Date dateCreated
	Date lastUpdated
	
	static belongsTo = [ story : Story]
	
    static constraints = {
		content nullable: false
		user nullable: false
    }
	
	static mapping = {
		content type:'text'
		autoTimestamp true
	}
}
