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
import vertgreen.Config;

import vertgreen.util.ArgumentUtil;
import static vertgreen.util.ArgumentUtil.fuzzyMemberSearch;
import vertgreen.util.TextUtils;

public class RolesCommand extends Command implements IUtilCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        Member target;
        String hasteurl;
        String sortroles;
        String formroles;
        String msgcontent = message.getRawContent();
        if (args.length == 1) {
            target = invoker;
            if (target.getRoles().size() >= 1){     
                List<Role> roles = new ArrayList<>(target.getRoles());
                Collections.sort(roles);
                sortroles = roles.toString();
                formroles = sortroles.replace("R:", "**").replace("[", "").replace("(", "**--").replace("),", "\n").replace("]", "").replace(")", "");
            } else {
                formroles = "everyone";
            }
            try {
                hasteurl = TextUtils.postToHastebin(formroles, true) + ".roles";
            }
           catch (UnirestException ex) {
                throw new MessagingException("Couldn't upload roles to hastebin :(");
            }
            eb.addField("Roles for " + invoker.getEffectiveName(), " " + formroles, true);
            eb.setThumbnail(target.getUser().getAvatarUrl());
            eb.setColor(invoker.getColor());
            channel.sendMessage(eb.build()).queue();
            channel.sendMessage("If you can't see embeds, view your roles here:\n" + hasteurl).queue();
        } else {
            String searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "roles ", "");
            searchterm = searchterm.toLowerCase();
            List<Member> list = fuzzyMemberSearch(channel.getGuild(), searchterm);
            if (list.size() == 0) {
               channel.sendMessage("No members found for `" + searchterm + "`.").queue();
            } else if (list.size() == 1){
                target = list.get(0);
                eb.setColor(target.getColor());
                eb.setThumbnail(target.getUser().getAvatarUrl());
                List<Role> roles = new ArrayList<>(target.getRoles());
                Collections.sort(roles);
                sortroles = roles.toString();
                formroles = sortroles.replace("R:", "**").replace("[", "").replace("(", "**--").replace("),", "\n").replace("]", "").replace(")", "");
                eb.addField("Roles for " + invoker.getEffectiveName(), " " + formroles, true);
                try {
                    hasteurl = TextUtils.postToHastebin(formroles, true) + ".roles";
                }
                catch (UnirestException ex) {
                    throw new MessagingException("Couldn't upload roles to hastebin :(");
                }
                channel.sendMessage(eb.build()).queue();
                channel.sendMessage("If you can't see embeds, view your roles here:\n" + hasteurl).queue();
            } else if (list.size() >= 2){
                String msg = "Multiple users were found. Did you mean any of these users?\n```";

                for (int i = 0; i < 5; i++){
                    if(list.size() == i) break;
                    msg = msg + "\n" + list.get(i).getUser().getName() + "#" + list.get(i).getUser().getDiscriminator();
                }

                msg = list.size() > 5 ? msg + "\n[...]" : msg;
                msg = msg + "```";

                channel.sendMessage(msg).queue();
            } 
        }   
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0} {1} @<username>\n#";
        return usage + "Get the roles for the mentionned user";
    }
}
