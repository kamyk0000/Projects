using System;

namespace Interactive
{
    public interface IInteractable
    {
        public void Interact();
        public void CancelInteract();
        public event Action OnCancelInteract;
    }
}