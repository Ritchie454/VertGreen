package vertgreen.command.music.info;

import com.mashape.unirest.http.exceptions.UnirestException;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.audio.queue.AudioTrackContext;
import vertgreen.audio.queue.RepeatMode;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.feature.I18n;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.LoggerFactory;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;

import java.text.MessageFormat;
import java.util.List;
import vertgreen.commandmeta.MessagingException;

public class ListCommand extends Command implements IMusicCommand {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ListCommand.class);

    private static final int PAGE_SIZE = 10;

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild);
        player.setCurrentTC(channel);
        if(player.isQueueEmpty()) {
            channel.sendMessage(I18n.get(guild).getString("npNotPlaying")).queue();
            return;
        }

        //MessageBuilder mb = new MessageBuilder();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(BotConstants.VERTRED);
        int page = 1;
        if(args.length >= 2) {
            try {
                page = Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        List<AudioTrackContext> tracks = player.getRemainingTracksOrdered();

        int maxPages = (int) Math.ceil(((double) tracks.size() - 1d)) / PAGE_SIZE + 1;

        page = Math.max(page, 1);
        page = Math.min(page, maxPages);

        int i = (page - 1) * PAGE_SIZE;
        int listEnd = (page - 1) * PAGE_SIZE + PAGE_SIZE;
        listEnd = Math.min(listEnd, player.getRemainingTracksOrdered().size());

        int numberLength = Integer.toString(listEnd).length();

        List<AudioTrackContext> sublist = tracks.subList(i, listEnd);
        String playmode = "";
        if (player.isShuffle()) {
            playmode = playmode + " | " + I18n.get(guild).getString("listShowShuffled").replace(".", "");
            if (player.getRepeatMode() == RepeatMode.OFF){
            }
        }
        if (player.getRepeatMode() == RepeatMode.SINGLE) {
            playmode = playmode + " | " + I18n.get(guild).getString("listShowRepeatSingle").replace(".", "");
        } else if (player.getRepeatMode() == RepeatMode.ALL) {
            playmode = playmode + " | " + I18n.get(guild).getString("listShowRepeatAll").replace(".", "");
        }
        eb.setFooter(playmode, "http://www.thestarbiz.com/wp-content/uploads/2016/05/icon-m.png");
        eb.setTitle("Showing current Playlist, Page: " + page + "/" + maxPages);
        String HList = "";
        for (AudioTrackContext atc : sublist) {
            String status = " ";
            if (i == 0) {
                status = player.isPlaying() ? " \\▶" : " \\\u23F8"; //Escaped play and pause emojis
                
            }

            eb.addField("[" + TextUtils.forceNDigits(i + 1, numberLength) + "]" + status + atc.getEffectiveTitle(), "Added By: " + atc.getMember().getEffectiveName(), true);
            HList = HList + "\n" + "[" + TextUtils.forceNDigits(i + 1, numberLength) + "]" + status + atc.getEffectiveTitle() + "\n" + "Added By: " + atc.getMember().getEffectiveName();
            if (i == listEnd) {
                break;
            }

            i++;
        }

        //Now add a timestamp for how much is remaining
        long t = player.getTotalRemainingMusicTimeSeconds();
        String timestamp = TextUtils.formatTime(t * 1000L);

        int numTracks = player.getRemainingTracks().size() - player.getLiveTracks().size();
        int streams = player.getLiveTracks().size();

        String desc;

        if (numTracks == 0) {
            //We are only listening to streams
            desc = MessageFormat.format(I18n.get(guild).getString(streams == 1 ? "listStreamsOnlySingle" : "listStreamsOnlyMultiple"),
                    streams, streams == 1 ?
                    I18n.get(guild).getString("streamSingular") : I18n.get(guild).getString("streamPlural"));
        } else {

            desc = MessageFormat.format(I18n.get(guild).getString(numTracks == 1 ? "listStreamsOrTracksSingle" : "listStreamsOrTracksMultiple"),
                    numTracks, numTracks == 1 ?
                    I18n.get(guild).getString("trackSingular") : I18n.get(guild).getString("trackPlural"), timestamp, streams == 0
                    ? "" : MessageFormat.format(I18n.get(guild).getString("listAsWellAsLiveStreams"), streams, streams == 1
                    ? I18n.get(guild).getString("streamSingular") : I18n.get(guild).getString("streamPlural")));
        }

        //mb.append("\n").append(desc);
        eb.addField(desc, "", true);
        channel.sendMessage(eb.build()).queue();
        try {
            String comurl = TextUtils.postToHastebin("Current Playlist\n------------------\n" + HList, true) + ".vertcurrplist";
            channel.sendMessage("If you cant see embeds, you can find the current playlist here!\n" + comurl).queue();
        }
        catch (UnirestException ex) {
            throw new MessagingException("Export Failed");
        } 
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpListCommand");
    }
}
