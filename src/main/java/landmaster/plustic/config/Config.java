package landmaster.plustic.config;

import net.minecraftforge.common.config.*;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config extends Configuration {
	public boolean bop;
	public boolean mekanism;
	
	public Config(FMLPreInitializationEvent event) {
		super(event.getSuggestedConfigurationFile());
	}
	
	public void sync() {
		bop = getBoolean("Enable BoP integration", "modules", true, "Integrate with Biomes o' Plenty");
		mekanism = getBoolean("Enable Mekanism integration", "modules", true, "Integrate with Mekanism");
		if (hasChanged()) save();
	}
}
