// 
// Decompiled by Procyon v0.5.36
// 

package main.java.de.voidtech.alison.commands;

import java.util.List;
import net.dv8tion.jda.api.entities.Message;

public abstract class AbstractCommand
{
    public abstract void execute(final Message message, final List<String> args);
    
    public abstract String getName();
}
