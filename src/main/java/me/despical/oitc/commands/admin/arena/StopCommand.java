package me.despical.oitc.commands.admin.arena;

import me.despical.oitc.arena.ArenaManager;
import me.despical.oitc.arena.ArenaRegistry;
import me.despical.oitc.arena.ArenaState;
import me.despical.oitc.commands.SubCommand;
import me.despical.oitc.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * @author Despical
 * <p>
 * Created at 02.07.2020
 */
public class StopCommand extends SubCommand {

	public StopCommand() {
		super("stop");
		setPermission("oitc.admin.stop");
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
		if (!Utils.checkIsInGameInstance((Player) sender)) {
			return;
		}
		
		if (ArenaRegistry.getArena((Player) sender).getArenaState() != ArenaState.ENDING) {
			ArenaManager.stopGame(true, ArenaRegistry.getArena((Player) sender));
		}
	}

	@Override
	public List<String> getTutorial() {
		return Arrays.asList("Stop the arena you're in", "You must be in target arena!");
	}

	@Override
	public CommandType getType() {
		return CommandType.GENERIC;
	}

	@Override
	public SenderType getSenderType() {
		return SenderType.PLAYER;
	}
}