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

public class PermissionsCommand extends Command implements IUtilCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        Member target;
        String permurl;
        if (message.getMentionedUsers().isEmpty()) {
            target = invoker;
        } else {
            target = ArgumentUtil.checkSingleFuzzySearchResult(channel,args[1]);

        }
        try {
            permurl = TextUtils.postToHastebin(target.getPermissions().toString(), true) + ".perms";
        }
        catch (UnirestException ex) {
            throw new MessagingException("Couldn't upload permissions to hastebin :(");
        }
        //eb.setTitle("Permissions for" + target.getEffectiveName());
        eb.setColor(target.getColor());
        eb.addField("Permissions for" + target.getEffectiveName(), target.getPermissions().toString().replace("_", " ").replace("["," ").replace("]", " "), true);
        eb.setFooter(permurl, target.getUser().getAvatarUrl());
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0} {1} @<username>\n#";
        return usage + "Get permissions for the mentionned user";
    }
}
