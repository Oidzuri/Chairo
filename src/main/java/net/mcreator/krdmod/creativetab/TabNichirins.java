
package net.mcreator.krdmod.creativetab;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;

import net.mcreator.krdmod.item.ItemNichirin;
import net.mcreator.krdmod.ElementsKrdModMod;

@ElementsKrdModMod.ModElement.Tag
public class TabNichirins extends ElementsKrdModMod.ModElement {
	public TabNichirins(ElementsKrdModMod instance) {
		super(instance, 3);
	}

	@Override
	public void initElements() {
		tab = new CreativeTabs("tabnichirins") {
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem() {
				return new ItemStack(ItemNichirin.block, (int) (1));
			}

			@SideOnly(Side.CLIENT)
			public boolean hasSearchBar() {
				return false;
			}
		};
	}
	public static CreativeTabs tab;
}
