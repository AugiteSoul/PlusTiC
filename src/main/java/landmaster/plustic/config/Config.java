package landmaster.plustic.config;

import java.util.*;

import com.google.common.collect.*;

import gnu.trove.*;
import gnu.trove.list.*;
import gnu.trove.list.array.*;
import landmaster.plustic.traits.*;
import mcjty.lib.tools.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.common.config.*;
import net.minecraftforge.fml.common.event.*;

public class Config extends Configuration {
	public static boolean base;
	public static boolean bop;
	public static boolean projectRed;
	public static boolean mekanism;
	public static boolean botania;
	public static boolean advancedRocketry;
	public static boolean armorPlus;
	public static boolean enderIO;
	public static boolean thermalFoundation;
	public static boolean draconicEvolution;
	public static boolean actuallyAdditions;
	public static boolean substratum;
	public static boolean natura;
	public static boolean psi;
	public static boolean avaritia;
	public static boolean landCraft;
	public static boolean landCore;
	
	public static boolean pyrotheumSmelt;
	
	public static boolean katana;
	public static boolean laserGun;
	
	public static float katana_combo_multiplier;
	
	private static final TIntArrayList botan_amount = new TIntArrayList(Botanical.MAX_LEVELS);
	
	public static TIntList getBotanAmount() {
		return TCollections.unmodifiableList(botan_amount);
	}
	
	private static class TrashThing {
		public final int weight;
		public final ItemStack stack;
		
		public TrashThing(int weight, ItemStack stack) {
			this.weight = weight;
			this.stack = stack;
		}
	}
	
	private static final List<TrashThing> trashThings = new ArrayList<>();
	
	private static int trashThingsSum = 0;
	public static @javax.annotation.Nullable ItemStack fetchThing(Random random) {
		if (trashThingsSum <= 0) {
			trashThingsSum = trashThings.stream().mapToInt(t -> t.weight).sum();
		}
		int rval = random.nextInt(trashThingsSum);
		ItemStack thing = ItemStackTools.getEmptyStack();
		for (TrashThing entry: trashThings) {
			rval -= entry.weight;
			thing = entry.stack;
			if (rval < 0) break;
		}
		return thing;
	}
	
	private static final ArrayListMultimap<Item, ItemStack> endlectricBlacklist = ArrayListMultimap.create();
	
	public Config(FMLPreInitializationEvent event) {
		super(event.getSuggestedConfigurationFile());
	}
	
