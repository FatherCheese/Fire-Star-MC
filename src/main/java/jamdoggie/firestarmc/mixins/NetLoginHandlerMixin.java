package jamdoggie.firestarmc.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import jamdoggie.firestarmc.FireStarMC;
import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.world.Dimension;
import net.minecraft.server.net.handler.NetLoginHandler;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value = NetLoginHandler.class, remap = false)
public abstract class NetLoginHandlerMixin extends NetHandler
{
	@ModifyArg(method = "doLogin", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/net/packet/Packet1Login;<init>(Ljava/lang/String;IJBBILjava/lang/String;)V",
		ordinal = 0), index = 3)
	private byte fire_Star_MC$modifyLoginPacketArg(byte dimensionId, @Local WorldServer worldServer) {
		if (worldServer.dimension.id >= FireStarMC.WORLD_INDEX_OFFSET) {
			FireStarMC.LOGGER.info("Spoofing dimension id to {} for custom world.", Dimension.overworld.id);
			return (byte) Dimension.overworld.id;
		}

		return dimensionId;
	}
}
