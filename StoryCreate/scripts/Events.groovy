eventTestPhaseStart = { args ->
	System.properties["grails.test.phase"] = args
}
println("in Events.groov:  ${args}")