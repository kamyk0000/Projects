using System.Collections;
using Audio;
using UnityEngine;

namespace Interactive
{
    [RequireComponent(typeof(Collider2D))]
    public class InteractableObject : MonoBehaviour
    {
        [SerializeField] private AudioClip _interactSound, _cancelInteractSound;
        [SerializeField] private MonoBehaviour _interactableTarget;
        [SerializeField] private Color _color = Color.yellow * new Vector4(1f, 1f, 1f, 0.5f);
        [SerializeField] private float _flashSpeed = 5f, _flashIntensity = 0.2f;
        [SerializeField] private bool _canHighlight = true;
        
        private Coroutine _flashCoroutine;
        private IInteractable _interactable;
        private bool _isHighlighted; //canHighlight?
        private SpriteRenderer _mainSpriteRenderer, _outlineSpriteRenderer;
        private GameObject _outlineObject;
        
        public IInteractable Interactable => _interactable;

        private void Awake()
        {
            _mainSpriteRenderer = GetComponent<SpriteRenderer>();

            _outlineObject = new GameObject("Outline");
            _outlineObject.transform.SetParent(transform);
            _outlineObject.transform.localPosition = new Vector3(0, -0.001f, 0);
            _outlineObject.transform.localScale = new Vector3(1, 1, 0);

            _outlineSpriteRenderer = _outlineObject.AddComponent<SpriteRenderer>();
            _outlineSpriteRenderer.sprite = _mainSpriteRenderer.sprite;
            _outlineSpriteRenderer.sortingLayerID = _mainSpriteRenderer.sortingLayerID;
            _outlineSpriteRenderer.sortingOrder = _mainSpriteRenderer.sortingOrder;
            _outlineSpriteRenderer.color = _color;

            _outlineObject.SetActive(false);
        }

        private void Start()
        {
            _interactable = _interactableTarget as IInteractable;
        }

        private void OnTriggerEnter2D(Collider2D other)
        {
            if (!other.CompareTag("Player") || other is not CapsuleCollider2D) return;
            InteractableManager.Instance.AddInteractable(this);
        }

        private void OnTriggerExit2D(Collider2D other)
        {
            if (!other.CompareTag("Player") || other is not CapsuleCollider2D) return;
            InteractableManager.Instance.RemoveInteractable(this);
        }

        public void HighlightInteractable(bool doHighlight)
        {
            if (!_canHighlight) return;
            
            switch (doHighlight)
            {
                case true when !_isHighlighted:
                    _isHighlighted = true;
                    _outlineObject.SetActive(true);
                    _flashCoroutine = StartCoroutine(Flash());
                    InteractablePrompt.Instance.ShowInteractive(transform);
                    break;
                case false when _isHighlighted:
                    _isHighlighted = false;
                    InteractablePrompt.Instance.HideInteractive();
                    StopCoroutine(_flashCoroutine);
                    _outlineObject.SetActive(false);
                    break;
            }
        }

        private IEnumerator Flash()
        {
            while (true)
            {
                var t = Mathf.Sin(Time.time * _flashSpeed) * _flashIntensity;
                _outlineSpriteRenderer.color = _color * (1f + t);
                yield return null;
            }
        }

        public void Interact()
        {
            AudioManager.Instance.PlaySFX(_interactSound, transform);
            _interactable?.Interact();
        }

        public void CancelInteract()
        {
            AudioManager.Instance.PlaySFX(_cancelInteractSound, transform);
            _interactable?.CancelInteract();
        }
    }
}