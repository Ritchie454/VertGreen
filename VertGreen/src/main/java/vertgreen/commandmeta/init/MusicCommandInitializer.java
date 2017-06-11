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

import vertgreen.command.music.control.SkipCommand;
import vertgreen.command.music.control.PlayCommand;
import vertgreen.command.music.control.PauseCommand;
import vertgreen.command.music.control.StopCommand;
import vertgreen.command.music.control.PlaySplitCommand;
import vertgreen.command.music.control.JoinCommand;
import vertgreen.command.music.control.ReshuffleCommand;
import vertgreen.command.music.control.LeaveCommand;
import vertgreen.command.music.control.VolumeCommand;
import vertgreen.command.music.control.UnpauseCommand;
import vertgreen.command.music.control.SelectCommand;
import vertgreen.command.music.control.RepeatCommand;
import vertgreen.command.music.control.ShuffleCommand;
import vertgreen.command.maintenance.AudioDebugCommand;
import vertgreen.command.maintenance.GitInfoCommand;
import vertgreen.command.maintenance.StatsCommand;
import vertgreen.command.maintenance.ShardsCommand;
import vertgreen.command.admin.BotRestartCommand;
import vertgreen.command.admin.UpdateCommand;
import vertgreen.command.admin.CompileCommand;
import vertgreen.command.admin.AnnounceCommand;
import vertgreen.command.admin.PlayerDebugCommand;
import vertgreen.command.admin.ExitCommand;
import vertgreen.Config;
import vertgreen.agent.VoiceChannelCleanupAgent;
import vertgreen.command.config.ConfigCommand;
import vertgreen.command.config.LanguageCommand;
import vertgreen.command.music.info.ExportCommand;
import vertgreen.command.music.info.ListCommand;
import vertgreen.command.music.info.NowplayingCommand;
import vertgreen.command.music.seeking.ForwardCommand;
import vertgreen.command.music.seeking.RestartCommand;
import vertgreen.command.music.seeking.RewindCommand;
import vertgreen.command.music.seeking.SeekCommand;
import vertgreen.command.util.CommandsCommand;
import vertgreen.command.util.HelpCommand;
import vertgreen.command.util.MusicHelpCommand;
import vertgreen.commandmeta.CommandRegistry;
import vertgreen.util.DistributionEnum;
import vertgreen.util.SearchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicCommandInitializer {

    private static final Logger log = LoggerFactory.getLogger(MusicCommandInitializer.class);

    public static void initCommands() {
        CommandRegistry.registerCommand("help", new HelpCommand());
        CommandRegistry.registerAlias("help", "info");

        CommandRegistry.registerCommand("play", new PlayCommand(SearchUtil.SearchProvider.YOUTUBE));
        CommandRegistry.registerAlias("play", "yt");
        CommandRegistry.registerCommand("sc", new PlayCommand(SearchUtil.SearchProvider.SOUNDCLOUD));
        CommandRegistry.registerAlias("sc", "soundcloud");
        CommandRegistry.registerCommand("skip", new SkipCommand());
        CommandRegistry.registerCommand("join", new JoinCommand());
        CommandRegistry.registerAlias("join", "summon");
        CommandRegistry.registerCommand("nowplaying", new NowplayingCommand());
        CommandRegistry.registerAlias("nowplaying", "np");
        CommandRegistry.registerCommand("leave", new LeaveCommand());
        CommandRegistry.registerCommand("list", new ListCommand());
        CommandRegistry.registerAlias("list", "queue");
        CommandRegistry.registerCommand("select", new SelectCommand());
        CommandRegistry.registerCommand("stop", new StopCommand());
        CommandRegistry.registerCommand("pause", new PauseCommand());
        CommandRegistry.registerCommand("unpause", new UnpauseCommand());
        CommandRegistry.registerCommand("shuffle", new ShuffleCommand());
        CommandRegistry.registerCommand("reshuffle", new ReshuffleCommand());
        CommandRegistry.registerCommand("repeat", new RepeatCommand());
        CommandRegistry.registerCommand("volume", new VolumeCommand());
        CommandRegistry.registerAlias("volume", "vol");
        CommandRegistry.registerCommand("restart", new RestartCommand());
        CommandRegistry.registerCommand("export", new ExportCommand());
        CommandRegistry.registerCommand("playerdebug", new PlayerDebugCommand());
        CommandRegistry.registerCommand("music", new MusicHelpCommand());
        CommandRegistry.registerAlias("music", "musichelp");
        CommandRegistry.registerCommand("commands", new CommandsCommand());
        CommandRegistry.registerAlias("commands", "comms");
        CommandRegistry.registerCommand("split", new PlaySplitCommand());
        CommandRegistry.registerCommand("config", new ConfigCommand());
        CommandRegistry.registerCommand("lang", new LanguageCommand());
        CommandRegistry.registerCommand("adebug", new AudioDebugCommand());
        CommandRegistry.registerCommand("announce", new AnnounceCommand());

        CommandRegistry.registerCommand("seek", new SeekCommand());
        CommandRegistry.registerCommand("forward", new ForwardCommand());
        CommandRegistry.registerAlias("forward", "fwd");
        CommandRegistry.registerCommand("rewind", new RewindCommand());
        CommandRegistry.registerAlias("rewind", "rew");

        // The null check is to ensure we can run this in a test run
        if (Config.CONFIG == null || Config.CONFIG.getDistribution() != DistributionEnum.PATRON) {
            new VoiceChannelCleanupAgent().start();
        } else {
            log.info("Skipped setting up the VoiceChannelCleanupAgent since we are running as PATRON distribution.");
        }
    }

}
