package supercoder79.survivalgames.game.map.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import xyz.nucleoid.substrate.gen.MapGen;

public class SpringGen implements MapGen {
    public static MapGen INSTANCE = new SpringGen();

    @Override
    public void generate(ServerLevelAccessor world, BlockPos pos, RandomSource random) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        for (int i = 0; i < 12; i++) {
            int dx = random.nextInt(8) - random.nextInt(8) + pos.getX();
            int dz = random.nextInt(8) - random.nextInt(8) + pos.getZ();
            int y = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, dx, dz) - 1;

            mutable.set(dx, y, dz);

            // ensure there is a block under the water
            if (!world.getBlockState(mutable.below()).canOcclude()) {
                continue;
            }

            boolean canSpawn = true;

            // check surrounding for non-opaque blocks
            BlockPos origin = mutable.immutable();
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                mutable.setWithOffset(origin, direction);

                if (!world.getBlockState(mutable).canOcclude()) {
                    if (!world.getFluidState(mutable).is(FluidTags.WATER)) {
                        canSpawn = false;

                        break;
                    }

                }
            }

            if (canSpawn) {
                world.setBlock(mutable.set(origin), Blocks.WATER.defaultBlockState(), 3);
                world.scheduleTick(mutable, world.getFluidState(mutable).getType(), 0);
            }
        }
    }
}
