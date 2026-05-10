package supercoder79.survivalgames.game.map.gen;

import kdotjpg.opensimplex.OpenSimplexNoise;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.substrate.gen.MapGen;

public class ThistleGen implements MapGen {
    public static final MapGen INSTANCE = new ThistleGen();
    public static final OpenSimplexNoise ALLIUM_NOISE = new OpenSimplexNoise();
    public static final OpenSimplexNoise LILY_NOISE = new OpenSimplexNoise();

    @Override
    public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        BlockState state = random.nextDouble() < 0.1 ? getFlower(pos) : Blocks.SHORT_GRASS.defaultBlockState();
        boolean grassBelow = world.getBlockState(pos.mutable().below()).getBlock().equals(Blocks.GRASS_BLOCK);
        if (!world.getBlockState(pos).getBlock().equals(Blocks.AIR)) return;
        if (grassBelow) {
            world.setBlock(pos, state, 0);
        } else {
            if (random.nextDouble() < 0.5 && state.getBlock().equals(Blocks.SHORT_GRASS)) world.setBlock(pos, state, 0);
        }
    }

    public BlockState getFlower(BlockPos pos) {
        if (ALLIUM_NOISE.eval(pos.getX() / 64.0, pos.getY() / 64.0) < 0.4) {
            return Blocks.ALLIUM.defaultBlockState();
        } else if (LILY_NOISE.eval(pos.getX() / 64.0, pos.getY() / 64.0) < 0.3) {
            return Blocks.LILY_OF_THE_VALLEY.defaultBlockState();
        }
        return Blocks.ORANGE_TULIP.defaultBlockState();
    }
}
