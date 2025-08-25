using System;
using System.Collections.Generic;
using System.Linq;
using Audio;
using Interactive;
using Inventory.Scriptable_Items;
using UnityEngine;

namespace Inventory
{
    public class SlotPresenter : MonoBehaviour
    {
        [SerializeField] private SlotUI _view;
        [SerializeField] private List<ItemType> _acceptableItemsList;
        [SerializeField] private ItemScriptableObject _unlockItem;
        [SerializeField] private SlotState _defaultState;
        [SerializeField] private AudioClip _unlockSound, _interactSound;
        
        private Inventory _inventory;

        public SlotModel Model { get; private set; }

        public ISlotState CurrentState => Model != null ? Model.CurrentState : null;
        public int CurrentStack => Model != null ? Model.CurrentStack : -1;
        public bool IsEmpty => Model != null ? Model.IsEmpty : true;
        public bool IsLocked =>  Model != null ?  Model.IsLocked : true;
        public bool CanIncreaseStack => Model != null ? Model.CanIncreaseStack : false;
        public Item Item => Model!= null ? Model.Item : null;

        public Inventory Inventory
        {
            get
            {
                if (Model != null && Model.Inventory) return Model.Inventory;
                return _inventory;
            }
            set
            {
                _inventory = value;
                if (Model != null) Model.Inventory = value;
            }
        }

        public event Action OnSlotItemChanged, OnSlotItemRemoved;

        public void Initialize(SlotModel model)
        {
            _view.OnItemHovered -= ShowItemTooltip;
            _view.OnItemDropped -= HandleItemDropFromSlots;
            _view.OnSlotInteracted -= HandleSlotInteraction;
            _view.OnSlotEnabled -= UpdateUI;

            if (Model != null)
            {
                Model.OnItemChanged -= UpdateUI;
                Model.OnItemChanged -= NotifyItemChanged;
                Model.OnItemRemoved -= UpdateUI;
                Model.OnItemRemoved -= NotifyItemRemoved;
                Model.OnSlotLocked -= LockSlot;
                Model.OnSlotUnlocked -= UnlockSlot;
            }

            Model = model;

            Model.OnItemChanged += UpdateUI;
            Model.OnItemChanged += NotifyItemChanged;
            Model.OnItemRemoved += UpdateUI;
            Model.OnItemRemoved += NotifyItemRemoved;
            Model.OnSlotLocked += LockSlot;
            Model.OnSlotUnlocked += UnlockSlot;

            _view.OnItemHovered += ShowItemTooltip;
            _view.OnItemDropped += HandleItemDropFromSlots;
            _view.OnSlotInteracted += HandleSlotInteraction;
            _view.OnSlotEnabled += UpdateUI;
            
            Model.SetDefaultState(_defaultState, _unlockItem);
            Model.SetAcceptableItems(_acceptableItemsList.Distinct().ToList());

            SlotRepository.Register(_view, this);
        }

        private void HandleItemDropFromSlots(SlotUI droppedOnSlot, SlotUI droppedSlot)
        {
            var otherSlot = SlotRepository.GetPresenterForSlotUI(droppedSlot).Model;
            HandleItemDropped(otherSlot);
        }

        public void HandleItemDropped(SlotModel droppedItem)
        {
            AudioManager.Instance.PlaySFX(_interactSound, transform);
            Model.CurrentState.OnDrop(Model, droppedItem);
        }

        public void HandleSlotInteraction(SlotUI droppedSlot, SlotInteraction slotInteraction)
        {
            var otherSlot = SlotRepository.GetPresenterForSlotUI(droppedSlot).Model;
            var otherInventory = InventoryInteractable.Instance.GetCurrentOpenOppositeInventoryBySlot(this);
            AudioManager.Instance.PlaySFX(_interactSound, transform);

            switch (slotInteraction)
            {
                case SlotInteraction.Group:
                    Model.CurrentState.OnGroup(Model);
                    break;
                case SlotInteraction.MoveAll:
                    Model.CurrentState.OnQuickMoveAll(Model, otherInventory);
                    break;
                case SlotInteraction.Move:
                    Model.CurrentState.OnQuickMove(Model, otherInventory);
                    break;
                case SlotInteraction.AutoHalfSplit:
                    Model.CurrentState.OnAutoSplit(Model);
                    break;
                case SlotInteraction.HalfSplit:
                    Model.CurrentState.OnManualSplit(Model, otherSlot);
                    break;
                case SlotInteraction.SingleSplit:
                    Model.CurrentState.OnSinglesSplit(Model, otherSlot);
                    break;
                default:
                    throw new ArgumentOutOfRangeException(nameof(slotInteraction), slotInteraction, null);
            }
        }

        private void NotifyItemChanged()
        {
            OnSlotItemChanged?.Invoke();
        }

        private void NotifyItemRemoved()
        {
            OnSlotItemChanged?.Invoke();
            OnSlotItemRemoved?.Invoke();
        }

        private void UpdateUI()
        {
            if (IsLocked) return;

            _view.UpdateUI(Model?.Item, Model?.CurrentStack);
        }

        private void LockSlot()
        {
            _view.LockSlot();
        }

        private void UnlockSlot()
        {
            AudioManager.Instance.PlaySFX(_unlockSound, transform);

            _view.UnlockSlot();
        }

        private void ShowItemTooltip(bool show)
        {
            if (show && !IsEmpty)
            {
                UpdateUI();
                ItemTooltipUI.Instance.ShowTooltip(Model.Item);
            }
            else
            {
                ItemTooltipUI.Instance.HideTooltip();
            }
        }

        public bool AcceptsItemType(ItemType type)
        {
            return Model.AcceptsItemType(type);
        }

        public int AddToStack(int amount)
        {
            return Model.AddToStack(amount);
        }

        public int RemoveFromStack(int amount)
        {
            return Model.RemoveFromStack(amount);
        }

        public void SetState(SlotState state, ItemScriptableObject item)
        {
            Model.SetState(state, item);
            _view.DragAndDrop.enabled = state is not (SlotState.PreviewOnly or SlotState.Locked);
        }

        public void SetItem(Item item, int amount = 1)
        {
            Model.SetItem(item, amount);
        }

        public void ClearItem()
        {
            Model.ClearItem();
        }

        public void SetInventory(Inventory inventory)
        {
            Model.Inventory = inventory;
        }

        public SlotModel GetData()
        {
            return Model;
        }
    }

    public enum SlotInteraction
    {
        Group,
        Move,
        MoveAll,
        HalfSplit,
        AutoHalfSplit,
        SingleSplit
    }

    public static class SlotRepository
    {
        private static Dictionary<SlotUI, SlotPresenter> _instances = new();

        public static void Register(SlotUI slotUI, SlotPresenter slotPresenter)
        {
            _instances[slotUI] = slotPresenter;
        }

        public static void Unregister(SlotUI slotUI)
        {
            _instances[slotUI] = null;
        }

        public static void Clear()
        {
            _instances = new Dictionary<SlotUI, SlotPresenter>();
        }

        public static SlotPresenter GetPresenterForSlotUI(SlotUI slotUI)
        {
            return _instances[slotUI];
        }
    }
}