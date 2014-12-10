/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.command;

import net.spleefleague.core.plugin.CorePlugin;

/**
 *
 * @author Jonas
 */
public class LoadedCommand {

    private CorePlugin owner;
    private BasicCommand commandExecutor;
    private String command = "undefined", description = "undefined", definedUsage = "undefined";

    public LoadedCommand(CorePlugin owner, BasicCommand commandExecutor, String command, String description, String definedUsage) {
        this.owner = owner;
        this.commandExecutor = commandExecutor;
        this.command = command;
        this.description = description;
        this.definedUsage = definedUsage;
    }

    public CorePlugin getOwner() {
        return this.owner;
    }

    public LoadedCommand setOwner(CorePlugin owner) {
        this.owner = owner;
        return this;
    }

    public BasicCommand getExecutor() {
        return this.commandExecutor;
    }

    public LoadedCommand setExecutor(BasicCommand commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    public String getCommand() {
        return this.command;
    }

    public LoadedCommand setCommand(String command) {
        this.command = command;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public LoadedCommand setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDefinedUsage() {
        return this.definedUsage;
    }

    public LoadedCommand setDefinedUsage(String definedUsage) {
        this.definedUsage = definedUsage;
        return this;
    }
}