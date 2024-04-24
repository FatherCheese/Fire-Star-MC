package jamdoggie.firestarmc.mixins;

import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = World.class, remap = false)
public interface WorldMixinAccessor
{
	@Mutable
	@Accessor("dimension")
	void setDimension(Dimension dimension);
}
