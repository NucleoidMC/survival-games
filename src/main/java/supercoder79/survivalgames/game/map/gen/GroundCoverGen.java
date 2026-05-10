package supercoder79.survivalgames.game.map.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.substrate.gen.MapGen;

public class GroundCoverGen implements MapGen {
    public static MapGen SNOW = new GroundCoverGen(Blocks.SNOW.defaultBlockState(), 1);
    private final BlockState state;
    private final int chance;

    public GroundCoverGen(BlockState state, int chance) {
        this.state = state;
        this.chance = chance;
    }

    @Override
    public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        if (random.nextInt(chance) == 0 && world.isEmptyBlock(pos)) {
            world.setBlock(pos, state, 0);
        }
    }
}
