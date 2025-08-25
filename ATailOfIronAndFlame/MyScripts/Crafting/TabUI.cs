using System;
using Audio;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;
using Utilities;

namespace Crafting
{
    public class TabUI : MonoBehaviour, IPointerClickHandler
    {
        [SerializeField] private Image _background;
        [SerializeField] private Image _icon;
        [SerializeField] private Sprite _selectedTab;
        [SerializeField] private Sprite _unselectedTab;
        [SerializeField] private AudioClip _clip;
        private CraftingType _type;

        public void OnPointerClick(PointerEventData eventData)
        {
            AudioManager.Instance.PlaySFX(_clip, transform);
            OnTabClicked?.Invoke(this);
            SelectTab();
        }

        public event Action<TabUI> OnTabClicked;

        public void Initialize(CraftingType type)
        {
            UnselectTab();
            SetIcon(type.GetSprite().Sprite);
            SetCraftingType(type);
        }

        public void SetCraftingType(CraftingType type)
        {
            _type = type;
        }

        public CraftingType GetCraftingType()
        {
            return _type;
        }

        public void SetIcon(Sprite icon)
        {
            _icon.sprite = icon;
        }

        public void SelectTab()
        {
            _background.sprite = _selectedTab;
        }

        public void UnselectTab()
        {
            _background.sprite = _unselectedTab;
        }
    }
}