	public void sync() {
		// MODULES
		base = getBoolean("Enable vanilla TiC addons", "modules", true, "Add features to vanilla Tinkers Construct");
		bop = getBoolean("Enable BoP integration", "modules", true, "Integrate with Biomes o' Plenty");
		projectRed = getBoolean("Enable Project Red integration", "modules", true, "Integrate with Project Red-Exploration");
		mekanism = getBoolean("Enable Mekanism integration", "modules", true, "Integrate with Mekanism");
		botania = getBoolean("Enable Botania integration", "modules", true, "Integrate with Botania");
		advancedRocketry = getBoolean("Enable Advanced Rocketry integration", "modules", true, "Integrate with Advanced Rocketry (actually LibVulpes)");
		armorPlus = getBoolean("Enable ArmorPlus integration", "modules", true, "Integrate with ArmorPlus");
		enderIO = getBoolean("Enable EnderIO integration", "modules", true, "Integrate with EnderIO");
		thermalFoundation = getBoolean("Enable Thermal Foundation integration", "modules", true, "Integrate with Thermal Foundation");
		{
			pyrotheumSmelt = getBoolean("Use Pyrotheum as smeltery fuel", "tweaks", true, "Use Pyrotheum as TiC smeltery fuel (only if Thermal Foundation is loaded)");
		}
		draconicEvolution = getBoolean("Enable Draconic Evolution integration", "modules", true, "Integrate with Draconic Evolution");
		actuallyAdditions = getBoolean("Enable Actually Additions support", "modules", true, "Integrate with Actually Additions");
		substratum = getBoolean("Enable Substratum support", "modules", true, "Integrate with Substratum");
		natura = getBoolean("Enable Natura support", "modules", true, "Integrate with Natura");
		psi = getBoolean("Enable Psi support", "modules", true, "Integrate with Psi");
		avaritia = getBoolean("Enable Avaritia support", "modules", true, "Integrate with Avaritia");
		landCraft = getBoolean("Enable Land Craft support", "modules", true, "Integrate with Land Craft");
		landCore = getBoolean("Enable LandCore support", "modules", true, "Integrate with LandCore");
		
		// TOOLS
		katana = getBoolean("Enable Katana", "tools", true, "Enable Katana");
		katana_combo_multiplier = getFloat("Katana combo multiplier", "tools", 1.25f, 0, Float.MAX_VALUE, "Multiply combo value by this to calculate bonus damage");
		
		laserGun = getBoolean("Enable Laser Gun", "tools", true, "Enable Laser Gun");
		
		// Trash
		String[] trash_things_arr = getStringList("Trash generation", "tweaks",
				new String[] {"20|coal", "5|slime_ball", "10|saddle",
						"5|tconstruct:edible;1", "1|emerald", "3|melon"},
				"Objects that the Trash modifier will generate; enter in the format \"weight|modid:name;meta\" (leave meta blank for zero metadata)");
		{
			int meta = 0;
			int weight = 0;
			for (int i=0; i<trash_things_arr.length; ++i) {
				String[] trash_wi = trash_things_arr[i].split("\\|");
				try {
					weight = Integer.parseInt(trash_wi[0]);
					if (weight < 0) {
						throw new IllegalArgumentException("Weight must not be negative");
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
				String[] loc_meta = trash_wi[1].split(";");
				if (loc_meta.length > 1) {
					try {
						meta = Integer.parseInt(loc_meta[1]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				Item it = Item.REGISTRY.getObject(new ResourceLocation(loc_meta[0]));
				if (it != null && weight > 0) {
					trashThings.add(new TrashThing(weight, new ItemStack(it, 1, meta)));
				}
			}
		}
		
		// Endlectric
		
		String[] arr_endlectric = getStringList("Items that Endlectric will not drain from", "tweaks", new String[0], "Enter in the format \"modid:name;meta\" (leave meta blank to match any meta)");
		{
			int meta = -1;
			for (int i=0; i<arr_endlectric.length; ++i) {
				String[] loc_meta = arr_endlectric[i].split(";");
				if (loc_meta.length > 1) {
					try {
						meta = Integer.parseInt(loc_meta[1]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				Item it = Item.REGISTRY.getObject(new ResourceLocation(loc_meta[0]));
				if (it != null) {
					endlectricBlacklist.put(it, (meta>=0 ? new ItemStack(it, 1, meta) : null));
				}
			}
		}
		
		// Modifier values for Botanical
		Property botan_amount_prop = this.get("tweaks", "Modifier values for Botanical", new int[0]);
		botan_amount_prop.setLanguageKey("Modifiers added for Botanical modifier");
		botan_amount_prop.setComment("Enter integer amounts in order of level (defaults will be extrapolated if some left blank)");
		botan_amount_prop.setMinValue(0);
		
		botan_amount.add(botan_amount_prop.getIntList());
		if (botan_amount.isEmpty()) botan_amount.add(1);
		while (botan_amount.size() < Botanical.MAX_LEVELS) {
			botan_amount.add(botan_amount.get(botan_amount.size()-1)<<1);
		}
		
		
		if (hasChanged()) save();
	}
	
	public static boolean isInEndlectricBlacklist(ItemStack is) {
		if (is == null) return true;
		if (endlectricBlacklist.containsKey(is.getItem())) {
			List<ItemStack> lst = endlectricBlacklist.get(is.getItem());
			for (ItemStack is1: lst) {
				if (is1 == null || is.getMetadata() == is1.getMetadata()) return true;
			}
		}
		return false;
	}
}
