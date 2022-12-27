package furhatos.app.musichatskill.flow.main

import furhatos.app.musichatskill.nlu.*
import furhatos.flow.kotlin.*
import furhatos.nlu.common.No
import furhatos.nlu.common.Yes
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

val AskUserType = state {
    onEntry {
        furhat.ask("Are you a casual listener, a dance maniac or a music nerd?")
    }

    onResponse<DMConfirm> {
        random(
            { furhat.say("Welcome, dance maniac!") },
            { furhat.say("Nice to meet you, dance maniac!") },
            { furhat.say("So you like to dance. I look forward to seeing your moves.") }
        )
        users.current.type = "dance maniac"
        goto(ProvideService)
    }

    onResponse<CLConfirm> {
        furhat.say("Welcome, casual listener!")
        users.current.type = "casual listener"
        goto(ProvideService)
    }

    onResponse<MNConfirm> {
        furhat.say("Welcome, music nerd!")
        users.current.type = "music nerd"
        goto(AskFurther)
    }
}

val ProvideService = state {
    onEntry {
        furhat.ask("I can recommend music, or you can tell me the name of a song and I will play it for you.")
    }

    onResponse<AskRecommendations> {
        furhat.say("Ok, let me recommend something suitable for a ${users.current.type}")
        when (users.current.type ) {
            "dance maniac" -> goto(RecommendMusic("dance"))
            "casual listener" -> goto(RecommendMusic("popular"))
            "music nerd - hiphop" -> goto(RecommendMusic("hip hop"))
            "music nerd - folk" -> goto(RecommendMusic("folk"))
            "music nerd - rock" -> goto(RecommendMusic("rock"))
            "music nerd - pop" -> goto(RecommendMusic("pop"))
            "music nerd - country" -> goto(RecommendMusic("country"))
            else -> {
                goto(RecommendMusic("popular"))
            }
        }
    }

    onResponse {
        furhat.say("Ok, I'll try to play "+ it.text)
        runBlocking {
            val searchResults = builtAPI.trackSearch("track:${it.text}")
            val parsedResults = displayHandler.parseTrackSearchResults(searchResults)
            if (parsedResults.isEmpty()) {
                furhat.say("I could not find any song by that name.")
                reentry()
            } else {
                val firstResult = parsedResults[1]
                furhat.say("Playing ${firstResult[1]} by ${firstResult[0]}")
                goto(PlayMusic)
            }
        }
    }
}

//val RecommendMusic = state {
//    onEntry {
//        runBlocking {
//            val searchResults = builtAPI.trackSearch("track:dance")
//            val parsedResults = displayHandler.parseTrackSearchResults(searchResults)
//            val randomIndex = Random.nextInt(parsedResults.size)
//            val randomResult = parsedResults[randomIndex]
//            val artist = randomResult[0]
//            val title = randomResult[1]
//            furhat.ask("How about $title by $artist?")
//        }
//    }
//
//    onResponse<Yes> {
//        furhat.say("Ok, playing the song now!")
//        goto(PlayMusic)
//    }
//
//    onResponse<No> {
//        random(
//            { furhat.say("No? Alright, I'll recommend another") },
//            { furhat.say("Fine, I'll recommend another") },
//            { furhat.say("Ok, not that one.") }
//        )
//        reentry()
//    }
//}


fun RecommendMusic(trackType: String) = state {
    onEntry {
        runBlocking {
            val searchResults = builtAPI.trackSearch("track: $trackType")
            val parsedResults = displayHandler.parseTrackSearchResults(searchResults)
            val randomIndex = Random.nextInt(parsedResults.size)
            val randomResult = parsedResults[randomIndex]
            val artist = randomResult[0]
            val title = randomResult[1]
            furhat.ask("How about $title by $artist?")
        }
    }

    onResponse<Yes> {
        furhat.say("Ok, playing the song now!")
        goto(PlayMusic)
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

val PlayMusic: State = state {
    onEntry {
        // Listen for event when song ends
        // Can dance?
        // Can lip sync?
        // When ends, do this:
        random(
            { furhat.say("Wow, what a great song!") },
            { furhat.say("That song is the bomb, man.") },
            { furhat.say("Well, I don't like that song. But you do, so good for you.") },
            { furhat.say("That was a real banger, huh?") }
        )
        goto(MoreMusic)
    }
}



val MoreMusic = state {
    onEntry {
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
        if (users.current.type=="dance maniac" || users.current.type=="casual listener"){
            goto(ProvideService)
        }else{
            goto(SameStyle)
        }
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



val AskFurther: State = state {
    onEntry {
        furhat.ask { "What genre do you like?" }
    }
    onResponse<AskForCountryMusic> {
        users.current.type = "music nerd - country"
        furhat.say("Country music, got it!")
        goto(ProvideService)
    }
    onResponse<AskForRockMusic> {
        users.current.type = "music nerd - rock"
        furhat.say("Rock music, got it!")
        goto(ProvideService)
    }
    onResponse<AskForHipHopMusic> {
        users.current.type = "music nerd - hiphop"
        furhat.say("Hip hop music, got it!")
        goto(ProvideService)
    }
    onResponse<AskForFolkMusic> {
        users.current.type = "music nerd - folk"
        furhat.say("Folk music, got it!")
        goto(ProvideService)
    }
    onResponse<AskForPopMusic> {
        users.current.type = "music nerd - pop"
        furhat.say("Pop music, got it!")
        goto(ProvideService)
    }

}

val SameStyle: State = state {
    onEntry {
        furhat.ask("Do you want the music in the same genre?")
    }
    onResponse<Yes> {
        furhat.say("Awesome!")
        goto(ProvideService)
    }

    onResponse<No> {
        furhat.say("OK")
        goto(AskFurther)
    }
}