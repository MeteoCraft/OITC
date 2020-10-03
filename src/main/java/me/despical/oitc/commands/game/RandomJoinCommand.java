package me.despical.oitc.commands.game;

import me.despical.oitc.ConfigPreferences;
import me.despical.oitc.arena.Arena;
import me.despical.oitc.arena.ArenaManager;
import me.despical.oitc.arena.ArenaRegistry;
import me.despical.oitc.arena.ArenaState;
import me.despical.oitc.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Despical
 * <p>
 * Created at 02.07.2020
 */
public class RandomJoinCommand extends SubCommand {

	public RandomJoinCommand() {
		super("randomjoin");
	}

	@Override
	public String getPossibleArguments() {
		return null;
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		if (getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
			return;
		}

		Map<Arena, Integer> arenas = new HashMap<>();

		for (Arena arena : ArenaRegistry.getArenas()) {
			if (arena.getArenaState() == ArenaState.STARTING && arena.getPlayers().size() < arena.getMaximumPlayers()) {
				arenas.put(arena, arena.getPlayers().size());
			}
		}

		if (arenas.size() > 0) {
			Stream<Map.Entry<Arena, Integer>> sorted = arenas.entrySet().stream().sorted(Map.Entry.comparingByValue());
			Arena arena = sorted.findFirst().get().getKey();

			if (arena != null) {
				ArenaManager.joinAttempt((Player) sender, arena);
				return;
			}
		}

		for (Arena arena : ArenaRegistry.getArenas()) {
			if ((arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) && arena.getPlayers().size() < arena.getMaximumPlayers()) {
				ArenaManager.joinAttempt((Player) sender, arena);
				return;
			}
		}

		sender.sendMessage(getPlugin().getChatManager().getPrefix() + getPlugin().getChatManager().colorMessage("Commands.No-Free-Arenas"));
	}

	@Override
	public List<String> getTutorial() {
		return null;
	}

	@Override
	public CommandType getType() {
		return CommandType.HIDDEN;
	}

	@Override
	public SenderType getSenderType() {
		return SenderType.PLAYER;
	}
}