package com.storycreate

class StoryContent {
	User author
	String content
//	Date dateCreated
//	Date lastUpdated
	
	static belongsTo = [ story : Story]
	
    static constraints = {
		content nullable: false
		author nullable: false
    }
	
	static mapping = {
		content type:'text'
		autoTimestamp true
	}
}
