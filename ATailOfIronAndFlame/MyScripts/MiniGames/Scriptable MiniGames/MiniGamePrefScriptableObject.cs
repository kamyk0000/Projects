using UnityEngine;

namespace MiniGames.Scriptable_MiniGames
{
    [CreateAssetMenu(menuName = "Scriptable Objects/MiniGames/Create MiniGame Pref", fileName = "New Pref File")]
    public class MiniGamePrefScriptableObject : ScriptableObject
    {
        [Header("MiniGame1")] public float ballSpeedMultiplier;

        public int maxScore, maxFails, scorePointsVariants, maxPresentScorePoints;
        [Header("MiniGame2")] public float pointZone;

        public float pointProgress,
            maxAngle,
            minForceAI,
            maxForceAI,
            durationForceAI;
    }
}