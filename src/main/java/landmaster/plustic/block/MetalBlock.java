package landmaster.plustic.block;

import mcjty.lib.compat.*;
import net.minecraft.block.material.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class MetalBlock extends CompatBlock {
	public MetalBlock(String name) {
		super(Material.IRON);
		this.setHarvestLevel("pickaxe", -1);
		this.setHardness(5);
		this.setUnlocalizedName(name).setRegistryName(name);
	}
	
	@Override
	public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
		return true;
	}
}
