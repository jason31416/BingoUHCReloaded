package dev.onelili.bingoreloaded.command;

import cn.jason31416.planetlib.command.ICommandContext;
import cn.jason31416.planetlib.command.RootCommand;
import cn.jason31416.planetlib.message.Message;
import org.jetbrains.annotations.Nullable;

public class BingoCommand extends RootCommand {
    public BingoCommand(String name){
        super(name);
    }

    public @Nullable Message execute(ICommandContext context){
        return null;
    }
}
