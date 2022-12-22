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
        runBlocking {
            val searchResults = builtAPI.trackSearch("track:"+it.text)
            val parsedResults = displayHandler.parsePopularTrackResult(searchResults)

            Util.convertURLtoWAV(parsedResults[Track.PREVIEW.value],parsedResults[Track.NAME.value])

            furhat.say("Oh, you like " + parsedResults[Track.NAME.value])
            val artistUtt = utterance {
                + "That is a song by "
                + parsedResults[Track.ARTIST.value]
                + Gestures.Wink
            }
            furhat.say(artistUtt)
            val previewUtt = utterance {
                + "It is "
                + parsedResults[Track.DURATION.value]
                + " long. Here is a preview!"
                + Gestures.BigSmile
            }
            furhat.say(previewUtt)

            furhat.say{
                + Audio(Util.filePathToURL(parsedResults[Track.NAME.value]).toString(), "SONG PREVIEW", speech=true)
            }
        }
    }
}
