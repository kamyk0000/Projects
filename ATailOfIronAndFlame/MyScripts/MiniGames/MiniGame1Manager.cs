using System;
using System.Collections;
using System.Collections.Generic;
using Audio;
using Inventory;
using Inventory.Scriptable_Items;
using MiniGames.Scriptable_MiniGames;
using TMPro;
using UnityEngine;
using UnityEngine.UI;
using Random = UnityEngine.Random;

namespace MiniGames
{
    public class MiniGame1Manager : MonoBehaviour
    {
        [SerializeField]
        private MiniGamePrefScriptableObject _commonPref, _uncommonPref, _rarePref, _epicPref, _legendaryPref;

        [SerializeField] private List<GameObject> _scorePointsList;
        [SerializeField] private RectTransform _line, _ballRect;
        [SerializeField] private Ball _ball;
        [SerializeField] private float _baseBallSpeed;

        [SerializeField] private Image _progressBar, _weaponSprite, _newWeaponSprite;
        [SerializeField] private TMP_Text _lifesLeftText;

        [SerializeField] private AudioClip _failClip, _successClip, _winClip, _loseClip;

        [SerializeField] private WeaponItemScriptableObject _weapon;

        [SerializeField] protected Button _tutorialButton;
        [SerializeField] protected TutorialPopUpScriptableObject _tutorialPopUpData;
        private readonly List<Vector2> _occupiedPositions = new();

        private float
            _currentScore,
            _maxScore,
            _currentPresentScorePoints,
            _maxPresentScorePoints,
            _scorePointsVariants,
            _ballSpeed;

        private bool _firstTime = true;
        private Coroutine _gameCoroutine;

        private Vector3 _leftBoundary, _rightBoundary, _target;

        private void Awake()
        {
            _tutorialButton?.onClick.AddListener(ShowTutorial);
        }

        private void FixedUpdate()
        {
            UpdateScores(-(_maxScore * 0.001f));
        }

        public event Action<bool> OnGameEndResult;

        private void Listen()
        {
            if (!Input.anyKeyDown) return;

            var keyPressed = Input.inputString;
            if (string.IsNullOrEmpty(keyPressed)) return;

            if (Enum.TryParse(keyPressed, true, out KeyCode code)) TryScorePoint(code);
        }

        public void StartGame(Item weapon)
        {
            ClearGame();
            LoadPreferences(weapon.Rarity);
            SpawnScorePoints();

            //_ballRect.anchoredPosition = new Vector2(_line.rect.width / 2, 0);
            _currentScore = _maxScore / 2;
            _weaponSprite.sprite = weapon.ItemSprite;
            _newWeaponSprite.sprite = (weapon.GetData() as WeaponItemScriptableObject)?.unsharpenedSprite;
            gameObject.SetActive(true);
            _gameCoroutine = StartCoroutine(Move());

            if (_firstTime)
            {
                TutorialPopUp.Instance.ShowTutorialPopUp(_tutorialPopUpData);
                _firstTime = false;
            }
        }

        private void ClearGame()
        {
            _currentScore = 0;
            _currentPresentScorePoints = 0;
            _occupiedPositions.Clear();
            for (var i = 2; i < _line.transform.childCount - 1; i++) Destroy(_line.transform.GetChild(i).gameObject);
            _rightBoundary = new Vector2(_line.rect.width / 2, 0);
            _leftBoundary = new Vector2(-(_line.rect.width / 2), 0);
            _progressBar.fillAmount = 0;
            _newWeaponSprite.color = new Color(255, 255, 255, 0);
            _target = _rightBoundary;
        }

        private IEnumerator Move()
        {
            while (true)
            {
                _ballRect.anchoredPosition =
                    Vector2.MoveTowards(_ballRect.anchoredPosition, _target, _ballSpeed * Time.deltaTime);
                if (Vector3.Distance(_ballRect.anchoredPosition, _target) <= 0.01f)
                    _target = _target == _rightBoundary ? _leftBoundary : _rightBoundary;
                Listen();
                yield return null;
            }
        }

