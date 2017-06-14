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

package vertgreen.commandmeta;


import vertgreen.util.DistributionEnum;
import vertgreen.util.TextUtils;
import vertgreen.util.DiscordUtil;
import vertgreen.util.RestActionScheduler;
import vertgreen.util.BotConstants;
import vertgreen.commandmeta.abs.ICommandOwnerRestricted;
import vertgreen.commandmeta.abs.ICommandAdminRestricted;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.commandmeta.abs.IMusicBackupCommand;
import vertgreen.Config;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CommandManager {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CommandManager.class);

    public static int commandsExecuted = 0;

    public static void prefixCalled(Command invoked, Guild guild, TextChannel channel, Member invoker, Message message) {
        String[] args = commandToArguments(message.getRawContent());
        commandsExecuted++;


        if (invoked instanceof IMusicCommand
                && !channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE)) {
            log.debug("Ignored command because it was a music command, and this bot cannot write in that channel");
            return;
        }

        if (invoked instanceof ICommandOwnerRestricted) {
            //Check if invoker is actually the owner
            if (!DiscordUtil.isUserBotOwner(invoker.getUser())) {
                channel.sendMessage(TextUtils.prefaceWithName(invoker, I18n.get(guild).getString("cmdAccessDenied"))).queue();
                return;
            }
        }

        if (invoked instanceof ICommandAdminRestricted) {
            //only admins and the bot owner can execute these
            if (!isAdmin(invoker) && !DiscordUtil.isUserBotOwner(invoker.getUser())) {
                channel.sendMessage(TextUtils.prefaceWithName(invoker, I18n.get(guild).getString("cmdAccessDenied"))).queue();
                return;
            }
        }

        try {
            invoked.onInvoke(guild, channel, invoker, message, args);
        } catch (Exception e) {
            TextUtils.handleException(e, channel, invoker);
        }

    }

    /**
     * returns true if the member is or holds a role defined as admin in the configuration file
     */
    private static boolean isAdmin(Member invoker) {
        boolean admin = false;
        for (String id : Config.CONFIG.getAdminIds()) {
            Role r = invoker.getGuild().getRoleById(id);
            if (invoker.getUser().getId().equals(id)
                    || (r != null && invoker.getRoles().contains(r))) {
                admin = true;
                break;
            }
        }
        return admin;
    }

    private static String[] commandToArguments(String cmd) {
        ArrayList<String> a = new ArrayList<>();
        int argi = 0;
        boolean isInQuote = false;

        for (Character ch : cmd.toCharArray()) {
            if (Character.isWhitespace(ch) && !isInQuote) {
                String arg = null;
                try {
                    arg = a.get(argi);
                } catch (IndexOutOfBoundsException e) {
                }
                if (arg != null) {
                    argi++;//On to the next arg
                }//else ignore

            } else if (ch.equals('"')) {
                isInQuote = !isInQuote;
            } else {
                a = writeToArg(a, argi, ch);
            }
        }

        String[] newA = new String[a.size()];
        int i = 0;
        for (String str : a) {
            newA[i] = str;
            i++;
        }

        return newA;
    }

    private static ArrayList<String> writeToArg(ArrayList<String> a, int argi, char ch) {
        String arg = null;
        try {
            arg = a.get(argi);
        } catch (IndexOutOfBoundsException ignored) {
        }
        if (arg == null) {
            a.add(argi, String.valueOf(ch));
        } else {
            a.set(argi, arg + ch);
        }

        return a;
    }
}
