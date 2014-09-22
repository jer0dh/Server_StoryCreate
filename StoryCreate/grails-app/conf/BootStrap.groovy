import com.storycreate.*
import grails.converters.*

class BootStrap {

    def init = { servletContext ->
		
		
		if (User.count() == 0){
			def adminUser = new User(username:"admin", password: "password").save(failOnError:true)
			def userUser = new User(username:"joe", password: "password").save(failOnError:true, flush: true)
			def adminRole = new Role(authority: "ROLE_admin").save(failOnError: true)
			def userRole = new Role(authority: "ROLE_user").save(failOnError: true)
			new UserRole(user: adminUser, role: adminRole).save(failOnError:true)
			new UserRole(user: userUser, role: userRole).save(failOnError:true, flush:true)
			new Profile( user: adminUser, email:'j.hammer@yahoo.com', bio:'I created this', fullName: 'Jerod Hammerstein')
			if (Story.count() == 0) {
				def story1 = new Story(title: "The very First Story", isPublic : true, owner: adminUser, description:"The first is usually the best").save(failOnError: true)
				def story2 = new Story(title: "The Storm", isPublic : true, owner: userUser, description:"It all started on dark and stormy night").save(failOnError: true)
				def story3 = new Story(title: "Jamaica at Night", isPublic : true, owner: userUser, description:"The drums were loud....very loud").save(failOnError: true, flush: true)
				new StoryContent(story: story1, user:adminUser, content:"Nam eleifend libero quis feugiat vehicula. Curabitur et cursus nulla. Maecenas cursus volutpat turpis at consequat. In eget facilisis leo, scelerisque rhoncus sem. Mauris pulvinar luctus fringilla. Quisque sit amet tortor viverra, commodo urna luctus, varius purus. Donec cursus faucibus felis id molestie. Maecenas eget semper nibh. Suspendisse sit amet diam eget risus tristique faucibus non a tellus. ").save(failOnError: true)
				new StoryContent(story: story1, user:adminUser, content:"Nulla id magna nec enim aliquam mollis in sit amet est. Nulla facilisi. In hac habitasse platea dictumst. Phasellus commodo tellus ligula, vel egestas quam malesuada ut. Pellentesque sollicitudin nulla sed diam consectetur, sed posuere enim vehicula. Morbi in suscipit urna. Proin sagittis lobortis nibh, vel tincidunt ligula condimentum eu. In hac habitasse platea dictumst. Duis orci urna, accumsan vitae maximus ac, elementum non nunc. Sed magna mi, dignissim sit amet eros eget, luctus scelerisque dui. Interdum et malesuada fames ac ante ipsum primis in faucibus.  ").save(failOnError: true)
				new StoryContent(story: story1, user:userUser, content:"Proin ut maximus lectus. Ut tempor euismod egestas. Fusce nec congue turpis, id dictum ex. Proin pulvinar enim tellus, et imperdiet dolor lacinia at. Proin ullamcorper eu tortor nec faucibus. Nulla et condimentum mi. Nulla odio lectus, viverra sed sapien id, eleifend lacinia ex. Suspendisse nisi felis, dapibus a euismod at, tempus vitae nisl.  ").save(failOnError: true)
				new StoryContent(story: story2, user:userUser, content:"Sed ultricies semper sodales. Aenean interdum lorem libero, nec vulputate mauris facilisis id. Phasellus in nisi nec felis blandit volutpat. Aenean nec dolor id lacus porta sagittis. Quisque dignissim blandit interdum. Aliquam erat volutpat. Sed quis quam aliquam, semper erat eget, pellentesque ante. Duis ut nibh ex. Etiam vel arcu molestie, vehicula lectus id, mattis felis. Curabitur in convallis justo. ").save(failOnError: true)
				new StoryContent(story: story2, user:adminUser, content:"Nam eleifend libero quis feugiat vehicula. Curabitur et cursus nulla. Maecenas cursus volutpat turpis at consequat. In eget facilisis leo, scelerisque rhoncus sem. Mauris pulvinar luctus fringilla. Quisque sit amet tortor viverra, commodo urna luctus, varius purus. Donec cursus faucibus felis id molestie. Maecenas eget semper nibh. Suspendisse sit amet diam eget risus tristique faucibus non a tellus. ").save(failOnError: true, flush: true)
			}
		}
		
		// Default JSON representation of Story
		JSON.registerObjectMarshaller(Story) { Story s ->
			   return [ id 				: 		s.id,
					   title			:		s.title,
					   dateCreated		:		s.dateCreated,
					   lastUpdated		:		s.lastUpdated,
					   isPublic			:		s.isPublic,
					   storyContent		:  		s.storyContent.collect {sc ->
									   [	user		 	: 	sc.user.profile?.fullName ?: sc.user.username,
										    userId			:	sc.user.id,
										   	content		 	: 	sc.content,
											dateCreated		:	sc.dateCreated,
											lastUpdated		:	sc.lastUpdated ]
									   }
					   ]
		   }

		// JSON format used for the list of stories
		JSON.createNamedConfig("storyList") { cfg ->
			 cfg.registerObjectMarshaller(Story) { Story s ->
				return [ id 			: 		s.id,
						title			:		s.title,
						owner			:		s.owner.profile?.fullName ?: s.owner.username,
						dateCreated		:		s.dateCreated,
						lastUpdated		:		s.lastUpdated,
						isPublic		:		s.isPublic,
						authors			:		s.storyContent.collect {sc-> sc.user.profile?.fullName ?: sc.user.username}.unique() ]
			}
		}

		// Default JSON representation of User
			JSON.registerObjectMarshaller(User) { User u ->
			   return [ id 				: 		u.id,
					   username			:		u.username,
					   fullName			:		u.profile?.fullName,
					   bio				:		u.profile?.bio,
					   homepage			:		u.profile?.homepage,
					   country			:		u.profile?.country  ]
		   }
		// JSON format for User for Admin users (includes email address)
		JSON.createNamedConfig("userListForAdmin") { cfg ->
			cfg.registerObjectMarshaller(User) { User u ->
			   return [ id 				: 		u.id,
					   username			:		u.username,
					   fullName			:		u.profile?.fullName,
					   email			:		u.profile?.email,
					   bio				:		u.profile?.bio,
					   homepage			:		u.profile?.homepage,
					   country			:		u.profile?.country  ]
		   }
	   }
		
    }
    def destroy = {
    }
}
