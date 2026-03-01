# Blackjack LWJGL Game

A 3D Blackjack game built using Java, the [Lightweight Java Game Library (LWJGL 3)](www.lwjgl.org), and the [Assimp](https://github.com/assimp/assimp) asset import library. This project features a fully rendered casino environment, animated dealer models, spatial audio, and interactive 3D chip betting mechanics.

## Features

* **3D Graphics Engine:** Custom engine implementation using OpenGL, featuring spot lights, point lights, and ambient lighting.
* **Asset Loading:** Advanced model loading via *Assimp* supporting `.obj` and `.md5mesh` formats with material and texture mapping.
* **Skeletal Animation:** Support for animated 3D entities (e.g., the dealer "Bob") using bone transformation matrices.
* **Spatial Audio:** OpenAL-based sound system with 3D attenuation and listener tracking.
* **Interactive GUI:** Integration with Dear ImGui for real-time light controls and game state debugging.
* **Cross-Platform Build:** Configured with Gradle to automatically handle native dependencies for Windows, Linux, and macOS.

## Prerequisites

* **Java Development Kit (JDK) 21:** The project uses recent version Java features and requires JDK 21 or higher.
* **OpenGL 3.3+ Compatible Hardware:** The rendering pipeline uses programmable shaders (GLSL).
* **OpenAL:** Required for the sound manager.

## Project Structure

The project follows the standard Gradle directory layout:

* `src/main/java`: Contains the game logic, engine core, and rendering pipeline.
* `src/main/resources`: Contains all game assets including:
* `/models`: 3D objects for cards, chips, and environment.
* `/shaders`: GLSL vertex and fragment shaders.
* `/sounds`: Game music and sound effects.



## Building and Running

The project includes the Gradle Wrapper, so you do not need a global Gradle installation.

### On Linux/macOS

1. Ensure the wrapper has execution permissions:
```bash
chmod +x gradlew

```


2. Run the application:
```bash
./gradlew run

```



### On Windows

Run the application using the command prompt or PowerShell:

```powershell
.\gradlew.bat run

```

## Controls

* **Movement:** Use `W`, `A`, `S`, `D` to navigate the camera.
* **Elevation:** Use `UP` and `DOWN` arrows to move the camera vertically.
* **Interaction:** * `Left Click`: Select chips to bet.
* `Right Click`: Remove/undo bets.


* **Debug Tools:** * `O`: Toggle the Light Controls UI panel (*Note:* currently buggy)

## Dependencies

* **LWJGL 3:** Core library for OpenGL, OpenAL, and GLFW.
* [**JOML:**](https://github.com/JOML-CI/JOML) Java OpenGL Math Library for 3D transformations.
* [**Dear ImGui:**](https://github.com/ocornut/imgui) For the graphical user interface.
* **Assimp:** For 3D model importing.

## Discussion, credits and acknowledgements

Developed as a demo of a 3D game engine principles in Java. My interest on videogames propelled the idea of getting to know how a game engine works "under the hood" and having a common interest with the contributors in learning Java motivated us towards working on the project, seeing this as a great opportunity for familiarizing ourselves with the language while working on something that is related to our common hobby as well: videogames.  
*PS:* learning about the math concepts behind meshes, shadows and lights was... funny, I think everyone interested should try building a game engine once (jk).  

**Contributors** to this work are:
[Nicolas Vega](github.com/NicoVegaPortaluppi) in charge of the blackjack game logic, 3D modeling and handling the usage of the engine (setting up the scene).
[Aristides Gernhofer](github.com/CarlosAristGernhofer) contributed to the in-game sounds, as well as fixing some bugs with the Camera.
[hadaperdida](github.com/hadaperdida) in charge of UI and menu configs.


---

