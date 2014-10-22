package com.storycreate

import grails.transaction.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils

// Will check permissions to see if a storyContent can be added, viewed, deleted, or updated
// Returns boolean value
// Assumes that storyContent has already been validated by sc.validate() with no errors


@Transactional
class StoryContentSecurelyService {
	def springSecurityService
	
    def update(sc) {
		def currentUser = springSecurityService.currentUser
		def isAdmin = SpringSecurityUtils.ifAllGranted("ROLE_admin")
		def isOwner = sc.story.isOwner(currentUser)
		def isEditor = sc.story.isEditor(currentUser)
		def isAuthor = (currentUser.id == sc.author.id)
		
		// if isAdmin, then permitted.  Otherwise must be author of sc and be owner or editor of story
		if (isAdmin || (isAuthor && (isOwner || isEditor)) ){
			return true
		  // sc.save()
		} else {
			return false
		}
    }
	
	def create(sc) {
		return update(sc)
	}
	
	def delete(sc) {
		def currentUser = springSecurityService.currentUser
		def isAdmin = SpringSecurityUtils.ifAllGranted("ROLE_admin")
		def isOwner = sc.story.isOwner(currentUser)
		def isAuthor = (currentUser.id == sc.author.id)
		
		// if isAdmin, then permitted.  Otherwise must be author of sc and be owner of story
		if (isAdmin || (isAuthor && isOwner)){
			return true
		} else {
			return false
		}
	}
	
	def retrieve(sc) {
		if (! sc.story.isPublic ) {
			def currentUser = springSecurityService.currentUser
			def isAdmin = SpringSecurityUtils.ifAllGranted("ROLE_admin")
			def isOwner = sc.story.isOwner(currentUser)
			def isEditor = sc.story.isEditor(currentUser)
			def isViewer = sc.story.isViewer(currentUser)
			
			if(isAdmin || isOwner || isEditor || isViewer) {
				return true
			} else {
				return false
			}
			
		} else {
			return true
		}
		
	}
}
/* test:
 * able to save sc when isOwner and isAuthor
 * able to save sc when isEditor and isAuthor
 * able to save sc when isAdmin and not isAuthor
 * 
 * false when saving sc not isAuthor or not isAdmin
 * false when saving sc when is Author but not isOwner nor isEditor nor isAdmin*/
 