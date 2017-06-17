package vertgreen.command.util;

import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.Collections;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUtilCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.commandmeta.MessagingException;
import vertgreen.util.BotConstants;
import java.text.MessageFormat;
import java.util.*;
import net.dv8tion.jda.core.Permission;
import vertgreen.Config;

import vertgreen.util.ArgumentUtil;
import static vertgreen.util.ArgumentUtil.fuzzyMemberSearch;
import vertgreen.util.TextUtils;

public class PermissionsCommand extends Command implements IUtilCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        Member target;
        String hasteurl;
        String msgcontent = message.getRawContent();
        if (args.length == 1) {
            target = invoker;
            eb.setColor(invoker.getColor());
            eb.setThumbnail(invoker.getUser().getAvatarUrl());
            List<Permission> permurl = new ArrayList<>(target.getPermissions()); 
            Collections.sort(permurl);
            String sortperms = permurl.toString();
            String sortedperms = " - " + sortperms.replace("_", " ").replace("["," ").replace("]", " ").replace(",", "\n - ").toLowerCase();
            try {
                hasteurl = TextUtils.postToHastebin(sortedperms, true) + ".perms";
            }
            catch (UnirestException ex) {
                throw new MessagingException("Couldn't upload permissions to hastebin :(");
            }
            eb.addField("Permissions for " + invoker.getEffectiveName(), " " + sortedperms, true);
            channel.sendMessage(eb.build()).queue();
            channel.sendMessage("If you can't see embeds, view your permissions here:\n" + hasteurl).queue();
        } else {
            String searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "perms ", "");
            searchterm = searchterm.toLowerCase();
            List<Member> list = fuzzyMemberSearch(channel.getGuild(), searchterm);
            if (list.size() == 0) {
               channel.sendMessage("No members found for `" + searchterm + "`.").queue();
            } else if (list.size() == 1){
                target = list.get(0);
                eb.setColor(target.getColor());
                List<Permission> permurl = new ArrayList<>(target.getPermissions()); 
                Collections.sort(permurl);
                String sortperms = permurl.toString();
                String sortedperms = " - " + sortperms.replace("_", " ").replace("["," ").replace("]", " ").replace(",", "\n - ").toLowerCase();
                try {
                    hasteurl = TextUtils.postToHastebin(sortedperms, true) + ".perms";
                }
                catch (UnirestException ex) {
                    throw new MessagingException("Couldn't upload permissions to hastebin :(");
                }
                eb.addField("Permissions for " + target.getEffectiveName(), " " + sortedperms, true);
                eb.setThumbnail(target.getUser().getAvatarUrl());
                channel.sendMessage(eb.build()).queue();
                channel.sendMessage("If you can't see embeds, view your permissions here:\n" + hasteurl).queue();  
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
        

    }//.

    @Override
    public String help(Guild guild) {
        String usage = "{0} {1} @<username>\n#";
        return usage + "Get permissions for the mentionned user";
    }
}
