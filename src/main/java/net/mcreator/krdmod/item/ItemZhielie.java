
package net.mcreator.krdmod.item;

@ElementsKrdModMod.ModElement.Tag
public class ItemZhielie extends ElementsKrdModMod.ModElement {

	@GameRegistry.ObjectHolder("krd_mod:zhielie")
	public static final Item block = null;

	public ItemZhielie(ElementsKrdModMod instance) {
		super(instance, 57);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("krd_mod:zhielie", "inventory"));
	}

	public static class ItemFoodCustom extends ItemFood {

		public ItemFoodCustom() {
			super(4, 0.3f, false);
			setUnlocalizedName("zhielie");
			setRegistryName("zhielie");

			setCreativeTab(TabFoods.tab);
			setMaxStackSize(64);
		}

		@Override
		public EnumAction getItemUseAction(ItemStack par1ItemStack) {
			return EnumAction.EAT;
		}

	}

}
