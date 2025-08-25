using System;
using System.Collections;
using TMPro;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

namespace Inventory
{
    [RequireComponent(typeof(DragAndDrop))]
    public class SlotUI : MonoBehaviour, IDropHandler, IPointerClickHandler, IPointerEnterHandler, IPointerExitHandler
    {
        [SerializeField] protected TMP_Text _stackCounter;
        [SerializeField] private Image _itemIcon;
        [SerializeField] private Sprite _isLockedSprite;
        [SerializeField] private float _doubleClickThreshold = 0.3f;
        [SerializeField] private DragAndDrop _dragAndDrop;
        
        private float _lastClickTime;
        public DragAndDrop DragAndDrop => _dragAndDrop;

        private void Awake()
        {
            _dragAndDrop = _dragAndDrop == null ? GetComponent<DragAndDrop>() : _dragAndDrop;
        }

        private void OnEnable()
        {
            StartCoroutine(WaitForChildrenActive());
        }

        public void OnDisable()
        {
            OnItemHovered?.Invoke(false);
        }

        public void OnDrop(PointerEventData eventData)
        {
            if (eventData.button != PointerEventData.InputButton.Left) return;
            var draggedUI = DragAndDrop.CurrentlyDraggedObject?.GetComponent<SlotUI>();
            if (draggedUI == null) return;
            OnItemDropped?.Invoke(this, draggedUI);
        }

        public void OnPointerClick(PointerEventData eventData)
        {
            var draggedItem = DragAndDrop.CurrentlyDraggedObject?.GetComponent<SlotUI>();
            if (draggedItem == null)
            {
                if (eventData.button == PointerEventData.InputButton.Left)
                {
                    var timeSinceLastClick = Time.time - _lastClickTime;
                    if (timeSinceLastClick <= _doubleClickThreshold)
                        OnSlotInteracted?.Invoke(this, SlotInteraction.Group);
                    _lastClickTime = Time.time;
                    if (Input.GetKey(KeyCode.LeftShift) && Input.GetKey(KeyCode.LeftControl))
                        OnSlotInteracted?.Invoke(this, SlotInteraction.MoveAll);
                    else if (Input.GetKey(KeyCode.LeftShift)) OnSlotInteracted?.Invoke(this, SlotInteraction.Move);
                }
                else if (eventData.button == PointerEventData.InputButton.Middle)
                {
                    OnSlotInteracted?.Invoke(this, SlotInteraction.AutoHalfSplit);
                }
            }
            else
            {
                if (PointerEventData.InputButton.Middle == eventData.button)
                    OnSlotInteracted?.Invoke(draggedItem, SlotInteraction.HalfSplit);
                else if (PointerEventData.InputButton.Right == eventData.button)
                    OnSlotInteracted?.Invoke(draggedItem, SlotInteraction.SingleSplit);
            }
        }

        public void OnPointerEnter(PointerEventData eventData)
        {
            OnItemHovered?.Invoke(true);
        }

        public void OnPointerExit(PointerEventData eventData)
        {
            OnItemHovered?.Invoke(false);
        }

        public event Action<SlotUI, SlotUI> OnItemDropped;
        public event Action<SlotUI, SlotInteraction> OnSlotInteracted;
        public event Action<bool> OnItemHovered;
        public event Action OnSlotEnabled;

        public void LockSlot()
        {
            _itemIcon.sprite = _isLockedSprite;
            _itemIcon.enabled = true;
            _stackCounter.text = "";
            _stackCounter.enabled = false;

            //_dragAndDrop.enabled = false;
        }

        public void UnlockSlot()
        {
            UpdateUI();
            //_dragAndDrop.enabled = true;
        }

        public virtual void UpdateUI(Item item = null, int? itemStackCount = -1)
        {
            if (item?.ItemSprite is not null || itemStackCount > 0)
            {
                _itemIcon.sprite = item?.ItemSprite;
                _itemIcon.enabled = true;
                _stackCounter.text = itemStackCount > 1 ? itemStackCount.ToString() : "";
                _stackCounter.enabled = true;
            }
            else
            {
                _itemIcon.sprite = null;
                _itemIcon.enabled = false;
                _stackCounter.text = "";
                _stackCounter.enabled = false;
            }
        }

        private bool AreAllChildrenActive()
        {
            foreach (Transform child in gameObject.transform)
                if (!child.gameObject.activeInHierarchy)
                    return false;
            return true;
        }

        private IEnumerator WaitForChildrenActive()
        {
            yield return new WaitUntil(AreAllChildrenActive);
            OnSlotEnabled?.Invoke();
        }
    }
}