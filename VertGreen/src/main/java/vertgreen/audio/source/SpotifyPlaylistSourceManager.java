package vertgreen.audio.source;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import vertgreen.VertGreen;
import vertgreen.audio.queue.PlaylistInfo;
import vertgreen.util.SearchUtil;
import vertgreen.util.SpotifyAPIWrapper;
import org.json.JSONException;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by napster on 08.03.17.
 * <p>
 * Loads playlists from Spotify playlist links.
 *
 * @author napster
 */
public class SpotifyPlaylistSourceManager implements AudioSourceManager, PlaylistImporter {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SpotifyPlaylistSourceManager.class);

    //https://regex101.com/r/AEWyxi/3
    private static final Pattern PLAYLIST_PATTERN = Pattern.compile("https?://.*\\.spotify\\.com/user/(.*)/playlist/([^?/\\s]*)");


    @Override
    public String getSourceName() {
        return "spotify_playlist_import";
    }

    @Override
    public AudioItem loadItem(final DefaultAudioPlayerManager manager, final AudioReference ar) {

        String[] data = parse(ar.identifier);
        if (data == null) return null;
        final String spotifyUser = data[0];
        final String spotifyListId = data[1];

        final SpotifyAPIWrapper saw = SpotifyAPIWrapper.getApi();

        PlaylistInfo plData;
        try {
            plData = saw.getPlaylistDataBlocking(spotifyUser, spotifyListId);
        } catch (Exception e) {
            log.warn("Could not retrieve playlist " + spotifyListId + " of user " + spotifyUser, e);
            throw new FriendlyException("Couldn't load playlist. Either Spotify is down or the playlist does not exist.", FriendlyException.Severity.COMMON, e);
        }

        String playlistName = plData.getName();
        if (playlistName == null || "".equals(playlistName)) playlistName = "Spotify Playlist";
        int tracksTotal = plData.getTotalTracks();

        final List<AudioTrack> trackList = new ArrayList<>();
        final List<String> trackListSearchTerms;

        try {
            trackListSearchTerms = saw.getPlaylistTracksSearchTermsBlocking(spotifyUser, spotifyListId);
        } catch (Exception e) {
            log.warn("Could not retrieve tracks for playlist " + spotifyListId + " of user " + spotifyUser, e);
            throw new FriendlyException("Couldn't load playlist. Either Spotify is down or the playlist does not exist.", FriendlyException.Severity.COMMON, e);
        }
        log.info("Retrieved playlist data for " + playlistName + " from Spotify, loading up " + tracksTotal + " tracks");

        //build a task list
        List<CompletableFuture<AudioTrack>> taskList = new ArrayList<>();
        for (final String s : trackListSearchTerms) {
            //remove all punctuation
            final String query = s.replaceAll("[.,/#!$%\\^&*;:{}=\\-_`~()]", "");

            CompletableFuture<AudioTrack> f = CompletableFuture.supplyAsync(() -> searchSingleTrack(query), VertGreen.executor);
            taskList.add(f);
        }

        //build a tracklist from that task list
        for (CompletableFuture<AudioTrack> futureTrack : taskList) {
            try {
                final AudioTrack audioItem = futureTrack.get();
                if (audioItem == null) {
                    continue; //skip the track if we couldn't find it
                }
                trackList.add(audioItem);
            } catch (InterruptedException | ExecutionException e) {
                //this is fine, loop will go for the next item
            }
        }
        return new BasicAudioPlaylist(playlistName, trackList, null, true);
    }

    /**
     * Searches all available searching sources for a single track.
     * <p>
     * Will go Youtube > SoundCloud > return null
     * This could probably be moved to SearchUtil
     *
     * @param query Term that shall be searched
     * @return An AudioTrack likely corresponding to the query term or null.
     */
    private AudioTrack searchSingleTrack(final String query) {
        boolean gotYoutubeResult = true;
        AudioPlaylist list = null;
        try {
            list = SearchUtil.searchForTracks(SearchUtil.SearchProvider.YOUTUBE, query, 60000);
            if (list == null || list.getTracks().size() == 0) {
                gotYoutubeResult = false;
            }
        } catch (final JSONException e) {
            log.debug("YouTube search exception", e);
            gotYoutubeResult = false;
        }

        //got a result from youtube? return it
        if (gotYoutubeResult)
            return list.getTracks().get(0);


        //continue looking for the track on SoundCloud
        try {
            list = SearchUtil.searchForTracks(SearchUtil.SearchProvider.SOUNDCLOUD, query, 60000);
        } catch (final JSONException e) {
            log.debug("SoundCloud search exception", e);
        }

        //didn't find anything, or youtube & soundcloud not available
        if (list == null || list.getTracks().size() == 0) {
            return null;
        }

        //pick topmost result, and hope it's what the user wants to listen to
        //having users pick tracks like they can do for individual searches would be ridiculous for playlists with
        //dozens of tracks. youtube search is probably good enough for this
        //
        //testcase:   Rammstein playlists; high quality Rammstein vids are really rare on Youtube.
        //            https://open.spotify.com/user/11174036433/playlist/0ePRMvD3Dn3zG31A8y64xX
        //result:     lots of low quality (covers, pitched up/down, etc) tracks loaded.
        //conclusion: there's room for improvement to this whole method
        return list.getTracks().get(0);

    }

    @Override
    public boolean isTrackEncodable(final AudioTrack track) {
        return false;
    }

    @Override
    public void encodeTrack(final AudioTrack track, final DataOutput output) throws IOException {
        throw new UnsupportedOperationException("This source manager is only for loading playlists");
    }

    @Override
    public AudioTrack decodeTrack(final AudioTrackInfo trackInfo, final DataInput input) throws IOException {
        throw new UnsupportedOperationException("This source manager is only for loading playlists");
    }

    @Override
    public void shutdown() {

    }

    /**
     * @return null or a string array containing spotifyUser at [0] and playlistId at [1] of the requested playlist
     */
    private String[] parse(String identifier) {
        String[] result = new String[2];
        final Matcher m = PLAYLIST_PATTERN.matcher(identifier);

        if (!m.find()) {
            return null;
        }

        result[0] = m.group(1);
        result[1] = m.group(2);

        log.debug("matched spotify playlist link. user: " + result[0] + ", listId: " + result[1]);
        return result;
    }

    @Override
    public PlaylistInfo getPlaylistDataBlocking(String identifier) {

        String[] data = parse(identifier);
        if (data == null) return null;
        final String spotifyUser = data[0];
        final String spotifyListId = data[1];

        try {
            return SpotifyAPIWrapper.getApi().getPlaylistDataBlocking(spotifyUser, spotifyListId);
        } catch (Exception e) {
            log.warn("Could not retrieve playlist " + spotifyListId + " of user " + spotifyUser, e);
            throw new FriendlyException("Couldn't load playlist. Either Spotify is down or the playlist does not exist.", FriendlyException.Severity.COMMON, e);
        }
    }
}
