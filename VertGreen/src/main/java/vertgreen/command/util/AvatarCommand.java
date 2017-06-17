package vertgreen.command.util;

import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.List;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUtilCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.commandmeta.MessagingException;
import vertgreen.Config;
import static vertgreen.util.ArgumentUtil.fuzzyMemberSearch;
import vertgreen.util.TextUtils;

public class AvatarCommand extends Command implements IUtilCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        String msgcontent = message.getRawContent();
        if (args.length == 1) {
            eb.setColor(invoker.getColor());
            eb.setTitle("Avatar for " + invoker.getEffectiveName());
            eb.setImage(invoker.getUser().getAvatarUrl() + "?size=1024");    
            try {
                String comurl = TextUtils.postToHastebin(invoker.getUser().getAvatarUrl() + "?size=1024", true) + ".avatar";
                eb.setFooter(comurl, invoker.getUser().getAvatarUrl());
            }
            catch (UnirestException ex) {
                throw new MessagingException("Couldn't upload avatar to hastebin :(");
            }
            channel.sendMessage(eb.build()).queue();
        } else {
            Member target;
            String searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "avatar ", "");
            searchterm = searchterm.toLowerCase();
            List<Member> list = fuzzyMemberSearch(channel.getGuild(), searchterm);
            if (list.size() == 0) {
               channel.sendMessage("No members found for `" + searchterm + "`.").queue();
            } else if (list.size() == 1){
                target = list.get(0);
                eb.setColor(target.getColor());
                eb.setTitle("Avatar for " + target.getEffectiveName());
                eb.setImage(target.getUser().getAvatarUrl() + "?size=1024");
                try {
                    String comurl = TextUtils.postToHastebin(target.getUser().getAvatarUrl() + "?size=1024", true) + ".avatar";
                    eb.setFooter(comurl, target.getUser().getAvatarUrl());
                }
                catch (UnirestException ex) {
                    throw new MessagingException("Couldn't upload avatar to hastebin :(");
                }
                channel.sendMessage(eb.build()).queue();
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
        String usage = "{0}{1} @<username>\n#";
        return usage + I18n.get(guild).getString("helpAvatarCommand");
    }
}
