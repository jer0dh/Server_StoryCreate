package com.storycreate

import grails.transaction.Transactional

@Transactional
class StoryRoleService {

    public findAllByStory(Story s, int role){
		switch (role ){
			case StoryRole.EDITOR:
			  	return Editor.findAllByStory(s).collect {e -> e.user}
				break;
			case StoryRole.VIEWER:
				return Viewer.findAllByStory(s).collect {v-> v.user}
				break;
		}
		
	}
	
	public addToStoryRole(User u, Story s, int role){
		switch (role ){
			case StoryRole.EDITOR:
				 new Editor(user: u, story: s).save()
				break;
			case StoryRole.VIEWER:
				new Viewer(user: u, story: s).save()
				break;
		}
	}
	
	public removeStoryRole(User u, Story s, int role){
		if(u == null || s == null) {return false}
		switch (role ){
			case StoryRole.EDITOR:
				 Editor.where {
					user.id == u.id &&
                        story.id == s.id
				}.deleteAll()
				break;
			case StoryRole.VIEWER:
				Viewer.where {
					user.id == u.id &&
                        story.id == s.id
				}.deleteAll()
				break;
		}
	}
	
	public isStoryRole(User u, Story s, int role) {
		if(u == null || s == null) {return false}
		switch (role){
			case StoryRole.EDITOR:
				def count = Editor.where {
					user.id == u.id &&
					story.id == s.id
				}.list()
				return (count.size() > 0)
				break;
			case StoryRole.VEIWER:
				def count = Viewer.where {
					user.id == u.id &&
					story.id == s.id
				}.list()
				return (count.size() > 0)
				break;
			
		}
		
	}
	
	
}