package vertgreen.command.maintenance;

import vertgreen.VertGreen;
import vertgreen.commandmeta.CommandManager;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;

import java.text.MessageFormat;
import vertgreen.util.GitRepoState;

public class VersionCommand extends Command implements IMaintenanceCommand {

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
        eb.setColor(BotConstants.VERTGREEN);
        
        eb.addField("Version Info", "Distribution: " + BotConstants.RELEASE + "\n" + "Bot Version:" + BotConstants.VERSION + "\n" + "JDA responses total: " + guild.getJDA().getResponseTotal() + "\n" + "JDA version: " + JDAInfo.VERSION + "\n", true);
        GitRepoState gitRepoState = GitRepoState.getGitRepositoryState();
        eb.setFooter("Rev: " + gitRepoState.describe, "http://i.imgur.com/RjWwxlg.png");
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Show some statistics about this bot.";
    }
}
