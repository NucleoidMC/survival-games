package supercoder79.survivalgames.game.map.gen;

import xyz.nucleoid.substrate.gen.MapGen;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BranchingTreeGen implements MapGen {
    public static final MapGen DARK_OAK = new BranchingTreeGen(Blocks.DARK_OAK_LOG.defaultBlockState(), Blocks.DARK_OAK_LEAVES.defaultBlockState().setValue(BlockStateProperties.DISTANCE, 1), Blocks.GRASS_BLOCK.defaultBlockState(), 8, 1);
    public static final MapGen JUNGLE = new BranchingTreeGen(Blocks.JUNGLE_LOG.defaultBlockState(), Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(BlockStateProperties.DISTANCE, 1), Blocks.GRASS_BLOCK.defaultBlockState(), 16, 1);
    public static final MapGen ACACIA = new BranchingTreeGen(Blocks.ACACIA_LOG.defaultBlockState(), Blocks.ACACIA_LEAVES.defaultBlockState().setValue(BlockStateProperties.DISTANCE, 1), Blocks.GRASS_BLOCK.defaultBlockState(), 8, 3);
    public static final MapGen WARPED = new BranchingTreeGen(Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.WARPED_NYLIUM.defaultBlockState(), 12, 0);
    public static final MapGen CRIMSON = new BranchingTreeGen(Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.CRIMSON_NYLIUM.defaultBlockState(), 12, 0);
    public static final MapGen BONE = new BranchingTreeGen(Blocks.BONE_BLOCK.defaultBlockState(), Blocks.AIR.defaultBlockState(), Blocks.SOUL_SOIL.defaultBlockState(), 4, 1);
    public static final MapGen BASALT_COLUMN = new BranchingTreeGen(Blocks.BASALT.defaultBlockState(), Blocks.AIR.defaultBlockState(), Blocks.BASALT.defaultBlockState(), 6, 0);

    private final BlockState log;
    private final BlockState leaves;
    private final BlockState plantable;
    private final int height;
    private final int branchLength;

    public BranchingTreeGen(BlockState log, BlockState leaves, BlockState plantable, int height, int branchLength) {
        this.log = log;
        this.leaves = leaves;
        this.height = height;
        this.branchLength = branchLength;
        this.plantable = plantable;
    }

    @Override
    public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        if (world.getBlockState(pos.below()) != this.plantable) return;

        int height = this.height + random.nextInt(Math.max(1, this.height / 4));
        int branchThreshold = (int) (height * 0.4);
        List<BlockPos> leaves = new ArrayList<>();

        BlockPos.MutableBlockPos mutable = pos.mutable();
        for (int y = 0; y <= height; y++) {
            world.setBlock(mutable, this.log, 3);

            if (y > branchThreshold && random.nextInt(2) == 0) {
                BlockPos local = mutable.immutable();
                double theta = random.nextDouble() * Math.PI * 2;
                // TODO: scale with height
                int branchLength = random.nextInt(3) + this.branchLength;
                for (int i = 0; i <= branchLength; i++) {
                    int dx = (int) (Math.cos(theta) * i);
                    int dy = i / 2;
                    int dz = (int) (Math.sin(theta) * i);

                    world.setBlock(local.offset(dx, dy, dz), this.log, 3);

                    if (i == branchLength) {
                        leaves.add(local.offset(dx, dy, dz).immutable());
                    }
                }
            }

            if (y == height) {
                BlockPos local = mutable.immutable();
                int topCount = 2 + random.nextInt(3);

                for (int i = 0; i < topCount; i++) {

                    double theta = (i / (double) topCount) * Math.PI * 2;
                    theta += random.nextDouble() * 0.3;

                    int branchLength = random.nextInt(4) + 2;
                    for (int j = 0; i <= branchLength; i++) {
                        int dx = (int) (Math.cos(theta) * j);
                        int dz = (int) (Math.sin(theta) * j);

                        world.setBlock(local.offset(dx, j, dz), this.log, 3);

                        if (i == branchLength) {
                            leaves.add(local.offset(dx, j, dz).immutable());
                        }
                    }
                }

                break;
            }

            mutable.move(Direction.UP);
        }

        for (BlockPos leaf : leaves) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos local = leaf.offset(x, 1, z);
                    if (world.getBlockState(local).isAir()) {
                        world.setBlock(local, this.leaves, 3);
                    }
                }
            }

            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (Math.abs(x) == 2 && Math.abs(z) == 2) continue;

                    BlockPos local = leaf.offset(x, 0, z);
                    if (world.getBlockState(local).isAir()) {
                        world.setBlock(local, this.leaves, 3);
                    }
                }
            }
        }
    }
}
