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
 */

package vertgreen.commandmeta.init;

import vertgreen.command.util.AvatarCommand;
import vertgreen.command.util.InviteCommand;
import vertgreen.command.util.HelpCommand;
import vertgreen.command.util.CommandsCommand;
import vertgreen.command.util.MALCommand;
import vertgreen.command.maintenance.FuzzyUserSearchCommand;
import vertgreen.command.maintenance.GitInfoCommand;
import vertgreen.command.maintenance.StatsCommand;
import vertgreen.command.maintenance.VersionCommand;
import vertgreen.command.maintenance.ShardsCommand;
import vertgreen.command.maintenance.MemoryCommand;
import vertgreen.command.fun.TalkCommand;
import vertgreen.command.fun.JokeCommand;
import vertgreen.command.admin.BotRestartCommand;
import vertgreen.command.admin.UpdateCommand;
import vertgreen.command.admin.CompileCommand;
import vertgreen.command.admin.MavenTestCommand;
import vertgreen.command.admin.ExitCommand;
import vertgreen.command.moderation.ClearCommand;
import vertgreen.command.moderation.HardbanCommand;
import vertgreen.command.moderation.KickCommand;
import vertgreen.command.moderation.SoftbanCommand;
import vertgreen.commandmeta.CommandRegistry;

public class MainCommandInitializer {

    public static void initCommands() {
        CommandRegistry.registerCommand("help", new HelpCommand());
        CommandRegistry.registerAlias("help", "info");

        CommandRegistry.registerCommand("commands", new CommandsCommand());
        CommandRegistry.registerAlias("commands", "comms");
        CommandRegistry.registerCommand("memory", new MemoryCommand());
        CommandRegistry.registerAlias("memory", "stats.memory");
        CommandRegistry.registerCommand("shards", new ShardsCommand());
        CommandRegistry.registerAlias("shards", "stats.shards");
        CommandRegistry.registerCommand("version", new VersionCommand());
        CommandRegistry.registerAlias("version", "stats.version");
        CommandRegistry.registerCommand("stats", new StatsCommand());
        CommandRegistry.registerCommand("serverinfo", new vertgreen.command.util.ServerInfoCommand());
        CommandRegistry.registerAlias("serverinfo", "guildinfo");
        CommandRegistry.registerCommand("invite", new InviteCommand());
        CommandRegistry.registerCommand("userinfo", new vertgreen.command.util.UserInfoCommand());
        CommandRegistry.registerAlias("userinfo", "memberinfo");
        CommandRegistry.registerCommand("gitinfo", new GitInfoCommand());
        CommandRegistry.registerAlias("gitinfo", "git");
        CommandRegistry.registerCommand("exit", new ExitCommand());
        CommandRegistry.registerCommand("avatar", new AvatarCommand());
        CommandRegistry.registerCommand("joke", new JokeCommand());

        CommandRegistry.registerCommand("update", new UpdateCommand());
        CommandRegistry.registerCommand("compile", new CompileCommand());
        CommandRegistry.registerCommand("mvntest", new MavenTestCommand());
        CommandRegistry.registerCommand("botrestart", new BotRestartCommand());
        CommandRegistry.registerCommand("clear", new ClearCommand());
        CommandRegistry.registerCommand("talk", new TalkCommand());
        CommandRegistry.registerCommand("mal", new MALCommand());
        CommandRegistry.registerCommand("fuzzy", new FuzzyUserSearchCommand());
        CommandRegistry.registerCommand("hardban", new HardbanCommand());
        CommandRegistry.registerCommand("kick", new KickCommand());
        CommandRegistry.registerCommand("softban", new SoftbanCommand());


        /* Other Anime Discord, Sergi memes or any other memes */
        // saved in this album https://imgur.com/a/wYvDu
    }

}
