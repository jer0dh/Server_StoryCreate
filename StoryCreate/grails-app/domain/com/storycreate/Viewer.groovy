package com.storycreate

class Viewer {
	User user

	static belongsTo = [story: Story]
    static constraints = {
    }
	
	static void removeAll(User u, boolean flush = false) {
		println("viewers: ${Viewer.count()}")
		println("In Viewers.removeAll(user)")
		if (u == null) return

//			Editor.executeUpdate("delete Editor e where e.user = :userid", [userid : User.load(u.id)])
		Viewer.where {
			user == User.load(u.id)
		}.deleteAll()

		if (flush) { Viewer.withSession { it.flush() } }
		println("viewers: ${Viewer.count()}")
		println('finished Viewer.removeAll(user)')
}
}
