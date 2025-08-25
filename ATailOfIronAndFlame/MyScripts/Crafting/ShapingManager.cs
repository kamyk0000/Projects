using System;
using Audio;
using Inventory;
using Inventory.Scriptable_Items;
using MiniGames;
using UnityEngine;
using UnityEngine.UI;

namespace Crafting
{
    public class ShapingManager : CraftingManager
    {
        [SerializeField] private Button _craftButton;
        [SerializeField] private GameObject _shapingMiniGameObject;
        
        private MiniGame1Manager _miniGame;

        protected override void Start()
        {
            base.Start();
            _miniGame = _shapingMiniGameObject.GetComponent<MiniGame1Manager>();
            _miniGame.OnGameEndResult += FinishCrafting;

            _craftButton.onClick.AddListener(StartMiniGame);
            _craftButton.interactable = false;
        }

        protected override void HandleSlotItemChange()
        {
            _craftButton.interactable = _resultSlot.Item is not WeaponItem;
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
                    case Rarity.Common:
                        junk.Value = junkResource.baseValue;
                        break;
                    default:
                        break;
                }

                _resultSlot.SetItem(junk);
                return;
            }

            var billet = _resultSlot.Item;
            var trueWeapon = new WeaponItem(billet.GetData() as WeaponItemScriptableObject ??
                                            throw new InvalidOperationException());
            _resultSlot.SetItem(trueWeapon);
            HandleSlotItemChange();
        }
    }
}