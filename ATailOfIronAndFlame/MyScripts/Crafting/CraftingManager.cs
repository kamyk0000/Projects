using System;
using System.Collections.Generic;
using System.Linq;
using Audio;
using Crafting.Scriptable_Recipes;
using Interactive;
using Inventory;
using UnityEngine;
using UnityEngine.UI;

namespace Crafting
{
    public abstract class CraftingManager : MonoBehaviour, IInteractable
    {
        [SerializeField] protected SlotPresenter _resultSlot;
        [SerializeField] protected List<SlotPresenter> _craftingSlots;
        [SerializeField] protected CraftingType _type;
        [SerializeField] protected GameObject _craftingPanel, _playerInventoryPanel;
        [SerializeField] protected Button _tutorialButton;
        [SerializeField] protected TutorialPopUpScriptableObject _tutorialPopUpData;
        [SerializeField] protected AudioClip _clip, _interactClip;
        
        protected Coroutine _craftingCoroutine;
        protected RecipeScriptableObject _currentRecipe;
        protected bool _isWorking;
        
        public event Action<RecipeScriptableObject> OnItemCrafted;
        public event Action<CraftingType> OnStationInteracted;

        public virtual List<SlotPresenter> Slots
        {
            get
            {
                var slots = new List<SlotPresenter>();
                slots.AddRange(_craftingSlots);
                if (!slots.Contains(_resultSlot)) slots.Add(_resultSlot);

                return slots;
            }
        }

        public CraftingType Type => _type;

        private void Awake()
        {
            if (_playerInventoryPanel == null) _playerInventoryPanel = GameObject.Find("RightSide (InventorySide)");
        }

        protected virtual void Start()
        {
            foreach (var slot in _craftingSlots) slot.OnSlotItemChanged += HandleSlotItemChange;
            _resultSlot.OnSlotItemChanged += HandleSlotItemChange;
            _tutorialButton?.onClick.AddListener(ShowTutorial);
        }

        public void Interact()
        {
            //AudioManager.Instance.PlaySFX(_interactClip, transform);
            InventoryInteractable.Instance.CurrentCraftingManager = this;
            _craftingPanel.SetActive(true);
            _playerInventoryPanel.SetActive(true);
            OnStationInteracted?.Invoke(_type);
        }

        public void CancelInteract()
        {
            InventoryInteractable.Instance.CurrentCraftingManager = this;
            _craftingPanel.SetActive(false);
            _playerInventoryPanel.SetActive(false);
        }

        public event Action OnCancelInteract;
        protected abstract void HandleSlotItemChange();

        protected virtual void StopCrafting()
        {
            if (_craftingCoroutine is not null) StopCoroutine(_craftingCoroutine);
            _isWorking = false;
            _currentRecipe = null;
        }

        protected virtual Dictionary<string, int> GetResources()
        {
            return _craftingSlots
                .Where(slot => slot.gameObject.activeSelf && !slot.IsLocked && !slot.IsEmpty)
                .GroupBy(slot => slot.Item.DebugName)
                .ToDictionary(grouped => grouped.Key,
                    grouped => grouped.Sum(item => item.CurrentStack));
        }

        protected void ConsumeResources()
        {
            OnItemCrafted?.Invoke(_currentRecipe);
            var resourcesToConsume = _currentRecipe.ResourcesNeeded;
            foreach (var slot in _craftingSlots)
            {
                if (slot.IsLocked || slot.IsEmpty) continue;
                var item = slot.Item;

                var left = slot.RemoveFromStack(resourcesToConsume[item.DebugName]);
                resourcesToConsume[item.DebugName] = left > 0 ? resourcesToConsume[item.DebugName] - left : 0;
            }
        }

        public void ShowTutorial()
        {
            TutorialPopUp.Instance.ShowTutorialPopUp(_tutorialPopUpData);
        }
    }
}