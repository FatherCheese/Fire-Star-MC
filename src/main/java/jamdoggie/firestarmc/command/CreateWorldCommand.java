package jamdoggie.firestarmc.command;

import jamdoggie.firestarmc.FireStarMC;
import jamdoggie.firestarmc.multiworld.RegisteredMultiWorld;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandError;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class CreateWorldCommand extends Command {
	public CreateWorldCommand() {
		super("createworld", "worldcreate");
	}

	public int parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			throw new CommandError("Not a number: \"" + str + "\"");
		}
	}

	@Override
	public boolean execute(CommandHandler handler, CommandSender sender, String[] args) {
		if (args.length == 0) return false;

		if (args.length == 3) {
			FireStarMC.worldAPI.initCustomWorld(new RegisteredMultiWorld(args[0], args[1].hashCode(), parseInt(args[2])));
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
