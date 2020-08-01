package me.despical.oitc.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.despical.commonsbox.string.StringMatcher;
import me.despical.oitc.Main;
import me.despical.oitc.commands.SubCommand.SenderType;
import me.despical.oitc.commands.admin.HelpCommand;
import me.despical.oitc.commands.admin.ListCommand;
import me.despical.oitc.commands.admin.arena.DeleteCommand;
import me.despical.oitc.commands.admin.arena.EditCommand;
import me.despical.oitc.commands.admin.arena.ForceStartCommand;
import me.despical.oitc.commands.admin.arena.ReloadCommand;
import me.despical.oitc.commands.admin.arena.StopCommand;
import me.despical.oitc.commands.exception.CommandException;
import me.despical.oitc.commands.game.CreateCommand;
import me.despical.oitc.commands.game.JoinCommand;
import me.despical.oitc.commands.game.LeaderBoardCommand;
import me.despical.oitc.commands.game.LeaveCommand;
import me.despical.oitc.commands.game.RandomJoinCommand;
import me.despical.oitc.commands.game.StatsCommand;

/**
 * @author Despical
 * <p>
 * Created at 02.07.2020
 */
public class CommandHandler implements CommandExecutor {

	private final List<SubCommand> subCommands;
	private final Main plugin;

	public CommandHandler(Main plugin) {
		this.plugin = plugin;
		subCommands = new ArrayList<>();

		registerSubCommand(new CreateCommand());
		registerSubCommand(new EditCommand());
		registerSubCommand(new DeleteCommand());
		registerSubCommand(new ReloadCommand());
		registerSubCommand(new ListCommand());
		registerSubCommand(new HelpCommand());
		registerSubCommand(new ForceStartCommand());
		registerSubCommand(new StopCommand());
		registerSubCommand(new JoinCommand());
		registerSubCommand(new RandomJoinCommand());
		registerSubCommand(new LeaveCommand());
		registerSubCommand(new StatsCommand());
		registerSubCommand(new LeaderBoardCommand());

		TabCompletion tabCompletion = new TabCompletion(this);
		plugin.getCommand("oitc").setExecutor(this);
		plugin.getCommand("oitc").setTabCompleter(tabCompletion);
	}
	
	public void registerSubCommand(SubCommand subCommand) {
		subCommands.add(subCommand);
	}
	
	public List<SubCommand> getSubCommands() {
		return new ArrayList<>(subCommands);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.DARK_AQUA + "This server is running " + ChatColor.AQUA + "One in the Chamber " + ChatColor.DARK_AQUA + "v" + this.plugin.getDescription().getVersion() + " by " + ChatColor.AQUA + "Despical");
			if (sender.hasPermission("oitc.admin")) {
				sender.sendMessage(ChatColor.DARK_AQUA + "Commands: " + ChatColor.AQUA + "/" + label + " help");
			}
			return true;
		}
		for (SubCommand subCommand : subCommands) {
			if (subCommand.isValidTrigger(args[0])) {
				if (!subCommand.hasPermission(sender)) {
					sender.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Commands.No-Permission"));
					return true;
				}
				if (subCommand.getSenderType() == SenderType.PLAYER && !(sender instanceof Player)) {
					sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Only-By-Player"));
					return false;
				}
				if (args.length - 1 >= subCommand.getMinimumArguments()) {
					try {
						subCommand.execute(sender, label, Arrays.copyOfRange(args, 1, args.length));
					} catch (CommandException e) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
					}
				} else {
					if (subCommand.getType() == SubCommand.CommandType.GENERIC) {
						sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + subCommand.getName() + " " + (subCommand.getPossibleArguments().length() > 0 ? subCommand.getPossibleArguments() : ""));
					}
				}
				return true;
			}
		}
		List<StringMatcher.Match> matches = StringMatcher.match(args[0], subCommands.stream().map(SubCommand::getName).collect(Collectors.toList()));
        if (!matches.isEmpty()) {
          sender.sendMessage(plugin.getChatManager().colorMessage("Commands.Did-You-Mean").replace("%command%", label + " " + matches.get(0).getMatch()));
          return true;
        }
        return true;
	}
}