package jamdoggie.firestarmc.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import jamdoggie.firestarmc.FireStarMC;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.Packet1Login;
import net.minecraft.core.world.Dimension;
import net.minecraft.server.net.handler.NetLoginHandler;
import net.minecraft.server.net.handler.NetServerHandler;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetLoginHandler.class, remap = false)
public class NetLoginHandlerMixin
{
	@ModifyArg(method = "doLogin", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/net/packet/Packet1Login;<init>(Ljava/lang/String;IJBBILjava/lang/String;)V",
		ordinal = 0),
	index = 3)
	private byte modifyLoginPacketArg(byte dimensionId, @Local WorldServer worldServer)
	{
		if (worldServer.dimension.id >= FireStarMC.worldIndexOffset)
		{
			System.out.println("Spoofing dimension id to " + Dimension.overworld.id + " for custom world.");
			return (byte) Dimension.overworld.id;
		}

		return dimensionId;
	}
}
