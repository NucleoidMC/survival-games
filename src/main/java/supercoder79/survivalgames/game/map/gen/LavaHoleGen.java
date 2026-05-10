package supercoder79.survivalgames.game.map.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import xyz.nucleoid.substrate.gen.MapGen;

public class LavaHoleGen implements MapGen {
    public static final LavaHoleGen INSTANCE = new LavaHoleGen();

    @Override
    public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 0);
        world.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), 0);
        world.setBlock(pos.below(2), Blocks.LAVA.defaultBlockState(), 0);
    }
}
