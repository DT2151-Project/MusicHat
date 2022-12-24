package furhatos.app.musichatskill

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.SimplePlaylist
import com.adamratzman.spotify.models.SpotifyPublicUser
import com.adamratzman.spotify.models.SpotifySearchResult
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.spotifyAppApi
import com.adamratzman.spotify.utils.Market
import kotlin.collections.ArrayList
import kotlin.random.Random

val GENRE = listOf("trip-hop", "world music", "new age", "dream pop", "classical pop", "acappella", "bossanova", "post rock", "brit pop", "dub", "electro", "jungle", "drum and base", "big beat", "break beat", "psychedelictrance", "trance", "ambient", "electrophonic", "folk", "chamber pop", "synth pop", "orchestra", "rap", "house", "R and B", "disco", "punk", "soul", "reggae", "pop", "art rock")

class SpotifyApiHandler {
    private val clientID = ""
    private val clientSecret = ""
    private var api: SpotifyAppApi? = null

    init {

    }

    fun String.removeWhitespaces() = replace(" ", "%")


    /// Pulls the developer ClientID and ClientSecret tokens provided
    /// by Spotify and builds them into an object that can easily
    /// call public Spotify APIs.
    suspend fun buildSearchApi() {
        api = spotifyAppApi(clientID, clientSecret).build()
    }

    // Performs Spotify database query for queries related to user information. Returns
    // the results as a SpotifyPublicUser object.
    suspend fun userSearch(userQuery: String): SpotifyPublicUser? {
        return api!!.users.getProfile(userQuery)
    }

    suspend fun genreSearch(searchQuery: String): MutableList<Track?> {
        println(searchQuery.removeWhitespaces())
        val playlistResult = api!!.search.searchPlaylist(searchQuery.removeWhitespaces(), 5, 1, market=Market.US)

        val trackResults = api!!.playlists.getPlaylist(playlistResult[0].uri.uri, market=Market.US)

        val trackList = ArrayList<String>()
        for (i in trackResults!!.tracks) {
            trackList.add(i.track!!.uri.uri)
        }

        val trackSuggestion: MutableList<Track?> = mutableListOf()
        for (uri in trackList.shuffled().drop(trackList.size-3)) {
            trackSuggestion.add(api!!.tracks.getTrack(uri, market=Market.US))
        }

        return trackSuggestion
    }


    // Performs Spotify database query for queries related to track information. Returns
    // the results as a SpotifySearchResult object.
    suspend fun trackSearch(searchQuery: String): Track? {
        val searchResult = api!!.search.searchTrack(searchQuery, 10, 1, market=Market.US)

        return api!!.tracks.getTrack(searchResult[0].uri.uri, market=Market.US)
    }

}