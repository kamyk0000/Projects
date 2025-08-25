using System.Collections.Generic;
using System.Linq;
using Audio;
using Inventory;
using UnityEngine;
using UnityEngine.UI;

namespace Crafting
{
    public class EnchantingManager : CraftingManager
    {
        [SerializeField] private Button _craftButton;
        [SerializeField] private Image _weaponSprite;
        private List<RuneItem> _runes = new();

        private WeaponItem _weapon;

        protected override void Start()
        {
            base.Start();

            _craftButton.onClick.AddListener(FinishCrafting);
            _craftButton.interactable = false;

            HandleSlotItemChange();
        }

        protected override void HandleSlotItemChange()
        {
            if (_resultSlot.Item is not WeaponItem item)
            {
                _weaponSprite.enabled = false;
                _weaponSprite.sprite = null;

                _craftButton.interactable = false;
                return;
            }

            _weapon = item;
            if (_weapon is { IsUpgraded: true })
            {
                _weaponSprite.enabled = false;
                _weaponSprite.sprite = null;

                _craftButton.interactable = false;
                return;
            }

            _weaponSprite.enabled = true;
            _weaponSprite.sprite = _weapon.ItemSprite;

            _runes = _craftingSlots.Where(slot => !(slot.IsEmpty || slot.IsLocked))
                .Select(slot => slot.Item as RuneItem).ToList();
            if (_runes.Count <= 0 || _resultSlot.IsEmpty)
            {
                _craftButton.interactable = false;
                return;
            }

            _craftButton.interactable = true;
        }

        private new void ConsumeResources()
        {
            foreach (var slot in _craftingSlots)
            {
                if (slot.IsLocked || slot.IsEmpty) continue;
                slot.ClearItem();
            }
        }

        private void FinishCrafting()
        {
            AudioManager.Instance.PlaySFX(_clip, transform);
            _weapon?.Upgrade(_runes);
            _resultSlot.SetItem(_weapon);
            ConsumeResources();
        }
    }
}