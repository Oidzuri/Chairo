
package net.mcreator.krdmod.creativetab;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.creativetab.CreativeTabs;

import net.mcreator.krdmod.ElementsKrdModMod;

@ElementsKrdModMod.ModElement.Tag
public class TabFoods extends ElementsKrdModMod.ModElement {
	public TabFoods(ElementsKrdModMod instance) {
		super(instance, 32);
	}

	@Override
	public void initElements() {
		tab = new CreativeTabs("tabfoods") {
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem() {
				return new ItemStack(Blocks.PUMPKIN, (int) (1));
			}

			@SideOnly(Side.CLIENT)
			public boolean hasSearchBar() {
				return true;
			}
		}.setBackgroundImageName("item_search.png");
	}
	public static CreativeTabs tab;
}
