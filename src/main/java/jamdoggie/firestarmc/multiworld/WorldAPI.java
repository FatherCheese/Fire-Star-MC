package jamdoggie.firestarmc.multiworld;

import jamdoggie.firestarmc.FireStarMC;
import jamdoggie.firestarmc.mixinduckinterfaces.IMinecraftServerMixin;
import net.minecraft.core.world.save.mcregion.SaveFormat19134;

import java.io.File;
import java.util.ArrayList;

public class WorldAPI implements IWorldAPI {
	private final ArrayList<RegisteredMultiWorld> worlds = new ArrayList<>();

	@Override
	public ArrayList<RegisteredMultiWorld> getRegisteredWorlds() {
		return worlds;
	}

	@Override
	public void initCustomWorld(RegisteredMultiWorld world) {
		IMinecraftServerMixin mixinServer = (IMinecraftServerMixin)FireStarMC.mcServer;

		System.out.println("Generating world " + world.name + "...");
		mixinServer.fire_Star_MC$initMultiWorld(new SaveFormat19134(new File(".")), world.name, world.seed, world.dimensionId);
		System.out.println("Done!");
	}
}
