package com.storycreate

import grails.transaction.Transactional

class StoryException extends RuntimeException {
	String message
	Story story
}

@Transactional
class StoryService {
	def springSecurityService

    def save(Story story) {
		
		//if contains storyContent and not ROLE_admin - error
		def storyContent = null
		if(story.storyContent != null && story.storyContent != []){
			storyContent = story.storyContent
			story.storyContent = null
			
			// not ROLE_admin - error.
			if (!SpringSecurityUtils.ifAllGranted("ROLE_admin")) {
				respondError(405, "StoryContent should be added via /api/storyContent")
				return
			}
		}
		
		// if story owner not currentUser and currentUser not ROLE_admin - error
		def currentUser = springSecurityService.currentUser;
		if(story.owner.id != currentUser.id && !SpringSecurityUtils.ifAllGranted("ROLE_admin")){
			respondError(405,"Story owner must equal to the user logged in")
			return
		}

		if (storyContent != null){
			println("there is storycontent")
			// test to make sure user saving is only saving content with user as currentuser
			// All content must be same owner
//			if (storyContent*.user.id.unique() != [currentUser.id]){
//				respondError(405,"Cannot save storyContent as other users")
//				return
//			}
			storyContent.each {sc ->
				story.addToStoryContent(sc)
			}
		}
		story.save(flush: true)

    }
}
