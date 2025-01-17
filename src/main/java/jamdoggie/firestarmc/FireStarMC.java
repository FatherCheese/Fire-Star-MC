package jamdoggie.firestarmc;

import jamdoggie.firestarmc.command.TPWorldCommand;
import jamdoggie.firestarmc.multiworld.IWorldAPI;
import jamdoggie.firestarmc.multiworld.RegisteredMultiWorld;
import jamdoggie.firestarmc.multiworld.WorldAPI;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.CommandHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;


public class FireStarMC implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "firestarmc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftServer mcServer;
	public static IWorldAPI worldAPI;
	public static final int worldIndexOffset = 500;
	public static final int multiWorldDefaultWorldIndex = 499;

    @Override
    public void onInitialize() {
        LOGGER.info("Starting Fire Star MC.");
		worldAPI = new WorldAPI();

		CommandHelper.createServerCommand(minecraftAtomicReference -> new TPWorldCommand("tpworld"));

		worldAPI.getRegisteredWorlds().add(new RegisteredMultiWorld("testWorld", "testSeed".hashCode(), worldIndexOffset));
    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

	}
}
