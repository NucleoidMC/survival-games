package supercoder79.survivalgames.game.map.biome.nether;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.ServerLevelAccessor;
import xyz.nucleoid.substrate.gen.MapGen;

public class PiglinGen implements MapGen {
    public static final PiglinGen INSTANCE = new PiglinGen();

    @Override
    public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        Piglin piglin = new Piglin(EntityType.PIGLIN, world.getLevel());
        piglin.snapTo(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
        piglin.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), EntitySpawnReason.CHUNK_GENERATION, null);
        world.addFreshEntity(piglin);
    }
}
