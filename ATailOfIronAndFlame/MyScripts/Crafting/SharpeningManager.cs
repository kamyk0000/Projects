using Audio;
using Inventory;
using Inventory.Scriptable_Items;
using MiniGames;
using UnityEngine;
using UnityEngine.UI;

namespace Crafting
{
    public class SharpeningManager : CraftingManager
    {
        [SerializeField] private Button _craftButton;
        [SerializeField] private GameObject _sharpeningMiniGameObject;
        
        private MiniGame2Manager _miniGame;

        protected override void Start()
        {
            base.Start();
            _miniGame = _sharpeningMiniGameObject.GetComponent<MiniGame2Manager>();
            _miniGame.OnGameEndResult += FinishCrafting;

            _craftButton.onClick.AddListener(StartMiniGame);
            _craftButton.interactable = false;
        }

        protected override void HandleSlotItemChange()
        {
            if (_resultSlot.Item is WeaponItem item) _craftButton.interactable = !item.IsSharpened;
            if (_resultSlot.IsEmpty) _craftButton.interactable = false;
        }

        private void StartMiniGame()
        {
            AudioManager.Instance.PlaySFX(_clip, transform);

            _miniGame.StartGame(_resultSlot.Item);
        }

        private void FinishCrafting(bool result)
        {
            if (!result)
            {
                ItemsManager.Instance.AllResources.TryGetValue("junk", out var junkResource);
                if (junkResource == null) return;

                var junk = new Item(junkResource);
                switch (_resultSlot.Item.Rarity)
                {
                    case Rarity.Uncommon:
                        junk.Value *= 2;
                        break;
                    case Rarity.Rare:
                        junk.Value *= 3;
                        break;
                    case Rarity.Epic:
                        junk.Value *= 4;
                        break;
                    case Rarity.Legendary:
                        junk.Value *= 5;
                        break;
                    default:
                        junk.Value = junkResource.baseValue;
                        break;
                }

                _resultSlot.SetItem(junk);
                return;
            }

            var sharpened = (WeaponItem)_resultSlot.Item;
            sharpened.Sharpen();
            _resultSlot.SetItem(sharpened);
            HandleSlotItemChange();
        }
    }
}