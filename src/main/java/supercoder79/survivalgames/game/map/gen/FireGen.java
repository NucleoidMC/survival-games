package supercoder79.survivalgames.game.map.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import xyz.nucleoid.substrate.gen.MapGen;

public class FireGen implements MapGen {
    public static final FireGen INSTANCE = new FireGen();

    @Override
    public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        if (world.getBlockState(mutable.below()) == Blocks.SOUL_SOIL.defaultBlockState() || world.getBlockState(mutable.below(2)) == Blocks.SOUL_SAND.defaultBlockState()) {
            // Spawn soul fire
            world.setBlock(pos, Blocks.SOUL_FIRE.defaultBlockState(), 0);
        } else {
            world.setBlock(pos, Blocks.FIRE.defaultBlockState(), 0);
        }
    }
}
