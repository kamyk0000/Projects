using Audio;
using UnityEngine;

namespace MiniGames
{
    public class Ball : MonoBehaviour
    {
        public ScorePoint ScorePoint { get; set; }

        private void OnTriggerEnter2D(Collider2D other)
        {
            if (!other.CompareTag("Point")) return;
            ScorePoint = other.GetComponent<ScorePoint>();
        }

        private void OnTriggerExit2D(Collider2D other)
        {
            if (!other.CompareTag("Point")) return;
            if (other.GetComponent<ScorePoint>() != ScorePoint) return;
            ScorePoint = null;
        }

        public void ShowMiss(AudioClip clip)
        {
            AudioManager.Instance.PlaySFX(clip, transform);
        }
    }
}