package vertgreen.commandmeta.abs;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;

public abstract class Command implements ICommand {
    
    public static String name = "Undefined";
    public static ArrayList<String> aliasses = new ArrayList<>();
    
    public void onInvoke(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, ArrayList<String> args){
        String[] newA = new String[args.size()];
        int i = 0;
        for (String str : args) {
            newA[i] = str;
            i++;
        }
        onInvoke(guild, channel, invoker, message, newA);
    }
    
    public static CommandInfo getCommandInfo(){
        return new CommandInfo(name, "Undefined", "Undefined", aliasses);
    }
    
    public static class CommandInfo {
        
        public String name;
        public String desc;
        public String usage;
        public ArrayList<String> aliasses;

        public CommandInfo(String name, String desc, String usage, ArrayList<String> aliasses) {
            this.name = name;
            this.desc = desc;
            this.usage = usage;
            this.aliasses = aliasses;
        }
        
    }
    
}
