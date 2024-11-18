package jamdoggie.firestarmc.mixins;

import jamdoggie.firestarmc.FireStarMC;
import jamdoggie.firestarmc.mixinduckinterfaces.IMinecraftServerMixin;
import jamdoggie.firestarmc.multiworld.CustomWorld;
import jamdoggie.firestarmc.multiworld.RegisteredMultiWorld;
import net.minecraft.core.net.PropertyManager;
import net.minecraft.core.net.packet.Packet4UpdateTime;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.save.ISaveFormat;
import net.minecraft.core.world.save.SaveHandlerServer;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.WorldTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.EntityTracker;
import net.minecraft.server.net.PlayerList;
import net.minecraft.server.player.PlayerManager;
import net.minecraft.server.world.WorldManager;
import net.minecraft.server.world.WorldServer;
import org.apache.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.ArrayList;

@Mixin(value = net.minecraft.server.MinecraftServer.class, remap = false)
public abstract class MinecraftServerMixin implements IMinecraftServerMixin
{
	@Shadow
	protected abstract void convertWorld(ISaveFormat saveFormat, String worldDirName);

	@Shadow
	public WorldServer[] dimensionWorlds;

	@Shadow
	public PlayerList playerList;

	@Shadow
	public int sleepPercentage;

	@Shadow
	public int difficulty;

	@Shadow
	public PropertyManager propertyManager;

	@Shadow
	public static Logger logger;

	@Shadow
	private boolean serverRunning;

	@Shadow
	public WorldType defaultWorldType;

	@Shadow
	public boolean spawnPeacefulMobs;

	@Shadow
	protected abstract void outputPercentRemaining(String s, int i);

	@Shadow
	protected abstract void clearCurrentTask();

	@Shadow
	int deathTime;

	@Unique
	public ArrayList<CustomWorld> multiWorldList = new ArrayList<>();

	@Unique
	private final int multiWorldIndex = FireStarMC.worldIndexOffset;

	@Inject(method = "startServer", at =
	@At(value = "INVOKE",
		target = "Lnet/minecraft/server/MinecraftServer;initWorld(Lnet/minecraft/core/world/save/ISaveFormat;Ljava/lang/String;J)V",
		shift = At.Shift.AFTER))
	private void startServerMixin(CallbackInfoReturnable<Boolean> cir) {
		// TODO: load worlds here
		for (RegisteredMultiWorld savedWorld : FireStarMC.worldAPI.getRegisteredWorlds()) {
			FireStarMC.worldAPI.initCustomWorld(savedWorld);
		}
	}

	@Unique
	public void fire_Star_MC$initMultiWorld(ISaveFormat saveFormat, String worldDirName, long seed, int dimID) {
		this.convertWorld(saveFormat, worldDirName);
		SaveHandlerServer saveHandler = new SaveHandlerServer(saveFormat, new File("."), worldDirName, true);

		WorldServer world;

		world = new WorldServer(thisAs(), saveHandler, worldDirName, dimID, WorldTypes.OVERWORLD_AMPLIFIED, seed);

		int viewDist = propertyManager.getIntProperty("view-distance", 10);

		CustomWorld customWorld = new CustomWorld("testWorld",
			world,
			new PlayerManager(thisAs(), dimID, viewDist),
			new EntityTracker(thisAs(), dimID),
			dimID);

		multiWorldList.add(customWorld);

		world.addListener(new WorldManager(thisAs(), world));
		world.difficultySetting = this.difficulty;
		world.sleepPercent = this.sleepPercentage;
		world.setAllowedMobSpawns(this.propertyManager.getBooleanProperty("spawn-monsters", true), this.spawnPeacefulMobs);

		int c = 196;
		long preGenTimeStamp = System.currentTimeMillis();

		System.out.println("Preparing start region for level " + dimID);

		ChunkCoordinates chunkcoordinates = world.getSpawnPoint();

		for (int x = -c; x <= c && this.serverRunning; x += 16)
		{
			for (int y = -c; y <= c && this.serverRunning; y += 16)
			{
				long timeStamp = System.currentTimeMillis();

				if (timeStamp < preGenTimeStamp) // How tf could this even happen?? I guess if the system clock is set?
				{
					preGenTimeStamp = timeStamp;
				}

				// We wait one second before updating the progress for some reason. Okay.
				if (timeStamp > preGenTimeStamp + 1000L)
				{
					int j1 = (c * 2 + 1) * (c * 2 + 1);
					int k1 = (x + c) * (c * 2 + 1) + (y + 1);
					this.outputPercentRemaining("Preparing spawn area", k1 * 100 / j1);
					preGenTimeStamp = timeStamp;
				}

				world.chunkProviderServer.prepareChunk(chunkcoordinates.x + x >> 4, chunkcoordinates.z + y >> 4);

				while (world.updatingLighting() && this.serverRunning) { }
			}
		}

		this.clearCurrentTask();
	}

	@Inject(method = "getDimensionWorld", at = @At("HEAD"), cancellable = true)
	private void getDimensionWorldMixin(int dim, CallbackInfoReturnable<WorldServer> cir)
	{
		if (dim >= FireStarMC.worldIndexOffset && dim < FireStarMC.worldIndexOffset + multiWorldList.size())
		{
			for (CustomWorld customWorld : multiWorldList)
			{
				if (customWorld.dimensionId == dim)
				{
					cir.setReturnValue(customWorld.world);
					return;
				}
			}
		}
	}

	@Inject(method = "doTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/net/PlayerList;onTick()V", shift = At.Shift.AFTER))
	private void tickEntityTrackers(CallbackInfo ci)
	{
		for (CustomWorld customWorld : multiWorldList)
		{
			customWorld.entityTracker.tick();
		}
	}

	@Inject(method = "getEntityTracker", at = @At("HEAD"), cancellable = true)
	private void getEntityTrackerMixin(int i, CallbackInfoReturnable<EntityTracker> cir)
	{
		if (i >= FireStarMC.worldIndexOffset && i < FireStarMC.worldIndexOffset + multiWorldList.size())
		{
			cir.setReturnValue(multiWorldList.get(i - FireStarMC.worldIndexOffset).entityTracker);
		}
	}

	@Inject(method = "saveServerWorld", at = @At("TAIL"))
	private void saveWorld(CallbackInfo ci)
	{
		for (CustomWorld customWorld : multiWorldList)
		{
			WorldServer world = customWorld.world;
			world.saveWorld(true, null, true);
			world.func_30006_w();
		}
	}

	@Inject(method = "doTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/net/NetworkListenThread;handleNetworkListenThread()V", shift = At.Shift.BEFORE))
	private void tickInject(CallbackInfo ci)
	{
		for (CustomWorld customWorld : multiWorldList)
		{
			WorldServer worldserver = customWorld.world;

			if (this.deathTime % 20 == 0)
			{
				this.playerList.sendPacketToAllPlayersInDimension(new Packet4UpdateTime(worldserver.getWorldTime()), customWorld.dimensionId); // TODO: investigate if sending our custom dimension id is necessary.
			}

			worldserver.tick();
			while (worldserver.updatingLighting()) { }
			worldserver.updateEntities();
		}
	}

	public ArrayList<CustomWorld> fire_Star_MC$getCustomWorlds()
	{
		return multiWorldList;
	}

	@Inject(method = "startServer", at = @At("HEAD"))
	private void staticInstanceMixin(CallbackInfoReturnable<Boolean> cir)
	{
		FireStarMC.mcServer = thisAs();
	}

	@Unique
	private MinecraftServer thisAs()
	{
		return (MinecraftServer)(Object)this;
	}
}
