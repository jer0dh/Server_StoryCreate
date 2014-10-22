package com.storycreate



import spock.lang.*

/**
 *
 */
class UserIntegrationSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
		given: "Users created from bootstrap"
		def adminUser = User.where { username == "admin" }.get()
		def userUser = User.where { username == "joe" }.get()
		def adminRole = Role.findByAuthority("ROLE_admin")
		
		expect: "getting their Role"
		adminUser.authorities.contains {adminRole}
    }
}
