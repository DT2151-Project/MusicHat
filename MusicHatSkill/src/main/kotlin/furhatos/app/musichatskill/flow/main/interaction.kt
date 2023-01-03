package furhatos.app.musichatskill.flow.main

import com.adamratzman.spotify.models.AudioFeatures
import com.adamratzman.spotify.models.Track
import furhatos.app.musichatskill.Util
import furhatos.app.musichatskill.nlu.*
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes
import kotlinx.coroutines.runBlocking

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
        furhat.ask("Do you want some recommendation from me?")
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

    onResponse {
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
    var currentTrack: Track? = null;

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
            var playlistQuery = ""
            playlistQuery = when (users.current.type ) {
                UserType.DANCE_MANIAC -> "dance hits"
                UserType.CASUAL_LISTENER -> "top 100 songs"
                UserType.MUSIC_NERD -> {
                    if (users.current.currrentGenre.isNullOrEmpty())
                        "top 100 songs"
                    else
                        users.current.currrentGenre.toString()
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
            playTrack(furhat, currentTrack!!)
            goto(MoreMusic)
        }
    }

    onResponse<YesStaff> {
        users.current.listeningHabits.artists.add(currentTrack!!.artists[0])
        furhat.say("Ok, let's hear it!")
        runBlocking {
            playTrack(furhat, currentTrack!!)
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
        random(
            { furhat.say("Wow, what a great song!") },
            { furhat.say("That song is the bomb, man.") },
            { furhat.say("Well, I don't like that song. But you do, so good for you.") },
            { furhat.say("That was a real banger, huh?") }
        )
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
        users.current.currrentGenre = it.text
        furhat.say(it.text + "Great.")
        goto(ProvideService)
    }
}


val AskMusicName = state {
    onEntry {
        furhat.say("OK. Maybe I can ask you more in detail.")
        furhat.ask("Can you tell me the name of the song you like?")
    }

    onResponse {
        furhat.say( it.text + " Great.")
        users.current.currrentMusicName = it.text
        goto(AskArtistName)
    }
}

val AskArtistName = state {
    onEntry {
        furhat.ask("Can you tell me the artist of the song?")
    }

    onResponse<IDK> {
        users.current.currrentMusicArtist = ""
        furhat.say("Ok")
        goto(SearchAndPlayMusic)
    }

    onResponse {
        furhat.say(it.text + " Got it.")
        users.current.currrentMusicArtist = it.text
        goto(SearchAndPlayMusic)
    }
}

val SearchAndPlayMusic = state {
    onEntry {
        furhat.say("Great! ")

        runBlocking {

            val musicTrack = if (users.current.currrentMusicName.isNullOrEmpty()) "" else users.current.currrentMusicName.toString()
            val musicArtist = if (users.current.currrentMusicArtist.isNullOrEmpty()) "" else users.current.currrentMusicArtist.toString()

            val track = builtAPI.trackSearch("track: $musicTrack", musicArtist)
            if (track == null) {
                furhat.say("I could not find any song by that name.")
                goto(ProvideService)
            } else {
                furhat.say("Playing ${track.name} by ${track.artists[0].name}")
                users.current.listeningHabits.artists.add(track.artists[0])

                playTrack(furhat, track!!)
                goto(MoreMusic)
            }
        }
    }
}



suspend fun playTrack(furhat: Furhat, track: Track) {
    if (track.previewUrl == null) {
        println("No track previewURL !!!")
        return
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
}