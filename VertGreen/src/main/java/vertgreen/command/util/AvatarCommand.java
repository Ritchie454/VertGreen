/*
 * MIT License
 *
 * Copyright (c) 2017 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

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
        if (message.getMentionedUsers().isEmpty()) {
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
