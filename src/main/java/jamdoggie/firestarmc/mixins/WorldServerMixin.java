package jamdoggie.firestarmc.mixins;

import jamdoggie.firestarmc.FireStarMC;
import jamdoggie.firestarmc.mixinduckinterfaces.IMinecraftServerMixin;
import jamdoggie.firestarmc.multiworld.CustomWorld;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.save.LevelStorage;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldServer.class, remap = false)
public class WorldServerMixin extends World
{
	public WorldServerMixin(LevelStorage saveHandler, String name, Dimension dimension, WorldType worldType, long seed)
	{
		super(saveHandler, name, dimension, worldType, seed);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(MinecraftServer minecraftserver, LevelStorage isavehandler, String name, int dimensionId, WorldType worldType, long seed, CallbackInfo ci)
	{
			System.out.println("Creating fake dimension with id");

			((WorldMixinAccessor)mixinThis()).setDimension(
				new Dimension("multiworld_" + dimensionId, null, 1.0f, -1));

			dimension.id = dimensionId;
	}

	private World mixinThis()
	{
		return (World)(Object)this;
	}
}
