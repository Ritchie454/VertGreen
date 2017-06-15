package vertgreen.commandmeta.init;

import vertgreen.command.util.AvatarCommand;
import vertgreen.command.util.InviteCommand;
import vertgreen.command.util.HelpCommand;
import vertgreen.command.util.CommandsCommand;
import vertgreen.command.util.MALCommand;
import vertgreen.command.util.DonateCommand;
import vertgreen.command.maintenance.GitInfoCommand;
import vertgreen.command.maintenance.StatsCommand;
import vertgreen.command.maintenance.VersionCommand;
import vertgreen.command.maintenance.ShardsCommand;
import vertgreen.command.maintenance.MemoryCommand;
import vertgreen.command.maintenance.StatusCommand;
import vertgreen.command.fun.TalkCommand;
import vertgreen.command.fun.JokeCommand;
import vertgreen.command.admin.BotRestartCommand;
import vertgreen.command.admin.UpdateCommand;
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
        CommandRegistry.registerCommand("status", new StatusCommand());
        CommandRegistry.registerCommand("memory", new MemoryCommand());
        CommandRegistry.registerCommand("shards", new ShardsCommand());
        CommandRegistry.registerCommand("version", new VersionCommand());
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
        CommandRegistry.registerCommand("boast", new JokeCommand());
        CommandRegistry.registerCommand("update", new UpdateCommand());
        CommandRegistry.registerCommand("botrestart", new BotRestartCommand());
        CommandRegistry.registerCommand("clear", new ClearCommand());
        CommandRegistry.registerCommand("talk", new TalkCommand());
        CommandRegistry.registerCommand("mal", new MALCommand());
        CommandRegistry.registerCommand("hardban", new HardbanCommand());
        CommandRegistry.registerCommand("kick", new KickCommand());
        CommandRegistry.registerCommand("softban", new SoftbanCommand());
        CommandRegistry.registerCommand("donate", new DonateCommand());
        CommandRegistry.registerAlias("donate", "patron");
        CommandRegistry.registerAlias("donate", "patreon");

    }

}
