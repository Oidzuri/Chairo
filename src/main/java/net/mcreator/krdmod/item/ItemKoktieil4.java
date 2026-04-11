
package net.mcreator.krdmod.item;

@ElementsKrdModMod.ModElement.Tag
public class ItemKoktieil4 extends ElementsKrdModMod.ModElement {

	@GameRegistry.ObjectHolder("krd_mod:koktieil_4")
	public static final Item block = null;

	public ItemKoktieil4(ElementsKrdModMod instance) {
		super(instance, 54);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("krd_mod:koktieil_4", "inventory"));
	}

	public static class ItemFoodCustom extends ItemFood {

		public ItemFoodCustom() {
			super(4, 0.3f, false);
			setUnlocalizedName("koktieil_4");
			setRegistryName("koktieil_4");
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
