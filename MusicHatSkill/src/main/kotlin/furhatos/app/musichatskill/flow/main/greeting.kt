package furhatos.app.musichatskill.flow.main

import DisplayHandler
import furhatos.app.musichatskill.SpotifyApiHandler
import furhatos.app.musichatskill.flow.Parent
import furhatos.flow.kotlin.*
import kotlinx.coroutines.runBlocking

import furhatos.app.musichatskill.Track
import furhatos.gestures.Gestures
import furhatos.app.musichatskill.Util

val builtAPI = SpotifyApiHandler()
val displayHandler = DisplayHandler()


val Greeting : State = state(Parent) {
    runBlocking {
        builtAPI.buildSearchApi()
    }
    onEntry {
        furhat.ask("What genre do you like?")
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
            val genreResult = builtAPI.genreSearch("playlist:"+it.text)

            furhat.say("Oh you like " + it.text)
            val suggestUtt = utterance {
                + "Here are 3 songs that fit your genre."
                + "Our first pick is ${genreResult[0]!!.name} by ${genreResult[0]!!.artists.first().name}."
                + "If you don't like that one I have ${genreResult[1]!!.name} by ${genreResult[1]!!.artists.first().name}."
                + "Otherwise ${genreResult[2]!!.name} by ${genreResult[2]!!.artists.first().name}."
            }
            furhat.say(suggestUtt)

            val selectUtt = utterance {
                + Gestures.BigSmile
                + "Which one do you want to play?"
            }

            furhat.say(selectUtt)
            //TODO: on new response play the requested song
        }
    }

/*
onResponse {
    runBlocking {
        val searchResults = builtAPI.trackSearch("track:"+it.text)
        val parsedResults = displayHandler.formatTrackResult(searchResults)

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
 */

}