        private void TryScorePoint(KeyCode keyPressed)
        {
            var scorePoint = _ball.ScorePoint;

            if (scorePoint is null)
            {
                _ball.ShowMiss(_failClip);
                UpdateScores(-1);
            }
            else if (scorePoint.IsKeyCorrect(keyPressed))
            {
                UpdateScores(scorePoint.GetPointValue());
                _currentPresentScorePoints--;
                var vec = scorePoint.pointPosition;
                _occupiedPositions.Remove(vec);

                _ball.ScorePoint = null;
                scorePoint.CollectPoint(_successClip);
            }
            else
            {
                scorePoint.ShowWrongKey(_failClip);
            }

            SpawnScorePoints();
        }

        private void SpawnScorePoints()
        {
            if (Mathf.Approximately(_currentPresentScorePoints, _maxPresentScorePoints)) return;

            var num = Random.Range(1, _maxPresentScorePoints - _currentPresentScorePoints);
            for (var i = 0; i < num; i++)
            {
                var go = Instantiate(_scorePointsList[(int)Random.Range(0, _scorePointsVariants)], _line);
                var rt = go.GetComponent<RectTransform>();
                rt.SetSiblingIndex(2);

                var emptyPos = GetRandomEmptyPosition(rt);
                if (emptyPos == new Vector2(-1, 0))
                {
                    Destroy(go);
                    continue;
                }

                rt.anchoredPosition = emptyPos;
                var occupiedPos = new Vector2(rt.anchoredPosition.x - rt.rect.width / 2,
                    rt.anchoredPosition.x + rt.rect.width / 2);
                go.GetComponent<ScorePoint>().pointPosition = occupiedPos;
                _occupiedPositions.Add(occupiedPos);
                _occupiedPositions.Sort((a, b) => a.x.CompareTo(b.x));
                _currentPresentScorePoints++;
            }
        }

        private Vector2 GetRandomEmptyPosition(RectTransform point)
        {
            if (_occupiedPositions.Count <= 0) return new Vector2(Random.Range(0, _line.rect.width), 0);

            var emptyRanges = new List<Vector2>();
            var startPos = 0f;
            foreach (var range in _occupiedPositions)
            {
                if (range.x - startPos >= point.rect.width)
                    emptyRanges.Add(new Vector2(startPos + point.rect.width / 2, range.x - point.rect.width / 2));
                startPos = range.y;
            }

            if (_line.rect.width - startPos >= point.rect.width)
                emptyRanges.Add(new Vector2(startPos + point.rect.width / 2, _line.rect.width - point.rect.width / 2));

            if (emptyRanges.Count <= 0) return new Vector2(-1, 0);
            var randomRange = emptyRanges[Random.Range(0, emptyRanges.Count)];
            return new Vector2(Random.Range(randomRange.x, randomRange.y), 0);
        }

        private void UpdateScores(float scoreToUpdate)
        {
            _currentScore += scoreToUpdate;

            if (_currentScore <= 0)
                EndGame(false);
            else if (_currentScore >= _maxScore) EndGame(true);

            var progress = _currentScore / _maxScore;

            _progressBar.fillAmount = progress;
            _newWeaponSprite.color = new Color(255, 255, 255, 1 * (progress - (1f - progress)));
        }

        private void EndGame(bool gameResult)
        {
            OnGameEndResult?.Invoke(gameResult);
            AudioManager.Instance.PlaySFX(gameResult ? _winClip : _loseClip, transform);
            StopCoroutine(_gameCoroutine);
            gameObject.SetActive(false);
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

            _ballSpeed = _baseBallSpeed * preferences.ballSpeedMultiplier;
            _maxScore = preferences.maxScore;
            _scorePointsVariants = preferences.scorePointsVariants;
            _maxPresentScorePoints = preferences.maxPresentScorePoints;
        }

        public void ShowTutorial()
        {
            TutorialPopUp.Instance.ShowTutorialPopUp(_tutorialPopUpData);
        }
    }
}