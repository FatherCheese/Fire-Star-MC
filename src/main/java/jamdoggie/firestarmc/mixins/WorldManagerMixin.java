package jamdoggie.firestarmc.mixins;

import jamdoggie.firestarmc.mixinduckinterfaces.IMinecraftServerMixin;
import jamdoggie.firestarmc.multiworld.CustomWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.WorldManager;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldManager.class, remap = false)
public class WorldManagerMixin
{
	@Shadow
	private MinecraftServer mcServer;

	@Shadow
	private WorldServer worldServer;

	@Inject(method = "blockChanged", at = @At("HEAD"), cancellable = true)
	private void blockChangedMixin(int x, int y, int z, CallbackInfo ci)
	{
		for (CustomWorld customWorld : ((IMinecraftServerMixin)mcServer).getCustomWorlds())
		{
			if (customWorld.world == worldServer)
			{
				this.mcServer.playerList.markBlockNeedsUpdate(x, y, z, customWorld.dimensionId);
				ci.cancel();
				return;
			}
		}
	}
}
