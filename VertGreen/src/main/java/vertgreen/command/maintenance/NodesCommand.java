package vertgreen.command.maintenance;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.remote.RemoteNode;
import vertgreen.audio.AbstractPlayer;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import vertgreen.util.DiscordUtil;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;

public class NodesCommand extends Command implements IMaintenanceCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        AudioPlayerManager pm = AbstractPlayer.getPlayerManager();
        List<RemoteNode> nodes = pm.getRemoteNodeRegistry().getNodes();
        boolean showHost = false;

        if (args.length == 2 && args[1].equals("host")) {
            if (DiscordUtil.isUserBotOwner(invoker.getUser())) {
                showHost = true;
            } else {
                TextUtils.replyWithName(channel, invoker, "You do not have permission to view the hosts!");
            }
        }

        MessageBuilder mb = new MessageBuilder();
        mb.append("```\n");
        int i = 0;
        for (RemoteNode node : nodes) {
            mb.append("Node " + i + "\n");
            if (showHost) {
                mb.append(node.getAddress())
                        .append("\n");
            }
            mb.append("Status: ")
                    .append(node.getConnectionState().toString())
                    .append("\nPlaying: ")
                    .append(node.getLastStatistics() == null ? "UNKNOWN" : node.getLastStatistics().playingTrackCount)
                    .append("\nCPU: ")
                    .append(node.getLastStatistics() == null ? "UNKNOWN" : node.getLastStatistics().systemCpuUsage * 100 + "%")
                    .append("\n");

            mb.append(node.getBalancerPenaltyDetails());

            mb.append("\n\n");

            i++;
        }

        mb.append("```");
        channel.sendMessage(mb.build()).queue();
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1} OR {0}{1} host\n#Show information about the connected lava nodes.";
    }
}
