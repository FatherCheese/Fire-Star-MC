package jamdoggie.firestarmc.command;

import jamdoggie.firestarmc.FireStarMC;
import jamdoggie.firestarmc.mixinduckinterfaces.IMinecraftServerMixin;
import jamdoggie.firestarmc.multiworld.CustomWorld;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.server.entity.player.EntityPlayerMP;

import java.util.ArrayList;

public class TPWorldCommand extends Command
{
	public TPWorldCommand(String name, String... alts)
	{
		super(name, alts);
	}

	@Override
	public boolean execute(CommandHandler handler, CommandSender sender, String[] strings) {
		EntityPlayer player = sender.getPlayer();
		if (strings.length == 0) return false;

		System.out.println("Teleporting " + sender.getPlayer().username + " to world " + strings[0]);
		sender.sendMessage("Teleporting to " + strings[0]);

		if (player instanceof EntityPlayerMP) {
			EntityPlayerMP playerMP = (EntityPlayerMP) player;

			if (FireStarMC.mcServer.propertyManager.getStringProperty("level-name", "world").equals(strings[0])) {
				FireStarMC.mcServer.playerList.sendPlayerToOtherDimension(playerMP, FireStarMC.multiWorldDefaultWorldIndex, false);

				World world = handler.getWorld(player);
				ChunkCoordinates pos = world.getSpawnPoint();

				playerMP.playerNetServerHandler.teleportAndRotate((double) pos.x + 0.5, (double) pos.y + 1, (double) pos.z + 0.5, 0.0F, 0.0F);

				return true;
			}
		}

		ArrayList<CustomWorld> worlds = ((IMinecraftServerMixin)FireStarMC.mcServer).fire_Star_MC$getCustomWorlds();

		for (int i = 0; i < worlds.size(); i++) {
			CustomWorld world = worlds.get(i);
			ChunkCoordinates pos = world.world.getSpawnPoint();

			if (world.name.equals(strings[0])) {
				if (player instanceof EntityPlayerMP) {
					EntityPlayerMP playerMP = (EntityPlayerMP) player;

					FireStarMC.mcServer.playerList.sendPlayerToOtherDimension(playerMP,
						FireStarMC.worldIndexOffset + i,
						false);

					playerMP.playerNetServerHandler.teleportAndRotate((double) pos.x + 0.5, (double) pos.y + 1, (double) pos.z + 0.5, 0.0F, 0.0F);

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean opRequired(String[] strings)
	{
		return false;
	}

	@Override
	public void sendCommandSyntax(CommandHandler handler, CommandSender sender)
	{
		sender.sendMessage("Usage: /tpworld <worldname>");
	}
}
