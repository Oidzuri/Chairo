
package net.mcreator.krdmod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemFood;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.mcreator.krdmod.creativetab.TabFoods;
import net.mcreator.krdmod.ElementsKrdModMod;

@ElementsKrdModMod.ModElement.Tag
public class ItemKoktiel2 extends ElementsKrdModMod.ModElement {
	@GameRegistry.ObjectHolder("krd_mod:koktiel_2")
	public static final Item block = null;
	public ItemKoktiel2(ElementsKrdModMod instance) {
		super(instance, 51);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("krd_mod:koktiel_2", "inventory"));
	}
	public static class ItemFoodCustom extends ItemFood {
		public ItemFoodCustom() {
			super(4, 0.3f, false);
			setUnlocalizedName("koktiel_2");
			setRegistryName("koktiel_2");
			setAlwaysEdible();
			setCreativeTab(TabFoods.tab);
			setMaxStackSize(64);
		}

		@Override
		public EnumAction getItemUseAction(ItemStack par1ItemStack) {
			return EnumAction.DRINK;
		}
	}
}
