package furhatos.app.musichatskill.flow.main

import DisplayHandler
import furhatos.app.musichatskill.SpotifyApiHandler
import furhatos.app.musichatskill.flow.Parent
import furhatos.flow.kotlin.*
import kotlinx.coroutines.runBlocking

val builtAPI = SpotifyApiHandler()
val displayHandler = DisplayHandler()


val Greeting : State = state(Parent) {
    runBlocking {
        builtAPI.buildSearchApi()
    }
    onEntry {
        furhat.ask("What song do you like?")
    }
/*

    onResponse<Yes> {
        furhat.say("")
    }

    onResponse<No> {
        furhat.say("No poop.")
    }
*/

    onResponse {
        furhat.say("Oh, you like "+ it.text)
        runBlocking {
            val searchResults = builtAPI.trackSearch("track:"+it.text)
            val parsedResults = displayHandler.parseTrackSearchResults(searchResults)
            val firstResult = parsedResults[1]
            furhat.say("That is a song by " + firstResult[0])
            furhat.say("It is " + firstResult[2] + "minutes long.")
        }
    }
}
