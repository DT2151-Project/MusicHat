package furhatos.app.musichatskill.flow.main

import com.adamratzman.spotify.models.AudioFeatures
import com.adamratzman.spotify.models.Track
import furhatos.app.musichatskill.Util
import furhatos.app.musichatskill.nlu.*
import furhatos.flow.kotlin.*
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes
import kotlinx.coroutines.runBlocking

var songPlayedSuccessfully = false

val AskUserType:State = state {
    onEntry {
        furhat.ask("Are you a casual listener, a dance maniac or a music nerd?")
    }

    onResponse<DMConfirm> {
        random(
            { furhat.say("Welcome, dance maniac!") },
            { furhat.say("Nice to meet you, dance maniac!") },
            { furhat.say("So you like to dance. I look forward to seeing your moves.") }
        )
        users.current.type = UserType.DANCE_MANIAC
        goto(ProvideService)
    }

    onResponse<CLConfirm> {
        furhat.say("Welcome, casual listener!")
        users.current.type = UserType.CASUAL_LISTENER
        goto(ProvideService)
    }

    onResponse<MNConfirm> {
        furhat.say("Welcome, music nerd!")
        users.current.type = UserType.MUSIC_NERD
        goto(AskGenre)
    }
}


val ProvideService:State = state {
    onEntry {
        furhat.say("I can recommend a song, or you could tell me the name of a song you want to hear.")
        furhat.ask("Would you like me to recommend a song?")
    }

    onResponse<Yes> {
        if (users.current.type != null) {
            furhat.say("Ok, let me recommend something suitable for a ${users.current.type!!.asString}")
        } else {
            furhat.say("Ok, let me recommend something")
        }
        goto(RecommendMusic)
    }

    onResponse<AskRecommendations> {
        if (users.current.type != null) {
            furhat.say("Ok, let me recommend something suitable for a ${users.current.type!!.asString}")
        } else {
            furhat.say("Ok, let me recommend something")
        }
        goto(RecommendMusic)
    }

    onResponse<No> {
        goto(AskMusicName)
    }
}

//val ProvideService:State = state {
//    onEntry {
//        furhat.ask("I can recommend music, or you can tell me the name of a song and I will play it for you.")
//    }
//
//    onResponse<AskRecommendations> {
//        if (users.current.type != null) {
//            furhat.say("Ok, let me recommend something suitable for a ${users.current.type!!.asString}")
//        } else {
//            furhat.say("Ok, let me recommend something")
//        }
//        goto(RecommendMusic)
//    }
//
//    onResponse {
//        furhat.say("Ok, I'll try to play "+ it.text)
//        runBlocking {
//            val track = builtAPI.trackSearch("track:${it.text}")
//            if (track == null) {
//                furhat.say("I could not find any song by that name.")
//                reentry()
//            } else {
//                furhat.say("Playing ${track.name} by ${track.artists[0].name}")
//                users.current.listeningHabits.artists.add(track.artists[0])
//                playTrack(furhat, track)
//                goto(MoreMusic)
//            }
//        }
//    }
//}


val RecommendMusic:State = state {
    var currentTrack: Track? = null

    onEntry {
        if (users.current.listeningHabits.artists.isNotEmpty()) {
            runBlocking {
                val someArtist = users.current.listeningHabits.artists.random()
                val relatedTrack = builtAPI.relatedTrackSearch(someArtist.id)
                if (relatedTrack != null) {
                    currentTrack = relatedTrack
                    furhat.say("I found a song by an artist similar to ${someArtist.name}")
                    furhat.ask("How about ${relatedTrack.name} by ${relatedTrack.artists[0].name}?")
                }
            }
        }

        runBlocking {
            val playlistQuery: String = when (users.current.type ) {
                UserType.DANCE_MANIAC -> "dance hits"
                UserType.CASUAL_LISTENER -> "top 100 songs"
                UserType.MUSIC_NERD -> {
                    if (users.current.currentGenre.isNullOrEmpty())
                        "top 100 songs"
                    else
                        users.current.currentGenre.toString()
                }
                else -> "top 100 songs"
            }

            val track = builtAPI.genreSearch("playlist:${playlistQuery}").random()
            if (track == null) {
                furhat.say("Hold on just a minute, I'll try to find you something..")
                reentry()
            } else {
                currentTrack = track
                furhat.ask("How about ${track.name} by ${track.artists[0].name}?")
            }
        }
    }

    onResponse<Yes> {
        users.current.listeningHabits.artists.add(currentTrack!!.artists[0])
        furhat.say("Ok, let's hear it!")
        runBlocking {
            songPlayedSuccessfully = playTrack(furhat, currentTrack!!)
            if (!songPlayedSuccessfully) {
                furhat.say("Sorry, I was unable to play that song.")
            }
            goto(MoreMusic)
        }
    }

    onResponse<YesStaff> {
        users.current.listeningHabits.artists.add(currentTrack!!.artists[0])
        furhat.say("Ok, let's hear it!")
        runBlocking {
            songPlayedSuccessfully = playTrack(furhat, currentTrack!!)
            if (!songPlayedSuccessfully) {
                furhat.say("Sorry, I was unable to play that song.")
            }
            goto(MoreMusic)
        }
    }


    onResponse<No> {
        random(
            { furhat.say("No? Alright, I'll recommend another") },
            { furhat.say("Fine, I'll recommend another") },
            { furhat.say("Ok, not that one.") }
        )
        reentry()
    }
}



