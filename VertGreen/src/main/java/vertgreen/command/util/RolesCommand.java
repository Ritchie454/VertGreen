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
import net.dv8tion.jda.core.*;
import vertgreen.VertGreen;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUtilCommand;
import vertgreen.feature.I18n;
import vertgreen.util.ArgumentUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import vertgreen.util.ArgumentUtil;
import vertgreen.util.TextUtils;

public class RolesCommand extends Command implements IUtilCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        EmbedBuilder eb = new EmbedBuilder();
        Member target;
        String hasteurl;
        if (message.getMentionedUsers().isEmpty()) {
            target = invoker;
        } else {
            target = ArgumentUtil.checkSingleFuzzySearchResult(channel,args[1]);
        }
        String roleurl = target.getRoles().toString(); 
        //Collections.sort(roleurl);
        //String sortperms = permurl.toString();
        try {
             hasteurl = TextUtils.postToHastebin(roleurl, true) + ".roles";
        }
        catch (UnirestException ex) {
            throw new MessagingException("Couldn't upload roles to hastebin :(");
        }
        //eb.setTitle("Permissions for" + target.getEffectiveName());
        eb.setColor(target.getColor());
        eb.setThumbnail(target.getUser().getAvatarUrl());
        eb.addField("Roles for " + target.getEffectiveName(), " " + hasteurl, true);
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
