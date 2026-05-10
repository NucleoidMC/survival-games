package supercoder79.survivalgames.game;

import org.apache.logging.log4j.core.jmx.Server;
import supercoder79.survivalgames.game.config.SurvivalGamesConfig;
import xyz.nucleoid.plasmid.api.game.GameSpace;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

public final class SurvivalGamesSpawnLogic {
    private final GameSpace world;
    private final SurvivalGamesConfig config;

    public SurvivalGamesSpawnLogic(GameSpace world, SurvivalGamesConfig config) {
        this.world = world;
        this.config = config;
    }

    public void resetPlayer(ServerPlayer player, GameType gameMode) {
        player.getInventory().clearContent();
        player.getEnderChestInventory().clearContent();
        player.removeAllEffects();
        player.setHealth(20.0F);
        player.getFoodData().setFoodLevel(20);
        player.fallDistance = 0.0F;
        player.setGameMode(gameMode);
        player.setExperienceLevels(0);
        player.setExperiencePoints(0);
    }

    public void spawnPlayerAtCenter(ServerPlayer player, ServerLevel world) {
        this.spawnPlayerAt(player, 0, 0, world);
    }

    public void spawnPlayerAt(ServerPlayer player, int x, int z, ServerLevel world) {

        ChunkPos chunkPos = new ChunkPos(x >> 4, z >> 4);
        LevelChunk chunk = world.getChunk(chunkPos.x(), chunkPos.z());
        BlockPos pos = new BlockPos(x, chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) + 1, z);

        if (!chunk.getBlockState(pos.below()).getFluidState().isEmpty()) {
            boolean found = false;

            // Try 20 times to spiral outwards, hopefully not hitting fluid
            for (int i = 0; i < 20; i++) {
                if (found) {
                    break;
                }

                int dist = i * 8 + 16;

                int count = 4 + i;
                for (int j = 0; j < count; j++) {
                    double theta = ((double)j / count) * Math.PI * 2;
                    int ax = (int) (Math.cos(theta) * dist) + x;
                    int az = (int) (Math.sin(theta) * dist) + z;

                    ChunkPos circlePos = new ChunkPos(ax >> 4, az >> 4);
                    LevelChunk circleChunk = world.getChunk(circlePos.x(), circlePos.z());
                    pos = new BlockPos(ax, circleChunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ax, az) + 1, az);

                    // Check the position at the circle
                    if (chunk.getBlockState(pos.below()).getFluidState().isEmpty()) {
                        found = true;
                        break;
                    }
                }
            }
        }

        player.teleportTo(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Set.of(), 0.0F, 0.0F, true);
    }
}
