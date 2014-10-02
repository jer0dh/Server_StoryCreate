package com.storycreate



import spock.lang.*

/**
 *
 */
class UserRoleIntegrationSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
		given: "Users and Roles defined"
		def user1 = new User(username:"joe2", password: "password").save(failOnError: true)
		def user2 = new User(username:"jane2", password: "password").save(failOnError: true)
		def user3 = new User(username:"bill2", password: "password").save(failOnError: true, flush: true)
		
		def role1 = new Role(authority: "admin").save(failOnError: true)
		def role2 = new Role(authority: "user").save(failOnError: true, flush: true)
		
		when: "userRole created and then user deleted"
		def userRole1 = new UserRole(user: user3, role: role1).save(failOnError: true, flush: true)
		assert User.count() == 7
		assert UserRole.count() == 4
		UserRole.removeAll(user3)
		user3.delete(flush: true)
		
		then: "we should get the following"
		User.count() == 6
		UserRole.count() == 3
		
    }
}
