package vertgreen.command.image;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import vertgreen.command.fun.RandomImageCommand;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IImageCommand;

public class CardCommand extends Command implements IImageCommand{
    private RandomImageCommand cards = new RandomImageCommand("https://imgur.com/a/LpFbx");
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        channel.sendMessage(cards.getRandomImageUrl());
    }
    
    @Override
    public String help(Guild guild) {
        return "<<card \n#Display a random image";
    }
}
