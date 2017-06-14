package vertgreen.command.maintenance;

import vertgreen.VertGreen;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.CommandManager;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import vertgreen.feature.I18n;
import vertgreen.util.DiscordUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;

import java.text.MessageFormat;

public class ShardsCommand extends Command implements IMaintenanceCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        long totalSecs = (System.currentTimeMillis() - VertGreen.START_TIME) / 1000;
        int days = (int) (totalSecs / (60 * 60 * 24));
        int hours = (int) ((totalSecs / (60 * 60)) % 24);
        int mins = (int) ((totalSecs / 60) % 60);
        int secs = (int) (totalSecs % 60);
        
        String str = MessageFormat.format(
                I18n.get(guild).getString("statsParagraph"),
                days, hours, mins, secs, CommandManager.commandsExecuted - 1)
                + "\n";
        EmbedBuilder eb = new EmbedBuilder();
        if(DiscordUtil.isMusicBot()){
            eb.addField("Shard Info","Sharding: " + VertGreen.getInstance(guild.getJDA()).getShardInfo().getShardString() + "\n" + "Players playing: " + PlayerRegistry.getPlayingPlayers().size() + "\n" + "Known servers: " + VertGreen.getAllGuilds().size() + "\n" + "Known users in servers: " + VertGreen.getAllUsersAsMap().size() + "\n" , true);
        }
        else {
            eb.addField("Shard Info","Sharding: " + VertGreen.getInstance(guild.getJDA()).getShardInfo().getShardString() + "\n" + "Known servers: " + VertGreen.getAllGuilds().size() + "\n" + "Known users in servers: " + VertGreen.getAllUsersAsMap().size() + "\n" , true);
        }
        if (!PlayerRegistry.getPlayingPlayers().isEmpty()){
            eb.setColor(BotConstants.VERTYELLOW);
            eb.setFooter(" | Player currently not playing", "http://www.debscrossstitch.co.uk/ekmps/shops/debscrossstitch/images/bright-blue-square-aperture-card-envelope-4-x-6-a6-bright-blue-square-aperture-4-x-6-5-x-cards-envelopes-1.85-2050-p.jpg");
        }
        else {
            eb.setColor(BotConstants.VERTGREEN);
            eb.setFooter(" | Player currently playing", "http://www.iconsdb.com/icons/preview/royal-blue/play-xxl.png");
        }
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Show some statistics about this bot.";
    }
}
