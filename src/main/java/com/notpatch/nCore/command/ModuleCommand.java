package com.notpatch.nCore.command;

import com.notpatch.nCore.NCore;
import com.notpatch.nCore.module.Module;
import com.notpatch.nCore.module.ModuleManager;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleCommand implements BasicCommand {

    private final NCore plugin;
    private final ModuleManager moduleManager;

    private final String error = "§c(§7!§c) ";
    private final String success = "§a(§7!§a) ";

    public ModuleCommand(NCore plugin) {
        this.plugin = plugin;
        this.moduleManager = plugin.getModuleManager();
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args){
        CommandSender sender = stack.getSender();
        if(args.length == 0){
            sendHelp(sender);
            return;
        }

        switch (args[0].toLowerCase()){
            case "list":
                listModules(sender);
                break;

            case "info":
                if (args.length < 2) {
                    sender.sendMessage(error + "§7Usage: §e/module info <module>");
                    return;
                }
                showModuleInfo(sender, args[1]);
                break;

            case "load":
                if (args.length < 2) {
                    sender.sendMessage(error + "§7Usage: §e/module load <jarfile>");
                    return;
                }
                loadModule(sender, args[1]);
                break;

            case "enable":
                if (args.length < 2) {
                    sender.sendMessage(error + "§7Usage: §e/module enable <module>");
                    return;
                }
                enableModule(sender, args[1]);
                break;

            case "disable":
                if (args.length < 2) {
                    sender.sendMessage(error + "§7Usage: §e/module disable <module>");
                    return;
                }
                disableModule(sender, args[1]);
                break;

            case "reload":
                if (args.length < 2) {
                    reloadAllModules(sender);
                } else {
                    reloadModule(sender, args[1]);
                }
                break;

            default:
                sendHelp(sender);
                break;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("§7§nModule Manager");
        sender.sendMessage("§7▪ §e/module list §7- List all modules");
        sender.sendMessage("§7▪ §e/module info <module> §7- Show module information");
        sender.sendMessage("§7▪ §e/module load <jarfile> §7- Load a new module from JAR");
        sender.sendMessage("§7▪ §e/module enable <module> §7- Enable a module");
        sender.sendMessage("§7▪ §e/module disable <module> §7- Disable a module");
        sender.sendMessage("§7▪ §e/module reload [module] §7- Reload module(s)");
        sender.sendMessage("");
    }

    private void listModules(CommandSender sender) {
        if (moduleManager.getModules().isEmpty()) {
            sender.sendMessage(error + "§7No active module found!");
            return;
        }

        sender.sendMessage("");
        sender.sendMessage("§7§nLoaded Modules");
        for (Module module : moduleManager.getModules()) {
            String color = module.isEnabled() ? "§a" : "§c";
            String statusSymbol = module.isEnabled() ? "✓" : "✗";
            String moduleString = module.getModuleData().getName() + " §7| §fv" + module.getModuleData().getVersion() + " §7- §f" + module.getModuleData().getDescription();
            sender.sendMessage("§7▪ " + color + moduleString + " " + statusSymbol);
        }
        sender.sendMessage("");
    }

    private void showModuleInfo(CommandSender sender, String moduleName) {
        Module module = moduleManager.getModule(moduleName);
        if (module == null) {
            sender.sendMessage(error + "§7Module not found: §4" + moduleName);
            return;
        }
        sender.sendMessage("");
        sender.sendMessage("§7§nModule Information");
        sender.sendMessage("§7▪ Name: §f" + module.getName());
        sender.sendMessage("§7▪ Version: §fv" + module.getModuleData().getVersion());
        sender.sendMessage("§7▪ Description: §f" + module.getModuleData().getDescription());

        if (!module.getModuleData().getAuthors().isEmpty()) {
            sender.sendMessage("§7▪ Authors: §f" + String.join(", ", module.getModuleData().getAuthors()));
        }

        if (!module.getModuleData().getDependencies().isEmpty()) {
            sender.sendMessage("§7▪ Dependencies: §f" + String.join(", ", module.getModuleData().getDependencies()));
        }

        String status = module.isEnabled() ? "§a✓ Enabled" : "§c✗ Disabled";
        sender.sendMessage("§7▪ Status: " + status);
        sender.sendMessage("");
    }

    private void loadModule(CommandSender sender, String jarFileName) {
        if (!jarFileName.endsWith(".jar")) {
            jarFileName = jarFileName + ".jar";
        }

        sender.sendMessage(success + "§7Loading module from: §e" + jarFileName);

        if (moduleManager.loadModuleFromFile(jarFileName)) {
            sender.sendMessage(success + "§7Module has been loaded and enabled successfully!");
        } else {
            sender.sendMessage(error + "§7Failed to load module. Check console for details.");
        }
    }

    private void enableModule(CommandSender sender, String moduleName) {
        if (moduleManager.enableModule(moduleName)) {
            sender.sendMessage(success + "§7Module §a" + moduleName + " §7has been enabled.");
        } else {
            sender.sendMessage(error + "§7Failed to enable module: §c" + moduleName);
        }
    }

    private void disableModule(CommandSender sender, String moduleName) {
        if (moduleManager.disableModule(moduleName)) {
            sender.sendMessage(success + "§7Module §c" + moduleName + " §7has been disabled.");
        } else {
            sender.sendMessage(error + "§7Failed to disable module: §c" + moduleName);
        }
    }

    private void reloadModule(CommandSender sender, String moduleName) {
        if (moduleManager.reloadModule(moduleName)) {
            sender.sendMessage(success + "§7Module §e" + moduleName + " §7has been reloaded.");
        } else {
            sender.sendMessage(error + "§7Failed to reload module: §c" + moduleName);
        }
    }

    private void reloadAllModules(CommandSender sender) {
        sender.sendMessage(success + "§7Reloading all modules...");
        moduleManager.reloadModules();
        sender.sendMessage(success + "§7All modules have been reloaded.");
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        List<String> suggestions = List.of("list", "info", "load", "enable", "disable", "reload");

        if (args.length == 0) {
            return suggestions;
        }

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return suggestions.stream()
                    .filter(suggestion -> suggestion.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            String input = args[1].toLowerCase();

            return switch (subCommand) {
                case "info", "enable", "disable", "reload" -> moduleManager.getModules().stream()
                        .map(Module::getName)
                        .filter(name -> name.toLowerCase().startsWith(input))
                        .collect(Collectors.toList());

                case "load" -> {
                    File modulesFolder = new File(plugin.getDataFolder().getParentFile(), "modules");
                    if (modulesFolder.exists() && modulesFolder.isDirectory()) {
                        File[] files = modulesFolder.listFiles((dir, name) -> name.endsWith(".jar"));
                        if (files != null) {
                            yield java.util.Arrays.stream(files)
                                    .map(File::getName)
                                    .filter(name -> name.toLowerCase().startsWith(input))
                                    .collect(Collectors.toList());
                        }
                    }
                    yield Collections.emptyList();
                }

                default -> Collections.emptyList();
            };
        }

        return Collections.emptyList();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return sender.isOp() || sender.hasPermission("ncore.module") || sender instanceof ConsoleCommandSender;
    }

    @Override
    public @Nullable String permission() {
        return "ncore.module";
    }
}

