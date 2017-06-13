package vertgreen.command.maintenance;

import vertgreen.Config;
import vertgreen.VertGreen;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.CommandManager;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import vertgreen.feature.I18n;
import vertgreen.util.DiscordUtil;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;

import java.text.MessageFormat;

public class MemoryCommand extends Command implements IMaintenanceCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        long totalSecs = (System.currentTimeMillis() -VertGreen.START_TIME) / 1000;
        int days = (int) (totalSecs / (60 * 60 * 24));
        int hours = (int) ((totalSecs / (60 * 60)) % 24);
        int mins = (int) ((totalSecs / 60) % 60);
        int secs = (int) (totalSecs % 60);
        
        String str = MessageFormat.format(
                I18n.get(guild).getString("statsParagraph"),
                days, hours, mins, secs, CommandManager.commandsExecuted - 1)
                + "\n";
        EmbedBuilder eb = new EmbedBuilder();
        Long Mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000;
        if (Mem > 50) {
            channel.sendMessage("Warning, High memory usage ~~TEST MESSAGE PLEASE IGNORE~~").queue();
        }
        eb.setColor(BotConstants.VERTGREEN_COLOR);
        eb.addField("Memory Stats", "Reserved memory: " + Runtime.getRuntime().totalMemory() / 1000000 + "MB\n" + "-> Of which is used: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000 + "MB\n" + "-> Of which is free: " + Runtime.getRuntime().freeMemory() / 1000000 + "MB\n" + "Max reservable: " + Runtime.getRuntime().maxMemory() / 1000000 + "MB\n", true);
        
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Show some statistics about this bot.";
    }
}
