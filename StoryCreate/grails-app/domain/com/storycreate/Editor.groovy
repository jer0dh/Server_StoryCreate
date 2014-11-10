package com.storycreate

class Editor  { 
	User user
	
	static belongsTo = [story: Story]

    static constraints = {
    }
	
		static void removeAll(User u, boolean flush = false) {
			println("editors: ${Editor.count()}")
			println("In Editors.removeAll(user)")
			if (u == null) return
	
//			Editor.executeUpdate("delete Editor e where e.user = :userid", [userid : User.load(u.id)])
			Editor.where {
				user == User.load(u.id)
			}.deleteAll()
	
			if (flush) { Editor.withSession { it.flush() } }
			println("editors: ${Editor.count()}")
			println('finished Editor.removeAll(user)')
	}
		static void removeAll(Story s, boolean flush = false) {
			println("editors: ${Editor.count()}")
			println("In Editors.removeAll(story)")
			if (s == null) return
	
//			Editor.executeUpdate("delete Editor e where e.user = :userid", [userid : User.load(u.id)])
//			Editor.where {
//				story.id == s.id
//			}.deleteAll()
			def editors = Editor.findAllByStory(s)
			editors*.delete()
			if (flush) { Editor.withSession { it.flush() } }
			println("editors: ${Editor.count()}")
			println('finished Editor.removeAll(story)')
	}
}
