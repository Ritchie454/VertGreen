package vertgreen.command.maintenance;

import net.dv8tion.jda.core.JDA;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class StatusCommand extends Command implements IMaintenanceCommand {
    String status;
    String ping;
    String shard;
    String guildn;
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        JDA jda = guild.getJDA();
        status = "Status: " + jda.getStatus() + "\n"; 
        ping = "Ping: " + jda.getPing() + "ms\n"; 
     //   shard = "Shard: " + jda.getShardInfo().getShardString() + "\n"; 
        guildn = "Guild: " + guild.getName() +"\n";
        channel.sendMessage("<:online:313956277808005120> All systems green <:online:313956277808005120>\n```" + status + ping + guildn + shard + "```").queue();
    }

    @Override
    public String help(Guild guild) {
        return "Test the bots status";
    }
}
