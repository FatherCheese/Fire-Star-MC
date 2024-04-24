package jamdoggie.firestarmc.mixins;

import net.minecraft.core.world.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Dimension.class, remap = false)
public class DimensionMixin
{
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	private void init(String languageKey, Dimension homeDim, float worldScale, int portalBlockId, CallbackInfo ci)
	{

	}
}
