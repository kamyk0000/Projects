# 🎮 CollisionsSimGame
[**PL**]
**CollisionsSimGame** - to prosta gra symulacyjna, obsługująca zderzenia sfer i pudełek, zaprogramowana w silniku **Godot**, wykorzystująca customowy silnik kolizji obiektów. Projekt został stworzony na potrzebę zaliczenia kursu Silniki Fizyki Gier na Uniwersytecie Jagiellońskim.

Projekt nie wykorzystuje żadnych wbudowanych mechanik (np. kolizji) w Godotcie prócz `MeshInstance3D` do wizualizacji obiektów.
Nie implementuje szerokiej fazy detekcji obiektów (działa na fazie blizkiej z dodatkowym elementem uśpienia).

Obsługuje zderzenia (detekcja kolizji, impuls fizyczny):
- Kul (na podstawie _promienia_)
- Kuli i pudełka (z algorytmem _OBB_)
- Pudełek (z wykorzystaniem zoptymalizowanego algorytmu _SAT_)

Muszę przyznać że projekt był częściowo vibe-codowany z uwagi na równoległą naukę zagadnienia obsługi zderzeń w silnikach gier.

---

[**EN**]
**CollisionsSimGame** - is a simple simulation game that handles sphere and box collisions, programmed in the **Godot** engine and built around a custom object‑collision system. 
The project was created for the purposes of the Game Physics Engines course at Jagiellonian University.

The project does not use any built‑in Godot mechanics (such as collisions), except for `MeshInstance3D` for object visualization. It does not implement a broad‑phase collision detection system (it operates in the narrow-phase with an additional sleep mechanism).

It supports collisions (collision detection and physical impulse) for:
- Spheres (based on _radius_)
- Sphere–box collisions (using an _OBB_ algorithm)
- Boxes (using an optimized _SAT_ algorithm)

I have to admit the project was partially vibe‑coded, due to parallel learning of collision‑handling concepts in game engines.

---

## 🚀 Download

### 🔧 Launch

1. Download the directory [Build](Build).
2. Unpack the directory and run **CollisionSimGame.exe** file.

---


