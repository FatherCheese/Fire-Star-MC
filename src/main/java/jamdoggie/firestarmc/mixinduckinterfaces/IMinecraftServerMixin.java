package jamdoggie.firestarmc.mixinduckinterfaces;

import jamdoggie.firestarmc.multiworld.CustomWorld;
import net.minecraft.core.world.save.ISaveFormat;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;

public interface IMinecraftServerMixin
{
	void fire_Star_MC$initMultiWorld(ISaveFormat saveFormat, String worldDirName, long l, int dimensionId);
	MinecraftServer getInstance();
	ArrayList<CustomWorld> fire_Star_MC$getCustomWorlds();
}
