package vertgreen.command.util;

import vertgreen.Config;
import vertgreen.VertGreen;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUtilCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

import static vertgreen.util.ArgumentUtil.fuzzyMemberSearch;

public class KnownServersCommand extends Command implements IUtilCommand {
    Member target;
    String msgcontent;
    StringBuilder knownServers = new StringBuilder();
    List<Guild> matchguild;
    String searchterm;
    List<Member> list;
    EmbedBuilder eb;
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        eb = new EmbedBuilder();
        msgcontent = message.getRawContent();
        if(args.length == 1) {
            knownServersSelf(channel, invoker);
        } else {
            getFuzzyResult(channel, message);
            if (list.size() == 0) {
               searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "kservers ", "");
               channel.sendMessage("No members found for `" + searchterm + "`.").queue();
            } else if (list.size() == 1){
                knownServersTarget(channel);
            } else if (list.size() >= 2){
                multiFuzzyResult(channel);
            } 
        }
    }

    private void knownServersSelf(TextChannel channel, Member invoker){
            matchguild = new ArrayList<>();
            target = invoker;
            if (target == null) return;
            for(Guild g: VertGreen.getAllGuilds()) {
                if(g.getMemberById(target.getUser().getId()) != null) {
                    matchguild.add(g);
                }
            }
            if(matchguild.size() >= 30) {
                knownServers.append(matchguild.size());
            } else {
                int i = 0;
                for(Guild g: matchguild) {
                    i++;
                    knownServers.append(g.getName()).append(",\n");
                    if(i == 5) {
                        break;
                    }
                }
            }
            knownServers.append(matchguild.size());
            eb.setColor(target.getColor());
            eb.setThumbnail(target.getUser().getAvatarUrl());
            eb.setTitle("Shared servers with " + target.getEffectiveName(), null);
            eb.addField("Shared Servers",knownServers.toString(),true); //Known Server
            eb.setFooter(target.getUser().getName() + "#" + target.getUser().getDiscriminator(), target.getUser().getAvatarUrl());
            channel.sendMessage(eb.build()).queue();
    }
    
    private void knownServersTarget(TextChannel channel){  
            matchguild = new ArrayList<>();
            target = list.get(0);
            if (target == null) return;
                for(Guild g: VertGreen.getAllGuilds()) {
                    if(g.getMemberById(target.getUser().getId()) != null) {
                    matchguild.add(g);
                    }
                }
                if(matchguild.size() >= 30) {
                    knownServers.append(matchguild.size());
                } else {
                    int i = 0;
                    for(Guild g: matchguild) {
                    i++;
                    knownServers.append(g.getName());
                    if(i < 5) {
                        knownServers.append(",\n");
                        }
                    }
                }
            knownServers.append(matchguild.size());
            eb.setColor(target.getColor());
            eb.setThumbnail(target.getUser().getAvatarUrl());
            eb.setTitle("Shared servers with " + target.getEffectiveName(), null);
            eb.addField("Shared Servers",knownServers.toString(),true); //Known Server
            eb.setFooter(target.getUser().getName() + "#" + target.getUser().getDiscriminator(), target.getUser().getAvatarUrl());
            channel.sendMessage(eb.build()).queue();
    }
    
    private void getFuzzyResult(TextChannel channel, Message message){
            searchterm = msgcontent.replace(Config.CONFIG.getPrefix() + "kservers ", "");
            searchterm = searchterm.toLowerCase();
            list = new ArrayList<>(fuzzyMemberSearch(channel.getGuild(), searchterm));
    }
    
    private void multiFuzzyResult(TextChannel channel){
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


