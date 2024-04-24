package jamdoggie.firestarmc.multiworld;

import jamdoggie.firestarmc.FireStarMC;
import jamdoggie.firestarmc.mixinduckinterfaces.IMinecraftServerMixin;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.save.ISaveFormat;
import net.minecraft.core.world.save.SaveHandlerServer;
import net.minecraft.core.world.save.mcregion.SaveFormat19134;
import net.minecraft.server.world.WorldManager;
import net.minecraft.server.world.WorldServer;
import net.minecraft.server.world.WorldServerMulti;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

public class WorldAPI implements IWorldAPI
{
	private ArrayList<RegisteredMultiWorld> worlds = new ArrayList<>();

	@Override
	public ArrayList<RegisteredMultiWorld> getRegisteredWorlds()
	{
		return worlds;
	}

	@Override
	public void initCustomWorld(RegisteredMultiWorld world)
	{
		IMinecraftServerMixin mixinServer = (IMinecraftServerMixin)FireStarMC.mcServer;

		System.out.println("Generating world " + world.name + "...");
		mixinServer.initMultiWorld(new SaveFormat19134(new File(".")), "testWorld", world.seed, world.dimensionId);
		System.out.println("Done!");
	}
}
