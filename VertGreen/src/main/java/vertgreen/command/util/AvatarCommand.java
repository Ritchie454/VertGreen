package vertgreen.command.util;

import com.mashape.unirest.http.exceptions.UnirestException;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUtilCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.commandmeta.MessagingException;

import vertgreen.util.ArgumentUtil;
import vertgreen.util.TextUtils;

public class AvatarCommand extends Command implements IUtilCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        String msg;
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
        } else {
            Member target;
            target = ArgumentUtil.checkSingleFuzzySearchResult(channel,args[1]);
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
        }
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} @<username>\n#";
        return usage + I18n.get(guild).getString("helpAvatarCommand");
    }
}
