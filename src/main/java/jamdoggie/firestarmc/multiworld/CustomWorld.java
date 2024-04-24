package jamdoggie.firestarmc.multiworld;

import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.type.WorldTypes;
import net.minecraft.server.entity.EntityTracker;
import net.minecraft.server.player.PlayerManager;
import net.minecraft.server.world.WorldServer;

public class CustomWorld
{
	public String name;
	public WorldServer world;
	public PlayerManager playerManager;
	public EntityTracker entityTracker;
	public int dimensionId;
	public Dimension dimension;

	public CustomWorld(String name, WorldServer world, PlayerManager playerManager, EntityTracker entityTracker, int dimensionId)
	{
		this.name = name;
		this.world = world;
		this.playerManager = playerManager;
		this.entityTracker = entityTracker;
		this.dimensionId = dimensionId;
		this.dimension = new Dimension("customWorld" + dimensionId, null, 1.0f, -1)
			.setDefaultWorldType(WorldTypes.FLAT);
	}
}
