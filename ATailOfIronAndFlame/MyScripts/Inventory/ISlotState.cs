using System;
using System.Linq;
using Inventory.Scriptable_Items;

namespace Inventory
{
    public interface ISlotState
    {
        public void OnDrop(SlotModel originalSlotModel, SlotModel receivedSlotModel); // regular drag'n drop
        public void OnSinglesSplit(SlotModel originalSlotModel, SlotModel receivedSlotModel); // lmb (hold) + rmb
        public void OnManualSplit(SlotModel originalSlotModel, SlotModel receivedSlotModel); // lmb (hold) + scroll
        public void OnAutoSplit(SlotModel slotModel); //scroll
        public void OnQuickMove(SlotModel slotModel, Inventory targetInventory); // shift + lmb
        public void OnQuickMoveAll(SlotModel originalSlotModel, Inventory targetInventory); // shift + ctrl + lmb
        public void OnGroup(SlotModel targetSlotModel); // lmb + lmb (double-click)
    }

    public enum SlotState
    {
        Regular,
        Locked,
        Infinite,
        TakeOnly,
        PreviewOnly
    }

    [Serializable]
    public class RegularSlotState : ISlotState
    {
        public void OnDrop(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
            if (!CanSlotsInteract(originalSlotModel, receivedSlotModel)) return;

            var originalItem = originalSlotModel.Item;
            var receivedItem = receivedSlotModel.Item;
            var originalStack = originalSlotModel.CurrentStack;
            var receivedStack = receivedSlotModel.CurrentStack;

            if (receivedItem == null) return;

            if (originalItem == null || originalItem.DebugName != receivedItem.DebugName) // Inne itemki
                HandleItemsSwap(originalSlotModel, receivedSlotModel, originalItem, receivedItem, originalStack,
                    receivedStack);
            else // Te same itemki
                HandleSameItemsInteraction(originalSlotModel, receivedSlotModel, originalItem, receivedItem,
                    receivedStack);
        }

        public void OnSinglesSplit(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
            if (!CanSlotsInteract(originalSlotModel, receivedSlotModel)) return;

            HandleItemSplit(originalSlotModel, receivedSlotModel, originalSlotModel.Item, receivedSlotModel.Item, 1);
        }

        public void OnManualSplit(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
            if (!CanSlotsInteract(originalSlotModel, receivedSlotModel)) return;

            HandleItemSplit(originalSlotModel, receivedSlotModel, originalSlotModel.Item, receivedSlotModel.Item,
                Math.Max(receivedSlotModel.CurrentStack / 2, 1));
        }

        public void OnAutoSplit(SlotModel slotModel)
        {
            var targetSlot = slotModel.Inventory.GetEmptySlot();
            if (targetSlot is null) return;
            if (!CanSlotsInteract(slotModel, targetSlot.Model)) return;

            HandleItemSplit(targetSlot.Model, slotModel, null, slotModel.Item, Math.Max(slotModel.CurrentStack / 2, 1));
        }

        public void OnQuickMove(SlotModel slotModel, Inventory targetInventory)
        {
            targetInventory?.TryMoveItem(slotModel);
        }

        public void OnQuickMoveAll(SlotModel originalSlotModel, Inventory targetInventory)
        {
            if (originalSlotModel.IsEmpty) return;

            if (targetInventory.GetEmptySlot() is null) return;

            var originalInventory = originalSlotModel.Inventory;
            var targetInventorySize = targetInventory.Slots.Count;
            foreach (var slot in originalInventory.Slots)
            {
                if (targetInventorySize <= 0) return;
                if (slot.IsEmpty) continue;
                if (targetInventory.TryMoveItem(slot.Model)) targetInventorySize--;
            }
        }

        public void OnGroup(SlotModel targetSlotModel)
        {
            if (targetSlotModel.IsEmpty || targetSlotModel.CurrentStack >= targetSlotModel.Item.MaxStack) return;

            var sameItemSlots = targetSlotModel.Inventory.Slots
                .Where(slot => !slot.IsEmpty && slot.Model != targetSlotModel)
                .Where(slot => slot.Item.DebugName == targetSlotModel.Item.DebugName)
                .OrderBy(slot => slot.CurrentStack)
                .ToList();

            if (sameItemSlots.Count == 0) return;
            foreach (var slot in sameItemSlots)
            {
                OnDrop(targetSlotModel, slot.Model);
                if (targetSlotModel.CurrentStack >= targetSlotModel.Item.MaxStack) break;
            }
        }

        private bool CanSlotsInteract(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
            if (receivedSlotModel.Item != null)
                return originalSlotModel.AcceptsItemType(receivedSlotModel.Item.ItemType) &&
                       receivedSlotModel.GetType() == originalSlotModel.GetType();
            return receivedSlotModel.GetType() == originalSlotModel.GetType();
        }

        private void HandleItemsSwap(SlotModel originalSlotModel, SlotModel receivedSlotModel, Item originalItem,
            Item receivedItem, int originalStack, int receivedStack)
        {
            if (!CanSlotsInteract(originalSlotModel, receivedSlotModel) ||
                !CanSlotsInteract(receivedSlotModel, originalSlotModel)) return;
            if (receivedSlotModel.CurrentState is not InfiniteSlotState)
                receivedSlotModel.SetItem(originalItem, originalStack);
            originalSlotModel.SetItem(receivedItem, receivedStack);
        }

