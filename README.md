# MIDI-Based Gesture Recognition for Real-Time Navigation of Digital Music Scores

This project is a JavaFX desktop application that allows users to import, organize, view, and open digital music scores. The main feature of the application is MIDI gesture recognition, which allows users to assign MIDI note gestures to music scores and open them using a connected digital piano.

## Features

- Import PDF music scores
- Organize scores into categories
- View PDF scores in a score viewer window
- Turn pages in the score viewer
- Assign MIDI gestures to music scores
- Open music scores using MIDI gestures
- Store score metadata locally using JSON

## Requirements

- Windows computer
- Java JDK 25
- Eclipse IDE
- JavaFX SDK 25.0.3
- Apache PDFBox
- Gson
- MIDI-capable keyboard or digital piano for MIDI features

## How to Run in Eclipse

1. Download or clone this repository.
2. Open Eclipse.
3. Go to **File > Import > Existing Projects into Workspace**.
4. Select this project folder.
5. Make sure the required libraries are added to the project build path.
6. Configure the JavaFX VM arguments.
7. Run the main class.

## Library Setup Note

The JavaFX SDK, Apache PDFBox, and Gson libraries must be available on the computer running the project. If Eclipse shows build path errors after importing the project, remove the broken library paths and add the libraries again from their location on your computer.

JavaFX should be configured through the VM arguments shown below. PDFBox and Gson should be added to the project build path as external JAR files.

## JavaFX VM Arguments

Use the following VM arguments in the Eclipse Run Configuration:

```text
--module-path "PATH_TO_JAVAFX_SDK\lib" --add-modules javafx.controls,javafx.fxml,javafx.swing
```

Replace `PATH_TO_JAVAFX_SDK` with the JavaFX SDK path on your computer.

Example:

```text
--module-path "C:\Users\YourName\javafx\javafx-sdk-25.0.3\lib" --add-modules javafx.controls,javafx.fxml,javafx.swing
```

## Main Class

The main class for the application is:

```text
app.Main
```

## Project Structure

```text
src/
├── app/
├── manager/
├── midi/
├── model/
├── ui/
└── util/
```

## Notes

The application creates a local storage folder on the user's computer for imported scores and saved metadata. This allows imported music scores, categories, and assigned MIDI gestures to be saved and loaded again when the application is reopened.

MIDI gesture functionality requires a connected MIDI-capable keyboard or digital piano. The application was developed and tested using a Roland FP-E50 digital piano.

## Author

Oscar Avalos  
California State University Dominguez Hills  
CTC 492 Senior Project
