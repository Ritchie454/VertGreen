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
        Long TotMem = Runtime.getRuntime().totalMemory() / 1000000;
        Long FreeMem = Runtime.getRuntime().freeMemory() / 1000000;
        Long MaxMem = Runtime.getRuntime().maxMemory() / 1000000;
        Long CurrMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000;
        if (CurrMem > 500) {
            eb.setFooter("Warning, High memory usage!", "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Achtung.svg/1169px-Achtung.svg.png");
            eb.setColor(BotConstants.VERTRED);
        } else if (CurrMem > 250) {
            eb.setFooter("Moderate memory usage", "http://www.clker.com/cliparts/6/8/1/7/1242237019492940192Achtung-yellow.svg.hi.png");
            eb.setColor(BotConstants.VERTYELLOW);
        } else {
            eb.setFooter("Low memory usage", "http://www.freeiconspng.com/uploads/accept-tick-icon-12.png");
            eb.setColor(BotConstants.VERTGREEN);
        }
        eb.addField("Memory Stats", "Reserved memory: " + TotMem + "MB\n" + "-> Of which is used: " + CurrMem + "MB\n" + "-> Of which is free: " + FreeMem + "MB\n" + "Max reservable: " + MaxMem + "MB\n", true);
        
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Show some statistics about this bot.";
    }
}
