using UnityEngine;
using UnityEngine.EventSystems;

namespace Inventory
{
    public class DragAndDrop : MonoBehaviour, IBeginDragHandler, IEndDragHandler, IDragHandler
    {
        public static GameObject CurrentlyDraggedObject;
        private CanvasGroup _canvasGroup;
        private Transform _mainCanvas;
        private Transform _originalParent;
        private RectTransform _rectTransform;

        public void Awake()
        {
            _rectTransform = GetComponent<RectTransform>();
            _canvasGroup = GetComponent<CanvasGroup>();
        }

        public void Start()
        {
            _mainCanvas ??= GameObject.Find("CraftingCanvas").GetComponent<RectTransform>();
        }

        public void OnBeginDrag(PointerEventData eventData)
        {
            if (eventData.button != PointerEventData.InputButton.Left) return;
            _originalParent = transform.parent;
            transform.SetParent(_mainCanvas.transform);
            _canvasGroup.alpha = 0.7f;
            _canvasGroup.blocksRaycasts = false;
            CurrentlyDraggedObject = gameObject;
        }

        public void OnDrag(PointerEventData eventData)
        {
            if (eventData.button != PointerEventData.InputButton.Left) return;

            RectTransformUtility.ScreenPointToLocalPointInRectangle(
                _rectTransform.parent as RectTransform,
                eventData.position,
                eventData.pressEventCamera,
                out var localPointerPosition
            );
            _rectTransform.anchoredPosition = localPointerPosition;
        }

        public void OnEndDrag(PointerEventData eventData)
        {
            if (eventData.button != PointerEventData.InputButton.Left) return;
            transform.SetParent(_originalParent);
            _canvasGroup.alpha = 1f;
            _canvasGroup.blocksRaycasts = true;
            transform.localPosition = Vector2.zero;
            CurrentlyDraggedObject = null;
        }
    }
}