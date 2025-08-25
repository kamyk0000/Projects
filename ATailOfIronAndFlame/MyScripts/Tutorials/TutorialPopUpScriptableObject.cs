using JetBrains.Annotations;
using UnityEngine;
using UnityEngine.Video;

[CreateAssetMenu(menuName = "Scriptable Objects/Tutorials/Create PopUp Data", fileName = "New Data File")]
public class TutorialPopUpScriptableObject : ScriptableObject
{
    public string title;
    [TextArea] public string description;
    [CanBeNull] public VideoClip video;
    [CanBeNull] public Texture image;
}