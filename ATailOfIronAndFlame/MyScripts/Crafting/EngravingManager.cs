using System.Collections;
using System.Collections.Generic;
using System.Linq;
using Audio;
using Inventory;
using Inventory.Scriptable_Items;
using UnityEngine;
using UnityEngine.UI;

namespace Crafting
{
    public class EngravingManager : CraftingManager
    {
        [SerializeField] private Button _craftButton;
        [SerializeField] private Transform _runeBook;
        [SerializeField] private GameObject _runeSlotPrefab;
        [SerializeField] private AudioClip _failClip;

        protected override void Start()
        {
            base.Start();

            _craftButton.onClick.AddListener(TryCraft);
            _craftButton.interactable = false;

            InitializeRuneWordsList();
        }

        private void InitializeRuneWordsList()
        {
            var allRunes = ItemsManager.Instance.AllResources.Where(kvp => kvp.Value.itemType == ItemType.RuneLetter)
                .Select(kvp => kvp.Value);

            foreach (var item in allRunes)
            {
                var inventorySlot = Instantiate(_runeSlotPrefab, _runeBook);
                var slotPresenter = inventorySlot.GetComponentInChildren<SlotPresenter>();
                slotPresenter.Initialize(new SlotModel());
                slotPresenter.SetItem(new Item(item));
                slotPresenter.SetState(SlotState.Infinite, null);
            }
        }

        protected override void HandleSlotItemChange()
        {
            if (_craftingSlots.Where(slot => slot && slot.gameObject.activeSelf)
                    .Any(slot => slot.IsEmpty && !slot.IsLocked) || _resultSlot.IsEmpty)
            {
                _craftButton.interactable = false;
                return;
            }

            _craftButton.interactable = true;
        }

        private IEnumerator WaitForCrafting()
        {
            _isWorking = true;
            _craftButton.interactable = false;
            yield return null;
        }

        private void TryCraft()
        {
            if (_craftingCoroutine != null) StopCoroutine(_craftingCoroutine);
            _currentRecipe = RecipesManager.Instance.GetExactMatchingRecipe(GetResources());

            if (_currentRecipe is not null)
            {
                AudioManager.Instance.PlaySFX(_clip, transform);
                ConsumeResources();
                _craftingCoroutine = StartCoroutine(WaitForCrafting());
                _resultSlot.SetItem(new RuneItem(_currentRecipe.resultItem));
            }
            else
            {
                AudioManager.Instance.PlaySFX(_failClip, transform);
                DestroyResources();
                _craftingCoroutine = StartCoroutine(WaitForCrafting());
            }

            StopCrafting();
        }

        protected override Dictionary<string, int> GetResources()
        {
            var resources = base.GetResources();
            resources.Add(_resultSlot.Item.DebugName, 1);
            return resources;
        }

        private void DestroyResources()
        {
            _resultSlot.SetItem(null);
        }
    }
}