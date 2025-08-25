using System;
using Audio;
using Interactive;
using Inventory;
using UnityEngine;

namespace Crafting
{
    public class ChestManager : MonoBehaviour, IInteractable
    {
        [SerializeField] protected CraftingType _type;
        [SerializeField] protected FixedSizeInventory _inventory;
        [SerializeField] protected GameObject _chestInventoryPanel, _playerInventoryPanel;
        [SerializeField] private AudioClip _openClip, _closeClip;

        private void Awake()
        {
            if (_playerInventoryPanel == null) _playerInventoryPanel = GameObject.Find("RightSide (InventorySide)");
        }

        public void Interact()
        {
            AudioManager.Instance.PlaySFX(_openClip, transform);
            InventoryInteractable.Instance.CurrentOtherInventory = _inventory;
            _chestInventoryPanel.SetActive(true);
            _playerInventoryPanel.SetActive(true);
        }

        public void CancelInteract()
        {
            AudioManager.Instance.PlaySFX(_closeClip, transform);
            InventoryInteractable.Instance.CurrentOtherInventory = null;
            _chestInventoryPanel.SetActive(false);
            _playerInventoryPanel.SetActive(false);
        }

        public event Action OnCancelInteract;
    }
}