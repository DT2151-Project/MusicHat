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
        random(
            { furhat.say("Hello there! I'm MusicHat, the music robot. Let's play music together!") },
            { furhat.say("Hey buddy! I'm MusicHat, the music robot. Let's listen to some songs!") }
        )
        goto(AskUserType)
    }



/*    onResponse {
        furhat.say("Oh, you like "+ it.text)
        runBlocking {
            val searchResults = builtAPI.trackSearch("track:"+it.text)
            val parsedResults = displayHandler.parseTrackSearchResults(searchResults)
            val firstResult = parsedResults[1]
            furhat.say("That is a song by " + firstResult[0])
            furhat.say("It is " + firstResult[2] + "minutes long.")
        }
    }*/
}
