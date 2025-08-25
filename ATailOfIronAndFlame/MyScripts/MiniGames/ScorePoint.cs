using System.Collections;
using Audio;
using TMPro;
using UnityEngine;
using UnityEngine.UI;

namespace MiniGames
{
    public class ScorePoint : MonoBehaviour
    {
        [SerializeField] private KeyCode _keyCode;
        [SerializeField] private float _pointValue;
        public Vector2 pointPosition;

        [SerializeField] private TMP_Text _text;
        private Image _image;

        private void Awake()
        {
            _text = gameObject.GetComponentInChildren<TMP_Text>();
            _image = gameObject.GetComponentInChildren<Image>();
        }

        private void Start()
        {
            _text.text = _keyCode.ToString();
        }

        public bool IsKeyCorrect(KeyCode keyCode)
        {
            return keyCode == _keyCode;
        }

        public float GetPointValue()
        {
            return _pointValue;
        }

        public string GetPointText()
        {
            return _text.text;
        }

        public void ShowWrongKey(AudioClip clip)
        {
            AudioManager.Instance.PlaySFX(clip, transform);
            StartCoroutine(WrongKeyHighlight());
        }

        public void CollectPoint(AudioClip clip)
        {
            AudioManager.Instance.PlaySFX(clip, transform);
            StartCoroutine(CorrectPointHighlight());
        }

        private IEnumerator WrongKeyHighlight()
        {
            _text.color = Color.red;
            _text.transform.localScale *= 1.2f;
            yield return new WaitForSeconds(0.2f);
            _text.color = Color.white;
            _text.transform.localScale = new Vector3(1, 1, 0);
        }

        private IEnumerator CorrectPointHighlight()
        {
            var time = 0.5f;
            _image.color *= Color.green;
            while (time > 0)
            {
                _image.transform.localScale *= 0.99f;
                time -= Time.deltaTime;
                yield return null;
            }

            Destroy(gameObject);
        }
    }
}