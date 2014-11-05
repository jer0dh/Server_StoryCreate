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
}
