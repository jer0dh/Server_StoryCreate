package com.storycreate

import grails.rest.RestfulController
import grails.converters.*

class StoryRestController extends RestfulController{

	static responseFormats = ['json']
	
    StoryRestController() {
		super(Story)
	}
	
	def index(String id){
		if (id == null){
			JSON.use("storyList") {
				respond Story.list()
			} 			
		}else {
			super.index(id)
		}

	}
}
