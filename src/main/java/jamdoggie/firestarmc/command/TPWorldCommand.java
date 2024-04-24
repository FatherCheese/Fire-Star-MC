package jamdoggie.firestarmc.command;

import jamdoggie.firestarmc.FireStarMC;
import jamdoggie.firestarmc.mixinduckinterfaces.IMinecraftServerMixin;
import jamdoggie.firestarmc.multiworld.CustomWorld;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.server.entity.player.EntityPlayerMP;

import java.util.ArrayList;

public class TPWorldCommand extends Command
{
	public TPWorldCommand(String name, String... alts)
	{
		super(name, alts);
	}

	@Override
	public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings)
	{
		if (strings.length == 0)
			return false;

		System.out.println("Teleporting to " + strings[0]);
		commandSender.sendMessage("Teleporting to " + strings[0]);

		if (FireStarMC.mcServer.propertyManager.getStringProperty("level-name", "world").equals(strings[0]))
		{
			FireStarMC.mcServer.playerList.sendPlayerToOtherDimension((EntityPlayerMP)commandSender.getPlayer(), FireStarMC.multiWorldDefaultWorldIndex);
			return true;
		}

		ArrayList<CustomWorld> worlds = ((IMinecraftServerMixin)FireStarMC.mcServer).getCustomWorlds();

		for (int i = 0; i < worlds.size(); i++)
		{
			CustomWorld world = worlds.get(i);
			if (world.name.equals(strings[0]))
			{
				FireStarMC.mcServer.playerList.sendPlayerToOtherDimension((EntityPlayerMP)commandSender.getPlayer(),
					FireStarMC.worldIndexOffset + i);

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean opRequired(String[] strings)
	{
		return true;
	}

	@Override
	public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender)
	{
		commandSender.sendMessage("Usage: /tpworld <worldname>");
	}
}
