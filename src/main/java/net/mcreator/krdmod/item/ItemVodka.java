
package net.mcreator.krdmod.item;

@ElementsKrdModMod.ModElement.Tag
public class ItemVodka extends ElementsKrdModMod.ModElement {

	@GameRegistry.ObjectHolder("krd_mod:vodka")
	public static final Item block = null;

	public ItemVodka(ElementsKrdModMod instance) {
		super(instance, 49);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("krd_mod:vodka", "inventory"));
	}

	public static class ItemFoodCustom extends ItemFood {

		public ItemFoodCustom() {
			super(4, 0.3f, false);
			setUnlocalizedName("vodka");
			setRegistryName("vodka");
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
