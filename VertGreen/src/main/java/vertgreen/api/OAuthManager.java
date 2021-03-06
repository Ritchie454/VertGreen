package vertgreen.api;

import com.mashape.unirest.http.exceptions.UnirestException;
import vertgreen.VertGreen;
import vertgreen.db.EntityReader;
import vertgreen.db.EntityWriter;
import vertgreen.db.entity.UConfig;
import vertgreen.util.DiscordUtil;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.LoggerFactory;

public class OAuthManager {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(OAuthManager.class);

    private static OAuth2Client oauth = null;

    public static void start(String botToken, String secret) throws UnirestException {
        // Create OAuth2 provider
        oauth = new OAuth2Client(
                DiscordUtil.getApplicationInfo(botToken).getString("id"),
                secret,
                "https://discordapp.com/api/oauth2/token",
                "http://localhost:1337/callback"
        );
    }

    static UConfig handleCallback(String code) {
        try {
            // Request access token using a Client Credentials Grant
            TokenGrant token = oauth.grantToken(code);
            if (!token.getScope().contains("guild")
                    || !token.getScope().contains("identify")) {
                throw new RuntimeException("Got invalid OAuth2 scopes.");
            }

            return saveTokenToConfig(token);
        } catch (UnirestException ex) {
            throw new RuntimeException("Failed oauth access token grant", ex);
        }
    }

    public static UConfig ensureUnexpiredBearer(UConfig config) {
        long cur = System.currentTimeMillis() / 1000;

        try {
            if(cur + 60 > config.getBearerExpiration()){
                //Will soon expire if it hasn't already
                TokenGrant token = oauth.refreshToken(config.getRefresh());

                config = saveTokenToConfig(token);
            }
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }

        return config;
    }

    private static UConfig saveTokenToConfig(TokenGrant token){
        User user = DiscordUtil.getUserFromBearer(VertGreen.getFirstJDA(), token.getBearer());

        UConfig uconfig = EntityReader.getUConfig(user.getId());

        uconfig = uconfig == null ? new UConfig() : uconfig;

        uconfig.setBearer(token.getBearer())
                .setBearerExpiration(token.getExpirationTime())
                .setRefresh(token.getRefresh())
                .setUserId(user.getId());

        //Save to database
        EntityWriter.mergeUConfig(uconfig);

        return uconfig;
    }

}
