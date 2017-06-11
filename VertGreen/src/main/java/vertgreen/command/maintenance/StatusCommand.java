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

public class StatusCommand extends Command implements IMaintenanceCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        channel.sendMessage("All systems green").queue();
    }

    @Override
    public String help(Guild guild) {
        return "Test the bots status";
    }
}
