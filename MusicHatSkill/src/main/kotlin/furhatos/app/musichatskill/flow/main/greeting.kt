package furhatos.app.musichatskill.flow.main

import DisplayHandler
import furhatos.app.musichatskill.SpotifyApiHandler
import furhatos.app.musichatskill.flow.Parent
import furhatos.flow.kotlin.*
import kotlinx.coroutines.runBlocking

import furhatos.app.musichatskill.Track
import furhatos.gestures.Gestures
import furhatos.app.musichatskill.Util
import javax.sound.sampled.AudioFormat

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
}
