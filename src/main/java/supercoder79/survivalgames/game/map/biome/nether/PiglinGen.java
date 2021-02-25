package supercoder79.survivalgames.game.map.biome.nether;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import xyz.nucleoid.plasmid.game.gen.MapGen;

import java.util.Random;

public class PiglinGen implements MapGen {
    public static final PiglinGen INSTANCE = new PiglinGen();

    @Override
    public void generate(ServerWorldAccess world, BlockPos pos, Random random) {
        PiglinEntity piglin = new PiglinEntity(EntityType.PIGLIN, world.toServerWorld());
        piglin.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
        piglin.initialize(world.toServerWorld(), world.getLocalDifficulty(pos), SpawnReason.CHUNK_GENERATION, null, null);
        world.spawnEntity(piglin);
    }
}
