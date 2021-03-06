package vertgreen.command.admin;

import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommandOwnerRestricted;
import vertgreen.util.log.SLF4JInputStreamErrorLogger;
import vertgreen.util.log.SLF4JInputStreamLogger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import vertgreen.VertGreen;
import vertgreen.util.ExitCodes;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;

public class UpdateCommand extends Command implements ICommandOwnerRestricted {

    private static final Logger log = LoggerFactory.getLogger(UpdateCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        try {
            Runtime rt = Runtime.getRuntime();
            Message msg;
            EmbedBuilder eb = new EmbedBuilder();
            EmbedBuilder eb2 = new EmbedBuilder();
            EmbedBuilder eb3 = new EmbedBuilder();
            EmbedBuilder eb4 = new EmbedBuilder();
            msg = channel.sendMessage("*Now updating...").complete(true);
            eb.setTitle("<:update:264184209617321984>Running `git clone`...");
            channel.sendMessage(eb.build()).queue();
            String branch = "master";
            if (args.length > 1) {
                branch = args[1];
            }
            String githubUser = "Ritchie454";
            if (args.length > 2) {
                githubUser = args[2];
            }

            //Clear any old update folder if it is still present
            try {
                Process rm = rt.exec("rm -rf update");
                rm.waitFor(5, TimeUnit.SECONDS);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            Process gitClone = rt.exec("git clone https://github.com/" + githubUser + "/VertGreen.git --branch " + branch + " --recursive --single-branch update");
            new SLF4JInputStreamLogger(log, gitClone.getInputStream()).start();
            new SLF4JInputStreamErrorLogger(log, gitClone.getInputStream()).start();

            if (!gitClone.waitFor(120, TimeUnit.SECONDS)) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: timed out]\n\n").complete(true);
                throw new RuntimeException("Operation timed out: git clone");
            } else if (gitClone.exitValue() != 0) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: returned code " + gitClone.exitValue() + "]\n\n").complete(true);
                throw new RuntimeException("Bad response code");
            }
            eb2.setTitle("<:check:314349398811475968>Succeeded pulling update from GitHub");
            channel.sendMessage(eb2.build()).queue();
            eb3.setTitle("<:stafftools:314348604095594498>Running `mvn package shade:shade`... ");
            channel.sendMessage(eb3.build()).queue();
            File updateDir = new File("update/VertGreen");

            Process mvnBuild = rt.exec("mvn -f " + updateDir.getAbsolutePath() + "/pom.xml package shade:shade");
            new SLF4JInputStreamLogger(log, mvnBuild.getInputStream()).start();
            new SLF4JInputStreamErrorLogger(log, mvnBuild.getInputStream()).start();

            if (!mvnBuild.waitFor(600, TimeUnit.SECONDS)) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: timed out]\n\n").complete(true);
                throw new RuntimeException("Operation timed out: mvn package shade:shade");
            } else if (mvnBuild.exitValue() != 0) {
                msg = msg.editMessage(msg.getRawContent() + "[:anger: returned code " + mvnBuild.exitValue() + "]\n\n").complete(true);
                throw new RuntimeException("Bad response code");
            }
            eb4.setTitle("<:check:314349398811475968>Succeeded packaging VertGreen.jar");
            channel.sendMessage(eb4.build()).queue();

            if(!new File("./update/VertGreen/target/VertGreen-1.0.jar").renameTo(new File("./VertGreen-1.0.jar"))){
                throw new RuntimeException("Failed to move jar to home");
            }
        } catch (InterruptedException | IOException | RateLimitedException ex) {
            throw new RuntimeException(ex);
        }
        
        Runtime rt = Runtime.getRuntime();
        String started;
        try {
            channel.sendMessage("Launching new process...").queue();
            rt.exec("./start.sh");
            started = "true";
        } catch (IOException e) {
            log.warn("Unable to start new process", e);
            channel.sendMessage("```\nWarning!\n Unable to start new process..\n```").queue();
            started = "false";
        }
        if (started.equals("true")){
            channel.sendMessage("Killing old process...").queue();
            VertGreen.shutdown(ExitCodes.EXIT_CODE_UPDATE);
        } else if (started.equals("false")){
            channel.sendMessage("Aborted restart process...").queue();
        }

        
    
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1} [branch [repo]]\n#Update the bot by checking out the provided branch from the provided github repo and compiling it.";
    }
}
