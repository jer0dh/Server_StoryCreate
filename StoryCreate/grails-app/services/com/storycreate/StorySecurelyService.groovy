package com.storycreate

import grails.transaction.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils

// Will check permissions to see if a Story can be added, viewed, deleted, or updated
// Returns boolean value
// Assumes that story has already been validated by sc.validate() with no errors
// does not touch StoryContent. External process should test StoryContent separately using
// the StoryContentSecurelyService

@Transactional
class StorySecurelyService {
	def springSecurityService

	// list returns a list of stories the currently logged in user can see		
	def list2(params) {
		def currentUser = springSecurityService.currentUser
		def isAdmin = SpringSecurityUtils.ifAllGranted("ROLE_admin")
		
		if(isAdmin){
			return Story.list(params)
		} else {
			def query = Story.where {
				(isPublic == true) || (owner.id == currentUser.id ) ||
				(editors { user { id == currentUser.id}}) ||
				(viewers { user {id == currentUser.id}})
			}
			return query.list(params)
			
			//Story.findAllWhere(isPublic:true, [max: params.max, offset: params.offset])
		}
	}
	def list(params) {
		def currentUser = springSecurityService.currentUser
		def isAdmin = SpringSecurityUtils.ifAllGranted("ROLE_admin")
		
		if(isAdmin){
			return Story.list()
		} else {
			def hql = "from Story as s left outer join s.editors as se left outer join s.viewers as sv where (s.isPublic = true) OR (s.owner.id = :currentUserId) OR (se.user.id= :currentUserId) OR (sv.user.id = :currentUserId)"
			def results = Story.executeQuery(hql,[currentUserId:currentUser.id])
			if(results != null) { // return only the story objects
				results = results.collect { it[0] }
			}
			return results
			
			//Story.findAllWhere(isPublic:true, [max: params.max, offset: params.offset])
		}
	}
	
    def create(story) {
		def currentUser = springSecurityService.currentUser
		def isAdmin = SpringSecurityUtils.ifAllGranted("ROLE_admin")
		def isOwner = story.isOwner(currentUser)

		if (isAdmin || isOwner){
			return true	
		} else {
			return false
		}
    }
	
	def update(story) {
		def currentUser = springSecurityService.currentUser
		def isAdmin = SpringSecurityUtils.ifAllGranted("ROLE_admin")
		def isOwner = story.isOwner(currentUser)
		def isEditor = story.isEditor(currentUser)
		
		// if isAdmin, then permitted.  Otherwise must be owner or editor of story
		if (isAdmin || isOwner || isEditor ){
			return true
		} else {
			return false
		}
    }
	
	def delete(story){
		return create(story)
	}

	def retrieve(story) {
		if (! story.isPublic ) {
			def currentUser = springSecurityService.currentUser
			def isAdmin = SpringSecurityUtils.ifAllGranted("ROLE_admin")
			def isOwner = story.isOwner(currentUser)
			def isEditor = story.isEditor(currentUser)
			def isViewer = story.isViewer(currentUser)
			
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
