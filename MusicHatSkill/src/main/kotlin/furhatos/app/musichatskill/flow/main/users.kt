package furhatos.app.musichatskill.flow.main

import com.adamratzman.spotify.models.SimpleArtist
import furhatos.flow.kotlin.UserDataDelegate
import furhatos.records.User

enum class UserType(val asString: String) {
    DANCE_MANIAC("dance maniac"),
    CASUAL_LISTENER("casual listener"),
    MUSIC_NERD("music nerd")
}
class ListeningHabits(
    var artists: MutableSet<SimpleArtist> = mutableSetOf<SimpleArtist>()
)

var User.type : UserType? by UserDataDelegate()
val User.listeningHabits : ListeningHabits
    get() = data.getOrPut(ListeningHabits::class.qualifiedName, ListeningHabits())