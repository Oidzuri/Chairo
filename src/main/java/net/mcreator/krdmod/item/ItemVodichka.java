
package net.mcreator.krdmod.item;

@ElementsKrdModMod.ModElement.Tag
public class ItemVodichka extends ElementsKrdModMod.ModElement {

	@GameRegistry.ObjectHolder("krd_mod:vodichka")
	public static final Item block = null;

	public ItemVodichka(ElementsKrdModMod instance) {
		super(instance, 56);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("krd_mod:vodichka", "inventory"));
	}

	public static class ItemFoodCustom extends ItemFood {

		public ItemFoodCustom() {
			super(4, 0.3f, false);
			setUnlocalizedName("vodichka");
			setRegistryName("vodichka");
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