        private void HandleItemSplit(SlotModel splitToSlotModel, SlotModel splitFromSlotModel, Item splitToItem,
            Item splitFromItem, int receivedStack)
        {
            if (splitToItem == null)
            {
                // Pojedyńczy itemek lub pusty slot więc prosta zamiana
                splitToSlotModel.SetItem(splitFromItem, receivedStack);
                splitFromSlotModel.RemoveFromStack(receivedStack);
                return;
            }

            if (splitToItem.DebugName != splitFromItem.DebugName) return; // Itemków różnych nie da się pozorzdielać

            // Itemki są takie same
            var remaining = splitToSlotModel.AddToStack(receivedStack);
            splitFromSlotModel.RemoveFromStack(receivedStack - remaining);
        }

        private void HandleSameItemsInteraction(SlotModel originalSlotModel, SlotModel receivedSlotModel,
            Item originalItem, Item receivedItem, int stackAmount)
        {
            // Zwróć resztę z dodawania
            var remaining = originalSlotModel.AddToStack(stackAmount);

            // Jeżeli reszta == oryginalnej liczbie tzn. nic nie zostało dodane
            if (remaining == stackAmount)
            {
                var originalStack = originalSlotModel.CurrentStack;
                // Zamień przedmioty miejscami
                HandleItemsSwap(originalSlotModel, receivedSlotModel, originalItem, receivedItem, originalStack,
                    stackAmount);
            }
            // Jeżeli została reszta tzn. nie udało się dodać wszystkiego
            else if (remaining > 0)
            {
                receivedSlotModel.RemoveFromStack(receivedSlotModel.CurrentStack - remaining);
            }
            // Jeżeli nie ma reszty tzn. wszystko zostało dodane
            else
            {
                receivedSlotModel.ClearItem();
            }
        }
    }

    [Serializable]
    public class LockedSlotState : ISlotState
    {
        private readonly ItemScriptableObject _unlockItem;

        public LockedSlotState(ItemScriptableObject unlockItem)
        {
            _unlockItem = unlockItem;
        }

        public void OnDrop(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
            if (receivedSlotModel.Item.GetData() != _unlockItem) return;
            receivedSlotModel.ClearItem();
            originalSlotModel.SetState(SlotState.Regular, null);
        }

        public void OnSinglesSplit(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
        }

        public void OnManualSplit(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
        }

        public void OnAutoSplit(SlotModel slotModel)
        {
        }

        public void OnQuickMove(SlotModel slotModel, Inventory targetInventory)
        {
        }

        public void OnQuickMoveAll(SlotModel originalSlotModel, Inventory targetInventory)
        {
        }

        public void OnGroup(SlotModel targetSlotModel)
        {
        }
    }

    [Serializable]
    public class InfiniteSlotState : ISlotState
    {
        public void OnDrop(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
            if (!originalSlotModel.AcceptsItemType(receivedSlotModel.Item.ItemType) ||
                receivedSlotModel.CurrentState.GetType() == originalSlotModel.CurrentState.GetType()) return;
            if (receivedSlotModel.GetType() == originalSlotModel.GetType()) receivedSlotModel.ClearItem();
        }

        public void OnSinglesSplit(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
        }

        public void OnManualSplit(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
        }

        public void OnAutoSplit(SlotModel slotModel)
        {
        }

        public void OnQuickMove(SlotModel slotModel, Inventory targetInventory)
        {
        }

        public void OnQuickMoveAll(SlotModel originalSlotModel, Inventory targetInventory)
        {
        }

        public void OnGroup(SlotModel targetSlotModel)
        {
        }
    }

    public class TakeOnlySlotState : ISlotState
    {
        public void OnDrop(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
            // empty bo nic się nie dzieje jak upuści się itemek na TakeOnly typ slotu
            // można jakieś ew audio czy vfx tu dodać
        }

        public void OnSinglesSplit(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
        }

        public void OnManualSplit(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
        }

        public void OnAutoSplit(SlotModel slotModel)
        {
        }

        public void OnQuickMove(SlotModel slotModel, Inventory targetInventory)
        {
            targetInventory.TryMoveItem(slotModel);
            if (slotModel.IsEmpty) return;
            if (slotModel.CurrentStack <= 0) slotModel.ClearItem();
        }

        public void OnQuickMoveAll(SlotModel originalSlotModel, Inventory targetInventory)
        {
            if (originalSlotModel.IsEmpty) return;

            if (targetInventory.GetEmptySlot() is null) return;

            var originalInventory = originalSlotModel.Inventory;
            foreach (var slot in originalInventory.Slots)
            {
                if (slot.IsEmpty) continue;
                if (!targetInventory.TryMoveItem(slot.Model)) return;
            }
        }

        public void OnGroup(SlotModel targetSlotModel)
        {
        }
    }

    [Serializable]
    public class PreviewOnlySlot : ISlotState
    {
        // wszystkie metody empty, jako że nie da się wejść z tym slotem w interakcje, ma służyć do np. quest reward preview itp.
        public void OnDrop(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
        }

        public void OnSinglesSplit(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
        }

        public void OnManualSplit(SlotModel originalSlotModel, SlotModel receivedSlotModel)
        {
        }

        public void OnAutoSplit(SlotModel slotModel)
        {
        }

        public void OnQuickMove(SlotModel slotModel, Inventory targetInventory)
        {
        }

        public void OnQuickMoveAll(SlotModel originalSlotModel, Inventory targetInventory)
        {
        }

        public void OnGroup(SlotModel targetSlotModel)
        {
        }
    }
}