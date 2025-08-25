using System.Collections;
using System.Collections.Generic;
using Audio;
using Crafting.Scriptable_Recipes;
using Inventory;
using UnityEngine;
using UnityEngine.UI;

namespace Crafting
{
    public class SmeltingManager : CraftingManager
    {
        [SerializeField] private SlotPresenter _fuelSlot;
        [SerializeField] private Image _progressBar;
        [SerializeField] private Image _fuelBar;
        
        private AudioSource _ambientAudio;
        private bool _isFueled;
        private float _remainingCraftingTime, _remainingFuelTime, _fuelTimer;

        public override List<SlotPresenter> Slots
        {
            get
            {
                var slots = new List<SlotPresenter>();
                slots.AddRange(_craftingSlots);
                if (!slots.Contains(_resultSlot)) slots.Add(_resultSlot);
                return slots;
            }
        }

        protected override void Start()
        {
            base.Start();

            _fuelSlot.OnSlotItemChanged += HandleSlotItemChange;
            _resultSlot.OnSlotItemChanged += HandleSlotItemChange;

            _progressBar.fillAmount = 0f;
            _fuelBar.fillAmount = 0f;

            if (_ambientAudio is null)
            {
                _ambientAudio = AudioManager.Instance.PlaySFXDontDestroy(_clip, transform);
                _ambientAudio.loop = true;
                _ambientAudio.volume = 0f;
                _ambientAudio.spatialBlend = 0.9f;
            }

            HandleSlotItemChange();
        }

        private void FixedUpdate()
        {
            if (!_isFueled) return;

            _remainingFuelTime -= Time.deltaTime;
            _fuelBar.fillAmount = 0f + (_fuelTimer - _remainingFuelTime) / _fuelTimer;

            if (!(_remainingFuelTime <= 0f)) return;

            if (_isWorking)
            {
                if (!_fuelSlot.IsEmpty)
                    ConsumeFuel();
                else
                    StopCrafting();
            }
            else
            {
                _fuelBar.fillAmount = 0f;
                _isFueled = false;
            }
        }

        protected override void HandleSlotItemChange()
        {
            if (_fuelSlot.IsEmpty && !_isFueled) return;

            var materials = GetResources();
            var newRecipe = RecipesManager.Instance.GetExactMatchingRecipe(materials);

            if (materials.Count <= 0 || newRecipe is null)
            {
                StopCrafting();
                return;
            }

            if (!_resultSlot.IsEmpty && (!_resultSlot.Item.DebugName.Equals(newRecipe.resultItem.name.ToLower()) ||
                                         !_resultSlot.CanIncreaseStack))
            {
                StopCrafting();
                return;
            }

            if (!_isWorking)
            {
                if (!_isFueled) ConsumeFuel();

                _ambientAudio.volume = 1f;
                StartCrafting(newRecipe);
            }

            if (newRecipe != _currentRecipe) AdjustCraftingTime(newRecipe);
        }

        private void StartCrafting(RecipeScriptableObject recipe)
        {
            _currentRecipe = recipe;
            _remainingCraftingTime = recipe.craftingTime;

            if (_craftingCoroutine != null) StopCoroutine(_craftingCoroutine);
            _craftingCoroutine = StartCoroutine(WaitForCrafting());
        }

        private IEnumerator WaitForCrafting()
        {
            _isWorking = true;

            while (_remainingCraftingTime > 0f)
            {
                _remainingCraftingTime -= Time.deltaTime;
                _progressBar.fillAmount = 0f + (_currentRecipe.craftingTime - _remainingCraftingTime) /
                    _currentRecipe.craftingTime;
                yield return null;
            }

            if (_currentRecipe != null) FinishCrafting();
        }

        private void AdjustCraftingTime(RecipeScriptableObject newRecipe)
        {
            var newCraftingTime = newRecipe.craftingTime;

            if (_currentRecipe is not null && newCraftingTime > _currentRecipe.craftingTime)
            {
                _remainingCraftingTime += newCraftingTime - (_currentRecipe.craftingTime - _remainingCraftingTime);
            }
            else
            {
                if (_currentRecipe is not null)
                    _remainingCraftingTime = Mathf.Max(1f,
                        _currentRecipe.craftingTime - _remainingCraftingTime - newCraftingTime);
            }

            _currentRecipe = newRecipe;
        }

        private void FinishCrafting()
        {
            _isWorking = false;
            if (_resultSlot.IsEmpty)
                _resultSlot.SetItem(new Item(_currentRecipe.resultItem));
            else
                _resultSlot.AddToStack(1);

            ConsumeResources();
            StopCrafting();
            HandleSlotItemChange();
        }

        protected override void StopCrafting()
        {
            base.StopCrafting();

            _remainingCraftingTime = 0f;
            _progressBar.fillAmount = 0F;

            _ambientAudio.volume = 0f;
        }

        private void ConsumeFuel()
        {
            if (_fuelSlot.IsEmpty) return;

            _remainingFuelTime = _fuelSlot.Item.GetData().GetBrunTime();
            _fuelTimer = _remainingFuelTime;
            _isFueled = true;

            _fuelSlot.RemoveFromStack(1);
        }
    }
}