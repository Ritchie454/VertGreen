package vertgreen.command.util;

import vertgreen.VertGreen;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUtilCommand;
import vertgreen.feature.I18n;
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
import vertgreen.Config;
import static vertgreen.util.ArgumentUtil.fuzzyMemberSearch;

public class UserInfoCommand extends Command implements IUtilCommand {
    Integer knownServers;
    List<Guild> matchguild;
    Member target;
    String msgcontent;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    String game;
    String role;
    ResourceBundle rb;
    String searchterm;
    List<Member> list;
    EmbedBuilder eb;
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        eb = new EmbedBuilder();
        rb = I18n.get(guild);
        msgcontent = message.getRawContent();
        searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "kservers ", "");
        searchterm = searchterm.toLowerCase();
        list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
        if(args.length == 1) {
            userInfoSelf(channel, invoker);
        } else {
            getFuzzyResult(channel);
            if (list.size() == 0) {
               channel.sendMessage("No members found for `" + searchterm + "`.").queue();
            } else if (list.size() == 1){
               userInfoTarget(channel);
            } else if (list.size() >= 2){
               fuzzyMultiResult(channel);
            } 
        }
    }
    private void userInfoSelf(TextChannel channel, Member invoker){
            String status;
            matchguild = new ArrayList<>();
            list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
            target = list.get(0);
        switch (target.getOnlineStatus().name()) {
            case "ONLINE":
                status = "<:online:313956277808005120>";
                break;
            case "IDLE":
                status = "<:away:313956277220802560>";
                break;
            case "DO_NOT_DISTURB":
                status = "<:dnd:313956276893646850>";
                break;
            case "UNKNOWN":
                status = "<:streaming:313956277132853248>";
                break;
            default:
                status = "<:offline:313956277237710868>";
                break;
        }
            if (target.getGame() == null){
                game = "Not currently in game..";
            } else {
                game = target.getGame().getName();
            }
            if (target.getRoles().size() >= 1){
                role = target.getRoles().get(0).getName();
            } else {
                role = "everyone";
            }
            if (target == null) return;
        for(Guild g: VertGreen.getAllGuilds()) {
            if(g.getMemberById(target.getUser().getId()) != null) {
                matchguild.add(g);
            }
        }
            knownServers = matchguild.size();
            eb.setColor(target.getColor());
            eb.setThumbnail(target.getUser().getAvatarUrl());
            eb.setTitle(status + MessageFormat.format(rb.getString("userinfoTitle"),target.getUser().getName()), null);
            eb.addField("Nickname",target.getEffectiveName()+ "\n" + target.getUser().getAsMention(),true);
            eb.addField("Shared Servers",knownServers.toString(),true); //Known Server
            eb.addField(rb.getString("userinfoJoinDate"),target.getJoinDate().format(dtf),true);
            eb.addField(rb.getString("userinfoCreationTime"),target.getUser().getCreationTime().format(dtf),true);
            eb.addField("Highest Role", role, true);
            eb.addField("Current Game", game, true);
            eb.setFooter(target.getUser().getName() + "#" + target.getUser().getDiscriminator(), target.getUser().getAvatarUrl());
            channel.sendMessage(eb.build()).queue();
    }
    
    private void userInfoTarget(TextChannel channel){
            String status;
            matchguild = new ArrayList<>();
            list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
            target = list.get(0);
        switch (target.getOnlineStatus().name()) {
            case "ONLINE":
                status = "<:online:313956277808005120>";
                break;
            case "IDLE":
                status = "<:away:313956277220802560>";
                break;
            case "DO_NOT_DISTURB":
                status = "<:dnd:313956276893646850>";
                break;
            case "UNKNOWN":
                status = "<:streaming:313956277132853248>";
                break;
            default:
                status = "<:offline:313956277237710868>";
                break;
        }
            if (target.getGame() == null){
                game = "Not currently in game..";
            } else {
                game = target.getGame().getName();
            }
            if (target.getRoles().size() >= 1){
                role = target.getRoles().get(0).getName();
            } else {
                role = "everyone";
            }
            if (target == null) return;
        for(Guild g: VertGreen.getAllGuilds()) {
            if(g.getMemberById(target.getUser().getId()) != null) {
                matchguild.add(g);
            }
        }
            knownServers = matchguild.size();
            eb.setColor(target.getColor());
            eb.setThumbnail(target.getUser().getAvatarUrl());
            eb.setTitle(status + MessageFormat.format(rb.getString("userinfoTitle"),target.getUser().getName()), null);
            eb.addField("Nickname",target.getEffectiveName()+ "\n" + target.getUser().getAsMention(),true);
            eb.addField("Shared Servers",knownServers.toString(),true); //Known Server
            eb.addField(rb.getString("userinfoJoinDate"),target.getJoinDate().format(dtf),true);
            eb.addField(rb.getString("userinfoCreationTime"),target.getUser().getCreationTime().format(dtf),true);
            eb.addField("Highest Role", role, true);
            eb.addField("Current Game", game, true);
            eb.setFooter(target.getUser().getName() + "#" + target.getUser().getDiscriminator(), target.getUser().getAvatarUrl());
            channel.sendMessage(eb.build()).queue();
    }
    
    private void getFuzzyResult(TextChannel channel){
        searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "userinfo ", "");
        searchterm = searchterm.toLowerCase();
        list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
    }
    
    private void fuzzyMultiResult(TextChannel channel){
        String msg = "Multiple users were found. Did you mean any of these users?\n```";
        for (int i = 0; i < 5; i++){
            if(list.size() == i) break;
            msg = msg + "\n" + list.get(i).getUser().getName() + "#" + list.get(i).getUser().getDiscriminator();
        }
        msg = list.size() > 5 ? msg + "\n[...]" : msg;
        msg = msg + "```";
        channel.sendMessage(msg).queue();
    }
    
    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} OR {0}{1} <user>\n#";
        return usage + I18n.get(guild).getString("helpUserInfoCommand");
    }
}
