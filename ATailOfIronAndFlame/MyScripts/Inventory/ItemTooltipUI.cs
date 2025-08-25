using System.Collections;
using System.Globalization;
using TMPro;
using UnityEngine;
using UnityEngine.UI;
using Utilities;

namespace Inventory
{
    public class ItemTooltipUI : Singleton<ItemTooltipUI>
    {
        [SerializeField] private TMP_Text _itemName, _itemRarity, _itemDescription, _itemValue;
        [SerializeField] private float _showDelay = 0.1f;
        [SerializeField] private Canvas _mainCanvas;
        [SerializeField] private Vector2 _offset;
        
        private CanvasGroup _canvasGroup;
        private RectTransform _canvasRectTransform;
        private RectTransform _rectTransform;
        private Coroutine _showTooltipCoroutine;

        protected override void Awake()
        {
            base.Awake();

            _rectTransform = GetComponent<RectTransform>();
            _canvasGroup = GetComponent<CanvasGroup>();
            _canvasRectTransform = _mainCanvas.GetComponent<RectTransform>();

            enabled = false;
            _canvasGroup.alpha = 0;
            _canvasGroup.blocksRaycasts = false;
        }

        private void Update()
        {
            if (enabled) UpdatePosition(false);
        }

        public void HideTooltip()
        {
            if (_showTooltipCoroutine != null) StopCoroutine(_showTooltipCoroutine);
            enabled = false;
            _canvasGroup.alpha = 0;
            _canvasGroup.blocksRaycasts = false;
            UpdatePosition(true);
        }

        public void ShowTooltip(Item item)
        {
            if (_showTooltipCoroutine != null) StopCoroutine(_showTooltipCoroutine);
            if (item is null) return;
            _showTooltipCoroutine = StartCoroutine(ShowTooltipAfterDelay(item));
        }

        private IEnumerator ShowTooltipAfterDelay(Item item)
        {
            yield return new WaitForSeconds(_showDelay);
            UpdatePosition(false);
            enabled = true;
            _itemName.text = item.Name;
            _itemRarity.text = item.Rarity.ToString();
            _itemRarity.color = item.Rarity.GetTextColor().TextColor;
            _itemDescription.text = item.Description;
            _itemValue.text = item.Value.ToString(CultureInfo.InvariantCulture);
            LayoutRebuilder.ForceRebuildLayoutImmediate(_rectTransform);
            _canvasGroup.alpha = 1;
            _canvasGroup.blocksRaycasts = true;
        }

        private void UpdatePosition(bool reset)
        {
            if (reset) _rectTransform.anchoredPosition = new Vector2(0, 0);

            RectTransformUtility.ScreenPointToLocalPointInRectangle(
                _mainCanvas.transform as RectTransform,
                Input.mousePosition,
                null, //or _mainCanvas.worldCamera if canvas woud be for camera 
                out var localPointerPosition
            );
            localPointerPosition += _offset;

            if (localPointerPosition.y + _rectTransform.rect.height > _canvasRectTransform.rect.height / 2)
                localPointerPosition.y -= _rectTransform.rect.height + _offset.y * 4;
            if (localPointerPosition.x + _rectTransform.rect.width > _canvasRectTransform.rect.width / 2)
                localPointerPosition.x -= _rectTransform.rect.width + _offset.x * 2;

            _rectTransform.anchoredPosition = localPointerPosition;
        }
    }
}