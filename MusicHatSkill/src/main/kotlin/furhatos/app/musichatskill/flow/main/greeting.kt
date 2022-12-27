package furhatos.app.musichatskill.flow.main

import DisplayHandler
import furhatos.app.musichatskill.SpotifyApiHandler
import furhatos.app.musichatskill.flow.Parent
import furhatos.flow.kotlin.*
import kotlinx.coroutines.runBlocking

import furhatos.app.musichatskill.Track
import furhatos.gestures.Gestures
import furhatos.app.musichatskill.Util
import furhatos.gestures.Gestures.ExpressDisgust
import kotlin.random.Random

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

    // FIND 3 SONGS THAT CORRESPOND TO A GENRE
    /*
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

    // LIST 3 DIFFERENT SONG ARTISTS FOR THE USER TO CHOOSE FROM
    onResponse {
        runBlocking{
            val songResult = builtAPI.artistSongSearch("track:"+it.text)

            furhat.say("Oh you like " + it.text)
            val suggestUtt = utterance {
                + "Here are 3 songs that fit your genre."
                + "Our first pick is by ${songResult[0]!!.artists.first().name}."
                + "If you don't like that one I have one by ${songResult[1]!!.artists.first().name}."
                + "Otherwise ${songResult[2]!!.artists.first().name} also performed this title."
            }
            furhat.say(suggestUtt)

            val selectUtt = utterance {
                + Gestures.BigSmile
                + "Which one do you want to play?"
            }

            furhat.say(selectUtt)
            //TODO: on new response play the song by the requested artist
        }
    }
    

    // FIND A SONG AND PLAY THIS ONE THROUGH FURHAT
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

            var gesture = ExpressDisgust
        }
    }
   */
}
