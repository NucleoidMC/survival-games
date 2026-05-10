package supercoder79.survivalgames.entity;

import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;

public final class SpawnerZombieEntity extends Zombie {
    public SpawnerZombieEntity(Level world) {
        super(world);
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }
}
