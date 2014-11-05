package com.storycreate

import grails.plugin.springsecurity.SpringSecurityUtils
def storySecurelyService = ctx.storySecurelyService

        if (User.count() == 0){
            def adminUser = new User(username:"admin", password: "password").save(failOnError:true)
            def userUser = new User(username:"joe", password: "password").save(failOnError:true)
            def user2User = new User(username:"jane", password: "password").save(failOnError:true)
            def user3User = new User(username:"bill", password: "password").save(failOnError:true, flush: true)
            def adminRole = new Role(authority: "ROLE_admin").save(failOnError: true)
            def userRole = new Role(authority: "ROLE_user").save(failOnError: true)
            new UserRole(user: adminUser, role: adminRole).save(failOnError:true)
            new UserRole(user: userUser, role: userRole).save(failOnError:true)
            new UserRole(user: user2User, role: userRole).save(failOnError:true)
            new UserRole(user: user3User, role: userRole).save(failOnError:true, flush:true)
            new Profile( user: adminUser, email:'j.hammer@yahoo.com', bio:'I created this', fullName: 'Jerod Hammerstein').save(failOnError: true)
            new Profile (user: userUser, email:'joe@yahoo.com').save(failOnError:true, flush: true)
            if (Story.count() == 0) {
                def story1 = new Story(title: "The very very First Story", isPublic : true, owner: adminUser, description:"The first is usually the best").save(failOnError: true)
                def story2 = new Story(title: "The Storm", isPublic : true, owner: userUser, description:"It all started on dark and stormy night").save(failOnError: true)
                def story3 = new Story(title: "Jamaica at Night", isPublic : true, owner: userUser, description:"The drums were loud....very loud").save(failOnError: true, flush: true)
                new StoryContent(story: story1, author:adminUser, content:"Nam eleifend libero quis feugiat vehicula. Curabitur et cursus nulla. Maecenas cursus volutpat turpis at consequat. In eget facilisis leo, scelerisque rhoncus sem. Mauris pulvinar luctus fringilla. Quisque sit amet tortor viverra, commodo urna luctus, varius purus. Donec cursus faucibus felis id molestie. Maecenas eget semper nibh. Suspendisse sit amet diam eget risus tristique faucibus non a tellus. ").save(failOnError: true)
                new StoryContent(story: story1, author:adminUser, content:"Nulla id magna nec enim aliquam mollis in sit amet est. Nulla facilisi. In hac habitasse platea dictumst. Phasellus commodo tellus ligula, vel egestas quam malesuada ut. Pellentesque sollicitudin nulla sed diam consectetur, sed posuere enim vehicula. Morbi in suscipit urna. Proin sagittis lobortis nibh, vel tincidunt ligula condimentum eu. In hac habitasse platea dictumst. Duis orci urna, accumsan vitae maximus ac, elementum non nunc. Sed magna mi, dignissim sit amet eros eget, luctus scelerisque dui. Interdum et malesuada fames ac ante ipsum primis in faucibus.  ").save(failOnError: true)
                new StoryContent(story: story1, author:userUser, content:"Proin ut maximus lectus. Ut tempor euismod egestas. Fusce nec congue turpis, id dictum ex. Proin pulvinar enim tellus, et imperdiet dolor lacinia at. Proin ullamcorper eu tortor nec faucibus. Nulla et condimentum mi. Nulla odio lectus, viverra sed sapien id, eleifend lacinia ex. Suspendisse nisi felis, dapibus a euismod at, tempus vitae nisl.  ").save(failOnError: true)
                new StoryContent(story: story2, author:userUser, content:"Sed ultricies semper sodales. Aenean interdum lorem libero, nec vulputate mauris facilisis id. Phasellus in nisi nec felis blandit volutpat. Aenean nec dolor id lacus porta sagittis. Quisque dignissim blandit interdum. Aliquam erat volutpat. Sed quis quam aliquam, semper erat eget, pellentesque ante. Duis ut nibh ex. Etiam vel arcu molestie, vehicula lectus id, mattis felis. Curabitur in convallis justo. ").save(failOnError: true)
                new StoryContent(story: story2, author:adminUser, content:"Nam eleifend libero quis feugiat vehicula. Curabitur et cursus nulla. Maecenas cursus volutpat turpis at consequat. In eget facilisis leo, scelerisque rhoncus sem. Mauris pulvinar luctus fringilla. Quisque sit amet tortor viverra, commodo urna luctus, varius purus. Donec cursus faucibus felis id molestie. Maecenas eget semper nibh. Suspendisse sit amet diam eget risus tristique faucibus non a tellus. ").save(failOnError: true, flush: true)
                new Editor(user: adminUser, story: story3).save(flush:true)
                story1.addToEditors(new Editor(user:user2User)).save(failOnError: true)
                story1.addToViewers(new Viewer(user:user3User)).save(failOnError: true, flush: true)
            }
        }
        
    def results
    def params = [max: 100, offset:0]
    SpringSecurityUtils.doWithAuth("joe") {
        results = storySecurelyService.list2(params)
    }
    println(results)
    