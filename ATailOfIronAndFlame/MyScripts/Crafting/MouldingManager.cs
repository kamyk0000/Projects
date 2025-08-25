using System.Collections;
using System.Collections.Generic;
using System.Linq;
using Audio;
using Crafting.Scriptable_Recipes;
using Inventory;
using Inventory.Scriptable_Items;
using TMPro;
using UnityEngine;

namespace Crafting
{
    public class MouldingManager : CraftingManager
    {
        [SerializeField] private TMP_Dropdown _mouldSelector;
        [SerializeField] private TMP_Text _materialsPrompt;

        private ItemScriptableObject[] _weaponMoulds;

        protected override void Start()
        {
            base.Start();

            _resultSlot.OnSlotItemRemoved += FinishCrafting;
            _materialsPrompt.text = string.Empty;

            InitializeWeaponMouldsList();
            HandleSlotItemChange();
        }

        private void InitializeWeaponMouldsList()
        {
            _weaponMoulds = Resources.LoadAll<ItemScriptableObject>("ScriptableObjects/WeaponTypes");
            var options = _weaponMoulds.Select(item => item.itemName).ToList();

            _mouldSelector.ClearOptions();
            _mouldSelector.AddOptions(options);
            _mouldSelector.onValueChanged.AddListener(delegate { HandleSlotItemChange(); });
        }

        protected override void HandleSlotItemChange()
        {
            var materials = GetResources();
            var newRecipe = RecipesManager.Instance.GetResourceMatchingRecipe(materials);

            if (materials.Count <= 1 || newRecipe is null)
            {
                ClearCrafting();
                return;
            }

            PrepareCrafting(newRecipe);
        }

        private void PrepareCrafting(RecipeScriptableObject recipe)
        {
            var amountNeeded = recipe.ResourcesNeeded[_craftingSlots[0].Item.DebugName];
            var material = _craftingSlots[0];
            _materialsPrompt.SetText($"{amountNeeded} {material.Item.Name}(s) needed");

            if (material.CurrentStack >= amountNeeded)
            {
                _currentRecipe = recipe;
                if (_resultSlot.IsEmpty || !_resultSlot.Item.Matches(_currentRecipe.resultItem))
                {
                    _resultSlot.SetItem(new Item(_currentRecipe.resultItem));
                }

                return;
            }

            if (_currentRecipe == null) return;

            _currentRecipe = null;
            _resultSlot.SetItem(null);
        }

        private IEnumerator WaitForCrafting()
        {
            _isWorking = true;
            yield return null;
        }

        private void FinishCrafting()
        {
            if (_currentRecipe == null) return;
            _isWorking = false;
            AudioManager.Instance.PlaySFX(_clip, transform);

            ConsumeResources();
            HandleSlotItemChange();
        }

        private void ClearCrafting()
        {
            StopCrafting();

            if (!_resultSlot.IsEmpty) _resultSlot.SetItem(null);
            _materialsPrompt.SetText("");
        }

        protected override Dictionary<string, int> GetResources()
        {
            var resources = base.GetResources();
            resources.Add(new Item(_weaponMoulds[_mouldSelector.value]).DebugName, 1);

            return resources;
        }
    }
}