package jamdoggie.firestarmc.multiworld;

import java.util.ArrayList;

public interface IWorldAPI
{
	ArrayList<RegisteredMultiWorld> getRegisteredWorlds();
	void initCustomWorld(RegisteredMultiWorld world);
}
