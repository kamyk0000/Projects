using System.Collections;
using UnityEngine;
using Utilities;

namespace Interactive
{
    public class InteractablePrompt : Singleton<InteractablePrompt>
    {
        [SerializeField] private float _moveDelay, _moveDistance, _tranformOffset;

        private Coroutine _moveCoroutine;

        protected override void Awake()
        {
            base.Awake();
            HideInteractive();
        }

        public void ShowInteractive(Transform destination)
        {
            transform.position = destination.position + new Vector3(0, _tranformOffset, 0);
            gameObject.SetActive(true);
            if (_moveCoroutine != null) StopCoroutine(_moveCoroutine);
            _moveCoroutine = StartCoroutine(MoveCoroutine());
        }

        public void HideInteractive()
        {
            if (_moveCoroutine != null) StopCoroutine(_moveCoroutine);
            gameObject.SetActive(false);
        }

        private IEnumerator MoveCoroutine()
        {
            var direction = 1;
            while (true)
            {
                transform.transform.localPosition += new Vector3(0, _moveDistance * direction, 0);
                direction *= -1;
                yield return new WaitForSeconds(_moveDelay);
            }
        }
    }
}