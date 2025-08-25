using System.Collections.Generic;
using System.Linq;
using Player;
using UnityEngine;
using Utilities;

namespace Interactive
{
    public class InteractableManager : Singleton<InteractableManager>
    {
        [SerializeField] private GameObject _rightSideUI;
        
        private List<InteractableObject> _interactables = new();
        private bool _isInteracting;
        private PlayerMovement _playerMovement;
        private Transform _playerTransform;
        private readonly float _updateInterval = 0.3f;
        private float _timer;

        protected override void Awake()
        {
            base.Awake();
            _playerTransform = GameObject.FindGameObjectWithTag("Player").transform;
            _playerMovement = _playerTransform.GetComponent<PlayerMovement>();
        }

        private void Update()
        {
            if (_interactables.Count <= 0 && (Input.GetKeyDown(KeyCode.E) || (Input.GetKeyDown(KeyCode.Escape) && _rightSideUI.activeSelf)))
            {
                _rightSideUI.SetActive(!_rightSideUI.activeSelf);
            }
            
            if (_interactables.Count <= 0) return;

            if (Input.GetKeyDown(KeyCode.E) && !_isInteracting)
            {
                SortInteractables();
                InteractWithClosest();
            }
            else if (_isInteracting && (Input.GetKeyDown(KeyCode.E) || Input.GetKeyDown(KeyCode.Escape)))
            {
                StopInteracting();
            }

            if (_interactables.Count <= 1)
            {
                var interactable = GetClosestInteractable();
                if (interactable != null) interactable.HighlightInteractable(true);
                return;
            }

            _timer += Time.deltaTime;
            if (_timer >= _updateInterval)
            {
                _timer = 0f;
                SortInteractables();
            }
        }

        public void AddInteractable(InteractableObject interactableObject)
        {
            _interactables.Add(interactableObject);
            interactableObject.Interactable.OnCancelInteract += StopInteracting;
        }

        public void RemoveInteractable(InteractableObject interactableObject)
        {
            interactableObject.HighlightInteractable(false);
            interactableObject.Interactable.OnCancelInteract -= StopInteracting;
            _interactables.Remove(interactableObject);
        }

        private void InteractWithClosest()
        {
            if (_playerMovement.blockPlayerMovement) return;
            _isInteracting = true;
            _playerMovement.blockPlayerMovement = true;
            GetClosestInteractable().Interact();
        }

        private void StopInteracting()
        {
            _isInteracting = false;
            GetClosestInteractable().CancelInteract();
            _playerMovement.blockPlayerMovement = false;
        }

        private InteractableObject GetClosestInteractable()
        {
            if (_interactables == null || _interactables.Count == 0) return null;
            return _interactables[0];
        }

        private void SortInteractables()
        {
            if (_interactables.Count <= 1) return;

            _interactables = _interactables
                .Where(obj => obj is not null)
                .OrderBy(obj => (obj.transform.position - _playerTransform.position).sqrMagnitude)
                .ToList();

            _interactables[0].HighlightInteractable(true);

            for (var i = 1; i < _interactables.Count; i++) _interactables[i].HighlightInteractable(false);
        }
    }
}