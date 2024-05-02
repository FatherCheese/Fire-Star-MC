package jamdoggie.firestarmc.command;

import jamdoggie.firestarmc.FireStarMC;
import jamdoggie.firestarmc.multiworld.RegisteredMultiWorld;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class CreateWorldCommand extends Command {
	public CreateWorldCommand() {
		super("createworld", "worldcreate");
	}

	@Override
	public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
		if (strings.length == 0) return false;

		if (strings.length == 2) {
			FireStarMC.worldAPI.initCustomWorld(new RegisteredMultiWorld(strings[0], strings[1].hashCode()));
		}
		return false;
	}

	@Override
	public boolean opRequired(String[] strings) {
		return true;
	}

	@Override
	public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {

	}
}
