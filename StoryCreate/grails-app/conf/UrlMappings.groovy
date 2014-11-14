class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
		"/api/story"(resources:'storyRest')
		"/api/user"(resources: 'userRest')
		"/api/me" (controller: 'userRest', action:'getMe')
		"/api/storyContent"(resources: 'storyContentRest')
	}
}
