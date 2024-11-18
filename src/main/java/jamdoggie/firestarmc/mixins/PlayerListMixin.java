package jamdoggie.firestarmc.mixins;

import jamdoggie.firestarmc.FireStarMC;
import jamdoggie.firestarmc.mixinduckinterfaces.IMinecraftServerMixin;
import jamdoggie.firestarmc.multiworld.CustomWorld;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.packet.*;
import net.minecraft.core.world.Dimension;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.PlayerList;
import net.minecraft.server.player.PlayerManager;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(value = PlayerList.class, remap = false)
public abstract class PlayerListMixin
{
	@Shadow
	@Final
	private MinecraftServer server;

	@Shadow
	public abstract void func_28172_a(EntityPlayerMP player);

	@Shadow
	public abstract void func_28170_a(EntityPlayerMP entityplayermp, WorldServer worldserver);

	@Shadow
	public abstract void func_30008_g(EntityPlayerMP entityplayermp);

	@Inject(method = "func_28172_a", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/net/PlayerList;getPlayerManager(I)Lnet/minecraft/server/player/PlayerManager;",
		shift = At.Shift.BEFORE))
	private void func_28172_a_INJECT(EntityPlayerMP player, CallbackInfo ci)
	{
		for (CustomWorld customWorld : customWorlds())
		{
			customWorld.playerManager.removePlayer(player);
		}
	}

	@Inject(method = "getPlayerManager", at = @At("HEAD"), cancellable = true)
	private void getPlayerManager(int dim, CallbackInfoReturnable<PlayerManager> cir)
	{
		// Return our custom world instead of a vanilla dimension.
		if (dim >= FireStarMC.worldIndexOffset && dim < FireStarMC.worldIndexOffset + customWorlds().size())
		{
			cir.setReturnValue(customWorlds().get(dim - FireStarMC.worldIndexOffset).playerManager);
		}
	}

	@Inject(method = "onTick", at = @At("TAIL"))
	private void onTick(CallbackInfo ci)
	{
		for (CustomWorld customWorld : customWorlds())
		{
			customWorld.playerManager.tick();
		}
	}

	@Inject(method = "sendPlayerToOtherDimension", at = @At("HEAD"), cancellable = true)
	private void sendPlayerToOtherDimensionInject(EntityPlayerMP player, int targetDim, boolean generatePortal, CallbackInfo ci)
	{
		System.out.println("sendPlayerToOtherDimensionInject: " + player + ", " + targetDim);
		if (targetDim >= FireStarMC.worldIndexOffset || targetDim == FireStarMC.multiWorldDefaultWorldIndex)
		{
			if (targetDim == FireStarMC.multiWorldDefaultWorldIndex)
				targetDim = 0;

			System.out.println("Start of playerlist inject if statement");
			WorldServer oldWorld = this.server.getDimensionWorld(player.dimension);

			player.dimension = targetDim;
			WorldServer newWorld = this.server.getDimensionWorld(player.dimension);

			for (Entity ent : oldWorld.loadedEntityList)
			{
				player.playerNetServerHandler.sendPacket(new Packet29DestroyEntity(ent.id));
			}

			player.playerNetServerHandler.sendPacket(new Packet9Respawn((byte)Dimension.nether.id, (byte) Registries.WORLD_TYPES.getNumericIdOfItem(newWorld.worldType)));
			player.playerNetServerHandler.sendPacket(new Packet9Respawn((byte)Dimension.overworld.id, (byte) Registries.WORLD_TYPES.getNumericIdOfItem(newWorld.worldType)));


			oldWorld.removePlayer(player);
			player.removed = false;

			double playerPosX = player.x;
			double playerPosZ = player.z;

			System.out.println("Player position: " + playerPosX + ", " + player.y + ", " + playerPosZ);

			player.moveTo(
				playerPosX,
				player.y,
				playerPosZ,
				player.yRot,
				player.xRot);

			if (player.isAlive())
			{
				oldWorld.updateEntityWithOptionalForce(player, false);
			}

			if (player.isAlive())
			{
				newWorld.entityJoinedWorld(player);

				player.moveTo(playerPosX, player.y, playerPosZ, player.yRot, player.xRot);

				newWorld.updateEntityWithOptionalForce(player, false);

				player.moveTo(playerPosX, player.y, playerPosZ, player.yRot, player.xRot);
			}

			this.func_28172_a(player);

			player.playerNetServerHandler.teleportAndRotate(player.x, player.y, player.z, player.yRot, player.xRot);
			player.playerNetServerHandler.sendPacket(new Packet41EntityPlayerGamemode(player.id,
				player.getGamemode().getId()));

			player.setWorld(newWorld);

			this.func_28170_a(player, newWorld);
			this.func_30008_g(player);

			player.playerNetServerHandler.sendPacket(new Packet74GameRule(this.server.getDimensionWorld(Dimension.overworld.id).getLevelData().getGameRules()));

			ci.cancel();
		}
	}

	private ArrayList<CustomWorld> customWorlds()
	{
		return ((IMinecraftServerMixin)server).fire_Star_MC$getCustomWorlds();
	}
}
