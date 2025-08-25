using System;
using System.Collections;
using Audio;
using Inventory;
using Inventory.Scriptable_Items;
using MiniGames.Scriptable_MiniGames;
using UnityEngine;
using UnityEngine.UI;
using Random = UnityEngine.Random;

namespace MiniGames
{
    public class MiniGame2Manager : MonoBehaviour
    {
        [SerializeField]
        private MiniGamePrefScriptableObject _commonPref, _uncommonPref, _rarePref, _epicPref, _legendaryPref;

        [SerializeField] private Transform _line, _base;
        [SerializeField] private Image _progressBar, _weaponSprite, _newWeaponSprite;
        [SerializeField] private float _playerForce, _pointProgress;

        [SerializeField] private WeaponItemScriptableObject _debugWeapon;

        [SerializeField] private AudioClip _failClip, _successClip, _winClip, _loseClip;

        [SerializeField] protected Button _tutorialButton;
        [SerializeField] protected TutorialPopUpScriptableObject _tutorialPopUpData;

        private int _direction = 1;
        private bool _firstTime = true;
        private Coroutine _gameCoroutine;
        private AudioSource _goodAudioSource, _badAudioSource;

        private float
            _minForceAI,
            _maxForceAI,
            _durationForceAI,
            _maxAngle,
            _minAngle,
            _pointZone,
            _currentScore,
            _maxScore;

        private void Awake()
        {
            _tutorialButton?.onClick.AddListener(ShowTutorial);
        }

        private void FixedUpdate()
        {
            UpdateScore();
            HandlePlayerInput();
        }

        public event Action<bool> OnGameEndResult;

        public void StartGame(Item weapon)
        {
            ClearGame();
            LoadPreferences(weapon.Rarity);

            _line.localPosition = new Vector3(_line.localPosition.x, _line.localPosition.y, 0);
            _currentScore = _maxScore / 2;
            _weaponSprite.sprite = (weapon.GetData() as WeaponItemScriptableObject)?.unsharpenedSprite;
            _newWeaponSprite.sprite = (weapon.GetData() as WeaponItemScriptableObject)?.sharpenedSprite;
            _line.Rotate(0, 0, Random.Range(-_maxAngle + 1, _maxAngle - 1));
            gameObject.SetActive(true);
            _gameCoroutine = StartCoroutine(RandomRotateRoutine());

            if (_firstTime)
            {
                TutorialPopUp.Instance.ShowTutorialPopUp(_tutorialPopUpData);
                _firstTime = false;
            }
        }

        private void ClearGame()
        {
            _currentScore = 0;
            _progressBar.fillAmount = 0;
            _newWeaponSprite.color = new Color(255, 255, 255, 0);
            _goodAudioSource = AudioManager.Instance.PlaySFXDontDestroy(_successClip, transform);
            _badAudioSource = AudioManager.Instance.PlaySFXDontDestroy(_failClip, transform);
            _badAudioSource.volume = 0f;
            _goodAudioSource.loop = true;
            _badAudioSource.loop = true;
        }

        private void UpdateScore()
        {
            if (_line.rotation.z >= -_pointZone / 180 && _line.rotation.z <= _pointZone / 180)
            {
                _currentScore += _pointProgress * _maxScore;
                _badAudioSource.volume = 0f;
            }
            else
            {
                _currentScore -= _pointProgress * _maxScore;
                _badAudioSource.volume = 1f;
            }

            var progress = _currentScore / _maxScore;

            _progressBar.fillAmount = progress;

            _newWeaponSprite.color = new Color(255, 255, 255, 1 * (progress - (1f - progress)));

            if (_currentScore >= _maxScore)
                EndGame(true);
            else if (_currentScore <= 0)
                EndGame(false);
        }

        private void EndGame(bool gameResult)
        {
            OnGameEndResult?.Invoke(gameResult);
            Destroy(_badAudioSource);
            Destroy(_goodAudioSource);
            AudioManager.Instance.PlaySFX(gameResult ? _winClip : _loseClip, transform);
            StopCoroutine(_gameCoroutine);
            gameObject.SetActive(false);
        }

        private void HandlePlayerInput()
        {
            var move = -Input.GetAxis("Vertical");
            if (move == 0) return;

            if (_line.rotation.z >= _maxAngle / 180 && move > 0) return;
            if (_line.rotation.z <= -_maxAngle / 180 && move < 0) return;

            _line.Rotate(0, 0, Time.deltaTime * _playerForce * move);
        }

        private IEnumerator RandomRotateRoutine()
        {
            while (true)
            {
                _direction *= -1;
                var force = Random.Range(_minForceAI, _maxForceAI);
                var duration = Random.Range(1f, _durationForceAI);
                var elapsedTime = 0f;

                while (elapsedTime < duration)
                {
                    elapsedTime += Time.deltaTime;

                    if (!((_line.rotation.z >= _maxAngle / 180 && _direction > 0) ||
                          (_line.rotation.z <= -_maxAngle / 180 && _direction < 0)))
                        _line.Rotate(0, 0, Time.deltaTime * force * _direction);

                    yield return null;
                }
            }
        }

        private void LoadPreferences(Rarity rarity)
        {
            var preferences = rarity switch
            {
                Rarity.Common => _commonPref,
                Rarity.Uncommon => _uncommonPref,
                Rarity.Rare => _rarePref,
                Rarity.Epic => _epicPref,
                Rarity.Legendary => _legendaryPref,
                _ => _uncommonPref
            };


            _pointZone = preferences.pointZone;
            _maxScore = preferences.maxScore;
            _minForceAI = preferences.minForceAI;
            _maxForceAI = preferences.maxForceAI;
            _durationForceAI = preferences.durationForceAI;
            _maxAngle = preferences.maxAngle;
        }

        public void ShowTutorial()
        {
            TutorialPopUp.Instance.ShowTutorialPopUp(_tutorialPopUpData);
        }
    }
}