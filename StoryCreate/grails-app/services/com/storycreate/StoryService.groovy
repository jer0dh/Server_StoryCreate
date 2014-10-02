package com.storycreate

import grails.transaction.Transactional

class StoryException extends RuntimeException {
	String message
	Story story
}

@Transactional
class StoryService {

    def serviceMethod() {

    }
}
