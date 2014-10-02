package com.storycreate

class StoryRole {
	User user
	Story story
	
    static constraints = {
    }
	
	static belongsTo = Story
}

class Viewer extends StoryRole {
	
}

class Editor extends StoryRole {
	
}