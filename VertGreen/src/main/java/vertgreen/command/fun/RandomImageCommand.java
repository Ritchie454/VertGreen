package vertgreen.command.fun;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import vertgreen.Config;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IFunCommand;
import vertgreen.util.CacheUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandomImageCommand extends Command implements IFunCommand {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RandomImageCommand.class);

    //https://regex101.com/r/0TDxsu/2
    private static final Pattern IMGUR_ALBUM = Pattern.compile("^https?://imgur\\.com/a/([a-zA-Z0-9]+)$");

    private volatile String etag = "";

    //contains the images that this class randomly serves, default entry is a "my body is not ready" gif
    private volatile String[] urls = {"http://i.imgur.com/NqyOqnj.gif"};

    public RandomImageCommand(String[] urls) {
        this.urls = urls;
    }

    public RandomImageCommand(String imgurAlbum) {
        //update the album every hour or so
        new Thread(() -> {
            boolean run = true;
            while (run) {
                populateItems(imgurAlbum);
                try {
                    Thread.sleep(1000 * 60 * 60);
                } catch (InterruptedException e) {
                    log.info("Update thread for " + imgurAlbum + " has been interrupted.");
                    run = false;
                }
            }
        }, RandomImageCommand.class.getSimpleName() + " imgur updater").start();
    }

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        sendRandomFileWithMessage(channel, null);
    }

    public void sendRandomFileWithMessage(TextChannel channel, Message message) {
        //Get a random file and send it
        String randomUrl;
        synchronized (this) {
            randomUrl = getRandomImageUrl();
        }
        try {
            channel.sendFile(CacheUtil.getImageFromURL(randomUrl), message).queue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRandomImageUrl() {
        return (String) Array.get(urls, ThreadLocalRandom.current().nextInt(urls.length));
    }

    /**
     * Updates the imgur backed images managed by this object
     */
    private void populateItems(String imgurAlbumUrl) {

        Matcher m = IMGUR_ALBUM.matcher(imgurAlbumUrl);

        if (!m.find()) {
            log.error("Not a valid imgur album url " + imgurAlbumUrl);
            return;
        }

        String albumId = m.group(1);
        HttpResponse<JsonNode> response;
        try {
            synchronized (this) {
                response = Unirest.get("https://api.imgur.com/3/album/" + albumId)
                        .header("Authorization", "Client-ID " + Config.CONFIG.getImgurClientId())
                        .header("If-None-Match", etag)
                        .asJson();
            }
        } catch (UnirestException e) {
            log.error("Imgur down? Could not fetch imgur album " + imgurAlbumUrl, e);
            return;
        }

        if (response.getStatus() == 200) {
            JSONArray images = response.getBody().getObject().getJSONObject("data").getJSONArray("images");
            List<String> imageUrls = new ArrayList<>();
            images.forEach(o -> imageUrls.add(((JSONObject) o).getString("link")));

            synchronized (this) {
                urls = imageUrls.toArray(urls);
                etag = response.getHeaders().getFirst("ETag");
            }
            log.info("Refreshed imgur album " + imgurAlbumUrl + ", new data found.");
        }
        //etag implementation: nothing has changed
        //https://api.imgur.com/performancetips
        else if (response.getStatus() == 304) {
            //nothing to do here
            log.info("Refreshed imgur album " + imgurAlbumUrl + ", no update.");
        } else {
            //some other status
            log.warn("Unexpected http status for imgur album request " + imgurAlbumUrl + ", response: " + response.getBody().toString());
        }
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Post a random image.";
    }
}
