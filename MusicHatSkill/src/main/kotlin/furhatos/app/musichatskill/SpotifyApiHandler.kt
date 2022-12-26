package furhatos.app.musichatskill

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.SimplePlaylist
import com.adamratzman.spotify.models.SpotifyPublicUser
import com.adamratzman.spotify.models.SpotifySearchResult
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.spotifyAppApi
import com.adamratzman.spotify.utils.Market
import kotlin.collections.ArrayList

class SpotifyApiHandler {
    private val clientID = ""
    private val clientSecret = ""
    private var api: SpotifyAppApi? = null

    init {

    }

    private fun String.removeWhitespaces() = replace(" ", "%")


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

    // Find a list of songs related to a specific genre
    // Return: 3 random songs
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

    // Find a track
    // Return: single track
    suspend fun trackSearch(searchQuery: String): Track? {
        val searchResult = api!!.search.searchTrack(searchQuery.removeWhitespaces(), 10, 1, market=Market.US)

        return api!!.tracks.getTrack(searchResult[0].uri.uri, market=Market.US)
    }

    // Find artists that have a track under the song name
    // Return: the song by 3 random artists
    suspend fun artistSongSearch(searchQuery: String): MutableList<Track> {
        val searchResult = api!!.search.searchTrack(searchQuery.removeWhitespaces(), 10, 1, market=Market.US)


        val artistList:MutableList<Pair<String, Track>> = mutableListOf()
        for (i in searchResult.items){
            var item = Pair(i.artists.first().name, i)

            if (artistList.all { it.first != item.first }) {
                artistList.add(item)
            }
        }

        val artistSuggestion: MutableList<Track> = mutableListOf()
        for (t in artistList.shuffled().drop(artistList.size-3)) {
            println(t.first)
            artistSuggestion.add(t.second)
        }

        return artistSuggestion
    }

}