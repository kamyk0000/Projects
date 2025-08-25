using Crafting.Scriptable_Recipes;
using Inventory;
using Inventory.Scriptable_Items;
using UnityEngine;

namespace Crafting
{
    public class RecipeUI : MonoBehaviour
    {
        [SerializeField] private Transform _resourcesContainer;
        [SerializeField] private GameObject _recipeSlotPrefab, _runeWordSlotPrefab;
        [SerializeField] private SlotPresenter _resultSlotPresenter;

        private Item _item;

        public void Initialize(RecipeScriptableObject recipe)
        {
            foreach (var resource in recipe.ResourcesNeeded)
            {
                var item = ItemsManager.Instance.GetResource(resource.Key);
                if (item is null) continue;
                GameObject recipeSlotUI;
                if (item.itemType == ItemType.RuneLetter)
                {
                    for (var i = 0; i < resource.Value; i++)
                    {
                        recipeSlotUI = Instantiate(_runeWordSlotPrefab, _resourcesContainer);
                        var slotPresenter = recipeSlotUI.GetComponentInChildren<SlotPresenter>();
                        slotPresenter.Initialize(new SlotModel());
                        slotPresenter.SetItem(new Item(item));
                    }
                }
                else
                {
                    recipeSlotUI = Instantiate(_recipeSlotPrefab, _resourcesContainer);
                    var slotPresenter = recipeSlotUI.GetComponentInChildren<SlotPresenter>();
                    slotPresenter.Initialize(new SlotModel());
                    slotPresenter.SetItem(new Item(item), resource.Value);
                }
            }

            _resultSlotPresenter.Initialize(new SlotModel());
            _resultSlotPresenter.SetItem(new Item(recipe.resultItem));
        }
    }
}