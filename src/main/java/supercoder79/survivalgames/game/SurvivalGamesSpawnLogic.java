package supercoder79.survivalgames.game;

import java.util.Random;

import supercoder79.survivalgames.game.config.SurvivalGamesConfig;
import xyz.nucleoid.plasmid.game.GameWorld;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.Heightmap;

public final class SurvivalGamesSpawnLogic {
    private final GameWorld world;
    private final SurvivalGamesConfig config;

    public SurvivalGamesSpawnLogic(GameWorld world, SurvivalGamesConfig config) {
        this.world = world;
        this.config = config;
    }

    public void resetPlayer(ServerPlayerEntity player, GameMode gameMode) {
        player.inventory.clear();
        player.getEnderChestInventory().clear();
        player.clearStatusEffects();
        player.setHealth(20.0F);
        player.getHungerManager().setFoodLevel(20);
        player.fallDistance = 0.0F;
        player.setGameMode(gameMode);
    }

    public void spawnPlayer(ServerPlayerEntity player) {
        ServerWorld world = this.world.getWorld();

        // TODO: trig distribution
        Random random = world.getRandom();
        int x = random.nextInt(config.borderConfig.startSize) - (config.borderConfig.startSize / 2);
        int z = random.nextInt(config.borderConfig.startSize) - (config.borderConfig.startSize / 2);
        BlockPos pos = new BlockPos(x, world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, x, z), z);

        ChunkPos chunkPos = new ChunkPos(pos);
        world.getChunkManager().addTicket(ChunkTicketType.field_19347, chunkPos, 1, player.getEntityId());

        // Get the y position by using this amazing hack
        // TODO: fix
        BlockPos.Mutable mutable = pos.mutableCopy();
        mutable.setY(256);
        for (int y = 256; y > 0; y--) {
            if (world.getBlockState(mutable.set(x, y, z)).isOpaque()) {
                break;
            }
        }

        pos = mutable.up(2).toImmutable();

        player.teleport(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0F, 0.0F);
    }
}
