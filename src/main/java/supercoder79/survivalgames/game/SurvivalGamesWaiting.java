package supercoder79.survivalgames.game;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.clock.ClockState;
import net.minecraft.world.clock.PackedClockStates;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import supercoder79.survivalgames.SurvivalGames;
import supercoder79.survivalgames.game.config.SurvivalGamesConfig;
import supercoder79.survivalgames.game.map.SurvivalGamesMap;
import xyz.nucleoid.fantasy.RuntimeLevelConfig;
import xyz.nucleoid.plasmid.api.game.GameOpenContext;
import xyz.nucleoid.plasmid.api.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.api.game.GameResult;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;

import java.util.Map;

public final class SurvivalGamesWaiting {
	private final GameSpace world;
	private final SurvivalGamesMap map;
	private final SurvivalGamesConfig config;
	private final GenerationTracker tracker;

	private SurvivalGamesWaiting(GameSpace world, SurvivalGamesMap map, SurvivalGamesConfig config, GenerationTracker tracker) {
		this.world = world;
		this.map = map;
		this.config = config;
		this.tracker = tracker;
	}

	public static GameOpenProcedure open(GameOpenContext<SurvivalGamesConfig> context) {
		GenerationTracker tracker = new GenerationTracker();
		SurvivalGamesMap map = new SurvivalGamesMap();
		var dimKey = ResourceKey.create(Registries.DIMENSION_TYPE, context.config().dimension());
		RuntimeLevelConfig worldConfig = new RuntimeLevelConfig()
				.setGenerator(map.chunkGenerator(context.server(), context.config(), tracker))
				//.setSpawner(BubbleLevelSpawner.atSurface(0, 0))
				.setDimensionType(dimKey);

		context.server().registryAccess().get(dimKey).flatMap(x -> x.value().defaultClock())
				.ifPresent(c -> worldConfig.setClockManagerConstructor(
						new PackedClockStates(Map.of(c, new ClockState(context.config().time(), 0, 0, true)
        ))));

		return context.openWithLevel(worldConfig, (game, world) -> {
			SurvivalGamesWaiting waiting = new SurvivalGamesWaiting(game.getGameSpace(), map, context.config(), tracker);
			GameWaitingLobby.addTo(game, context.config().playerConfig());
			var height = world.getChunk(0, 0).getOrCreateHeightmapUnprimed(Heightmap.Types.MOTION_BLOCKING).getFirstAvailable(0, 0);

			game.allow(SurvivalGames.DISABLE_SPAWNERS);
			game.listen(GameActivityEvents.REQUEST_START, () -> waiting.requestStart(world));
			game.listen(GamePlayerEvents.REMOVE, waiting::onPlayerDeath);
			game.listen(GamePlayerEvents.ACCEPT, offer -> offer.teleport(world, new Vec3(0, height, 0)));
		});
	}

	private GameResult requestStart(ServerLevel world) {
		SurvivalGamesActive.open(this.world, this.map, this.config, world, this.tracker);
		return GameResult.ok();
	}

	private void onPlayerDeath(ServerPlayer player) {
	}
}
