package com.storycreate



import spock.lang.*
import grails.plugin.springsecurity.SpringSecurityUtils

/**
 *
 */
class StorySecurelyServiceSpec extends Specification {
	def storySecurelyService
	
    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
		when: "def results"
		def results
		def params = [max: 100, offset:0]
		SpringSecurityUtils.doWithAuth("joe") {
			results = storySecurelyService.list2(params)
		}
		
		then: "Number of Stories should be"
		results == null
		results.size() == 3
    }
}
