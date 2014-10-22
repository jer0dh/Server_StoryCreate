package com.storycreate
// test
class StoryRole {
	User user
	Story story
    static constraints = {
    }
	
	
	static void removeAll(Story s, boolean flush = false) {
			println("In StoryRole.reffmoveAll(story)")
			if (s == null) return
	
			StoryRole.where {
				story == Story.load(s.id)
			}.deleteAll()
	
			if (flush) { StoryRole.withSession { it.flush() } }
			println('finished StoryRole.removeAll(story')
	}
	
	static void removeAll(User u, boolean flush = false) {
		println("StoryRole: ${StoryRole.count()}")
		println("In StoryRole.remoffveAll(user)")
		if (u == null) return

		StoryRole.where {
			user == User.load(u.id)
		}.deleteAll()

		if (flush) { StoryRole.withSession { it.flush() } }
		println('finished StoryRole.removeAll(user)')
		println("StoryRole: ${StoryRole.count()}")
}

	
	static final int EDITOR = 1
	static final int VIEWER = 2
}