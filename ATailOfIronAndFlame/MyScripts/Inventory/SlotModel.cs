using System;
using System.Collections.Generic;
using Inventory.Scriptable_Items;
using JetBrains.Annotations;
using UnityEngine;

namespace Inventory
{
    [Serializable]
    public class SlotModel
    {
        [SerializeField] private Item _item;
        [SerializeField] private int _currentStack;
        [NonSerialized] private List<ItemType> _acceptableItemTypes = new();
        [NonSerialized] private Inventory _inventory;
        [NonSerialized] private ISlotState _slotState;
        
        public SlotState slotStateType;
        public bool hadStateChanged;
        public Item Item => _item;
        public int CurrentStack => _currentStack;
        public bool IsStackable => _item.MaxStack > 1;
        public bool CanIncreaseStack => _item.MaxStack > CurrentStack;

        public Inventory Inventory
        {
            get => _inventory;
            set => _inventory = value;
        }

        public ISlotState CurrentState => _slotState;
        public bool IsLocked => _slotState is LockedSlotState;

        public bool IsEmpty => _item == null;
        public event Action OnItemChanged, OnItemRemoved, OnSlotLocked, OnSlotUnlocked;

        public void SetDefaultState(SlotState slotState, [CanBeNull] ItemScriptableObject unlockItem)
        {
            if (hadStateChanged)
            {
                SetState(slotStateType, unlockItem);
            }
            else
            {
                SetState(slotState, unlockItem);
                hadStateChanged = true;
            }
        }

        public void SetState(SlotState slotState, [CanBeNull] ItemScriptableObject unlockItem)
        {
            if (slotStateType == SlotState.Locked && slotState != slotStateType) OnSlotUnlocked?.Invoke();
            slotStateType = slotState;
            hadStateChanged = true;

            switch (slotStateType)
            {
                case SlotState.Locked:
                    _slotState = new LockedSlotState(unlockItem);
                    OnSlotLocked?.Invoke();
                    break;
                case SlotState.Infinite: _slotState = new InfiniteSlotState(); break;
                case SlotState.Regular: _slotState = new RegularSlotState(); break;
                case SlotState.PreviewOnly: _slotState = new PreviewOnlySlot(); break;
                case SlotState.TakeOnly: _slotState = new TakeOnlySlotState(); break;
                default: _slotState = new RegularSlotState(); break;
            }
        }

        public void SetItem(Item item, int stack = 1)
        {
            if (item is null || string.IsNullOrEmpty(item.DebugName))
            {
                ClearItem();
                return;
            }

            _item = item;
            _currentStack = stack;
            OnItemChanged?.Invoke();
        }

        public int AddToStack(int amount)
        {
            if (!IsStackable || !CanIncreaseStack || amount <= 0) return amount;
            var freeSpace = _item.MaxStack - _currentStack;
            var toAdd = Mathf.Min(amount, freeSpace);
            _currentStack += toAdd;
            OnItemChanged?.Invoke();

            return amount - toAdd;
        }

        public int RemoveFromStack(int amount)
        {
            if (amount <= 0) return amount;
            var toRemove = Mathf.Min(amount, _currentStack);
            _currentStack -= toRemove;
            OnItemChanged?.Invoke();
            if (_currentStack <= 0) ClearItem();

            return amount - toRemove;
        }

        public void ClearItem()
        {
            _item = null;
            _currentStack = 0;
            OnItemRemoved?.Invoke();
        }

        public void SetAcceptableItems(List<ItemType> acceptableItems)
        {
            _acceptableItemTypes = acceptableItems;
        }

        public bool AcceptsItemType(ItemType type)
        {
            return _acceptableItemTypes.Contains(type) || _acceptableItemTypes.Count == 0;
        }
    }
}