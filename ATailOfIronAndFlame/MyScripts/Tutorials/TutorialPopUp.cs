using Audio;
using TMPro;
using Unity.VisualScripting;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Video;

public class TutorialPopUp : Utilities.Singleton<TutorialPopUp>
{
    [SerializeField] private GameObject _popUpUI;
    [SerializeField] private TMP_Text _titleText, _descriptionText;
    [SerializeField] private Button _exitButton;
    [SerializeField] private RawImage _mediaContainer;
    [SerializeField] private VideoPlayer _videoPlayer;
    [SerializeField] private RenderTexture _renderTexture;
    [SerializeField] private AudioClip _showClip, _hideClip;

    private void Update()
    {
        if (!_popUpUI.activeSelf) return;
        if (Input.anyKeyDown) HideTutorialPopUp();
    }

    public void ShowTutorialPopUp(TutorialPopUpScriptableObject data)
    {
        AudioManager.Instance.PlaySFX(_showClip, _popUpUI.transform);
        ShowTutorialPopUp(data.title, data.description, data.video, data.image);
    }

    public void ShowTutorialPopUp(string title, string description, VideoClip video, Texture image)
    {
        _titleText.text = title;
        _descriptionText.text = description;

        _videoPlayer.Stop();
        _mediaContainer.gameObject.SetActive(true);
        if (!video.IsUnityNull())
        {
            _mediaContainer.texture = _renderTexture;
            _videoPlayer.clip = video;
            _videoPlayer.Play();
        }
        else if (!image.IsUnityNull())
        {
            _mediaContainer.texture = image;
        }
        else
        {
            _mediaContainer.gameObject.SetActive(false);
        }

        Time.timeScale = 0;
        _popUpUI.SetActive(true);
    }

    public void HideTutorialPopUp()
    {
        _popUpUI.SetActive(false);
        Time.timeScale = 1;
        _titleText.text = null;
        _descriptionText.text = null;
        _videoPlayer.clip = null;
        _mediaContainer.texture = null;
        AudioManager.Instance.PlaySFX(_hideClip, _popUpUI.transform);
    }
}