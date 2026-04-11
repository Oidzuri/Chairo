
package net.mcreator.krdmod.item;

@ElementsKrdModMod.ModElement.Tag
public class ItemKOktieil5 extends ElementsKrdModMod.ModElement {

	@GameRegistry.ObjectHolder("krd_mod:k_oktieil_5")
	public static final Item block = null;

	public ItemKOktieil5(ElementsKrdModMod instance) {
		super(instance, 55);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("krd_mod:k_oktieil_5", "inventory"));
	}

	public static class ItemFoodCustom extends ItemFood {

		public ItemFoodCustom() {
			super(4, 0.3f, false);
			setUnlocalizedName("k_oktieil_5");
			setRegistryName("k_oktieil_5");
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