val MoreMusic:State = state {
    onEntry {
        if (songPlayedSuccessfully) {
            random(
                { furhat.say("Wow, what a great song!") },
                { furhat.say("That song is the bomb, man.") },
                { furhat.say("Well, I don't like that song. But you do, so good for you.") },
                { furhat.say("That was a real banger, huh?") }
            )
            songPlayedSuccessfully = false
        }
        random(
            { furhat.ask("Wanna hear another one?") },
            { furhat.ask("Would you like me to play something else?") },
            { furhat.ask("Shall we listen to another song?") },
            { furhat.ask("How about some more?") }
        )
    }

    onResponse<Yes> {
        random(
            { furhat.say("Awesome") },
            { furhat.say("Great") },
            { furhat.say("Alright") }
        )

        if (users.current.type == UserType.MUSIC_NERD)
            goto(ConfirmMusicGenre)
        else
            goto(ProvideService)
    }

    onResponse<No> {
        random(
            { furhat.ask("Alright. It was really nice to enjoy music with you. Have a great day!") },
            { furhat.ask("Ok then. It was fun listening to music with you. Take care!") },
            { furhat.ask("Alright. Come back when you want to enjoy some more music. Bye!") },
            { furhat.ask("Ok, thanks for listening. See you later!") }
        )
        goto(Idle)
    }

}

val ConfirmMusicGenre: State = state {
    onEntry {
        furhat.ask("Do you want music in the same genre? ")
    }

    onResponse<Yes> {
        goto(ProvideService)
    }

    onResponse<No>{
        goto(AskGenre)
    }
}

val AskGenre: State = state {
    onEntry {
        furhat.ask( "What kind of genre do you like?")
    }

    onResponse {
        users.current.currentGenre = it.text
        furhat.say("Ok, you like the genre " + it.text)
        goto(ProvideService)
    }
}


val AskMusicName = state {
    onEntry {
        furhat.ask("Alright. Can you tell me the name of the song you like?")
    }

    onResponse {
        furhat.say( "Ok, you like " + it.text)
        users.current.currentTrackName = it.text
        goto(AskArtistName)
    }
}

val AskArtistName = state {
    onEntry {
        furhat.ask("What is the name of the artist of this song?")
    }

    onResponse<IDK> {
        users.current.currentArtistName = ""
        furhat.say("Ok, you don't know. I'll try to play " + users.current.currentTrackName)
        goto(SearchAndPlayMusic)
    }

    onResponse {
        furhat.say("Ok, I'll try to play " + users.current.currentTrackName + " by " + it.text)
        users.current.currentArtistName = it.text
        goto(SearchAndPlayMusic)
    }
}

val SearchAndPlayMusic = state {
    onEntry {
        runBlocking {

            val musicTrack = if (users.current.currentTrackName.isNullOrEmpty()) "" else users.current.currentTrackName.toString()
            val musicArtist = if (users.current.currentArtistName.isNullOrEmpty()) "" else users.current.currentArtistName.toString()

            val track = builtAPI.trackSearch("track:$musicTrack", musicArtist)
            if (track == null) {
                furhat.say("Sorry, I could not find that song.")
                goto(ProvideService)
            } else {
                furhat.say("Playing ${track.name} by ${track.artists[0].name}")
                users.current.listeningHabits.artists.add(track.artists[0])
                playTrack(furhat, track)
                goto(MoreMusic)
            }
        }
    }
}



suspend fun playTrack(furhat: Furhat, track: Track): Boolean {
    if (track.previewUrl == null) {
        println("No track previewURL !!!")
        return false
    }
    Util.convertURLtoWAV(track.previewUrl!!, track.name)

    val audioUtt = utterance {
        + Audio(Util.filePathToURL(track.name).toString(), "SONG PREVIEW", speech=true)
    }

    furhat.say(audioUtt, async = true)

    val t = System.currentTimeMillis()
    val end = t + 30000

    // THE BORING HEAD NOD
    val audioFeatures: AudioFeatures = builtAPI.getAudioFeatures(track.id)
    var interval = (60.0 / audioFeatures.tempo) * 1000
    while (interval < 500) {
        interval *= 2
    }
//    while (System.currentTimeMillis() < end) {
//        furhat.gesture(Gestures.Nod)
//        furhat.gesture(Gestures.BigSmile)
//        Thread.sleep(interval.toLong())
//    }


    // THE BEST DANCE IN THE WORLD
    while (System.currentTimeMillis() < end) {
        furhat.gesture(gestureList.random())
        Thread.sleep(1000)
    }
    return true
}