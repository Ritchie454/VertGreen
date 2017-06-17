package vertgreen.command.util;

import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.commandmeta.MessagingException;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUtilCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import java.util.*;

import vertgreen.util.ArgumentUtil;
import vertgreen.util.TextUtils;

public class RolesCommand extends Command implements IUtilCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        Member target;
        String hasteurl;
        String sortroles;
        if (args.length == 1) {
            target = invoker;
        } else {
            target = ArgumentUtil.checkSingleFuzzySearchResult(channel,args[1]);
        }
        if (target.getRoles() != null){     
            List<Role> roles = new ArrayList<>(target.getRoles());
            Collections.sort(roles);
            sortroles = roles.toString();
        } else {
            sortroles = "everyone";
        }
        String formroles = sortroles.replace("R:", "**").replace("[", "").replace("(", "**--").replace("),", "\n").replace("]", "").replace(")", "");
        
        try {
            hasteurl = TextUtils.postToHastebin(formroles, true) + ".roles";
        }
        catch (UnirestException ex) {
            throw new MessagingException("Couldn't upload roles to hastebin :(");
        }
        //eb.setTitle("Permissions for" + target.getEffectiveName());
        eb.setColor(target.getColor());
        eb.setThumbnail(target.getUser().getAvatarUrl());
        eb.addField("Roles for " + target.getEffectiveName(), " " + formroles, true);
        //eb.setFooter("", target.getUser().getAvatarUrl());
        channel.sendMessage(eb.build()).queue();
        channel.sendMessage("If you can't see embeds, view your roles here:\n" + hasteurl).queue();
    }//.

    @Override
    public String help(Guild guild) {
        String usage = "{0} {1} @<username>\n#";
        return usage + "Get the roles for the mentionned user";
    }
}
