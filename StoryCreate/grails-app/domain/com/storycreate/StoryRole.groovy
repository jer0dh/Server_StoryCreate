package com.storycreate

class StoryRole {
	User user
	Story story

    static constraints = {
    }
	
	static void removeAll(Story s, boolean flush = false) {
			if (s == null) return
	
			StoryRole.where {
				story == Story.load(s.id)
			}.deleteAll()
	
			if (flush) { StoryRole.withSession { it.flush() } }
	}
	
	static void removeAll(User u, boolean flush = false) {
		if (u == null) return

		StoryRole.where {
			user == User.load(u.id)
		}.deleteAll()

		if (flush) { StoryRole.withSession { it.flush() } }
}
	
	
	static final int EDITOR = 1
	static final int VIEWER = 2
	static final int AUTHOR = 3
}