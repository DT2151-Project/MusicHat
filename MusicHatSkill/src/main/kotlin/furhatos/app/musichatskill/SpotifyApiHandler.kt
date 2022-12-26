package furhatos.app.musichatskill

import com.adamratzman.spotify.SpotifyAppApi
import com.adamratzman.spotify.models.*
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
        try {
            api = spotifyAppApi(clientID, clientSecret).build()
        } catch (e: Exception) {
            println("# Unable to instantiate Spotify API #")
            throw e
        }
    }

    // Performs Spotify database query for queries related to user information. Returns
    // the results as a SpotifyPublicUser object.
    suspend fun userSearch(userQuery: String): SpotifyPublicUser? {
        return api!!.users.getProfile(userQuery.removeWhitespaces())
    }

    // Find a list of songs related to a specific genre
    // Return: 3 random songs
    suspend fun genreSearch(searchQuery: String): MutableList<Track?> {
        var playlistResult: PagingObject<SimplePlaylist>? = null
        try {
            playlistResult = api!!.search.searchPlaylist(searchQuery.removeWhitespaces(), 5, 1, market=Market.US)
        } catch (e: Exception) {
            println("# Unable to find playlist from Spotify API #")
            throw e
        }

        var trackResults: Playlist? = null
        try {
            trackResults = api!!.playlists.getPlaylist(playlistResult[0].uri.uri, market=Market.US)
        } catch (e: Exception) {
            println("# Unable to retrieve playlist from Spotify API #")
            throw e
        }

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
    suspend fun trackSearch(trackQuery: String, artistQuery: String = ""): Track? {
        var searchResult: PagingObject<Track>
        var trackURI = String()
        try {
            searchResult = api!!.search.searchTrack(trackQuery.removeWhitespaces(), 25, 1, market=Market.US)

            if (artistQuery.isNotEmpty()) {
                for (i in searchResult) {
                    if (i.artists.first().name == artistQuery) {
                        trackURI = i.uri.uri
                    }
                }
            }
            if (trackURI.isEmpty()) {
                trackURI = searchResult[0].uri.uri
            }
        } catch (e: Exception) {
            println("# Unable to retrieve track from Spotify API #")

            throw e
        }

        return api!!.tracks.getTrack(trackURI, market=Market.US)
    }

    suspend fun artistRelatedSearch(searchQuery: String): List<Artist> {
        try {
            return api!!.artists.getRelatedArtists(searchQuery.removeWhitespaces())
        } catch (e: Exception){
            println("# Unable to retrieve related artists from Spotify API #")
            throw e
        }
    }

    // Find artists that have a track under the song name
    // Return: the song by 3 random artists
    suspend fun artistSongSearch(searchQuery: String): MutableList<Track> {
        var searchResult: PagingObject<Track>? = null
        try {
            searchResult = api!!.search.searchTrack(searchQuery.removeWhitespaces(), 10, 1, market=Market.US)
        } catch (e: Exception) {
            println("# Unable to retrieve track from Spotify API #")
            throw e
        }

        val artistList:MutableList<Pair<String, Track>> = mutableListOf()
        for (i in searchResult!!.items){
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