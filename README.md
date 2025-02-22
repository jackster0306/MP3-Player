# Music Player

## Project Description
An Android application with the functionality of a simple music player, which allows users to play music stored on the devices SD card. The music will continue in the background while the user performs other tasks on their device.

The application consists of the following components:
- An activity that displays all the music tracks on the device that can be played.
  - Displays and allows the user to select music files to be played, found from /sdcard/Music.
- An activity that has the play, pause, and stop buttons for the music currently being played, as well as the progress of the current song as a bar, and the playback speed.
  - Allows the user to play, pause, or stop the currently playing song.
  - Displays the current progress bar of the song playing.
  - Displays the playback speed of the song playing.
- An activity that contains the settings to update the playback speed and background colour of the application.
  - Specify the value for the playback speed.
  - Choose a colour option for the background colour.
- A service to provide continued playback in the background.
- A notification to alert the user when music is currently playing.

### Developed Using
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Android Studio](https://img.shields.io/badge/android%20studio-346ac1?style=for-the-badge&logo=android%20studio&logoColor=white)


## How to Install & Run the Application

### Installation
- The project can be cloned & ran on an emulator or physical device.
- The apk file can be dragged onto an emulator or physical device.

### Setup
- You will need to grant permissions for the app to function correctly.
- Go to Settings > Apps > All Apps > Select the app.
- Go to Permissions > File and media
- Allow management of files > Allow

![](https://github.com/jackster0306/MP3-Player/blob/master/screenshots/screen_app_info.png)
![](https://github.com/jackster0306/MP3-Player/blob/master/screenshots/screen_app_permissions.png)
![](https://github.com/jackster0306/MP3-Player/blob/master/screenshots/screen_media_permissions.png)
![](https://github.com/jackster0306/MP3-Player/blob/master/screenshots/screen_accept_change.png)
![](https://github.com/jackster0306/MP3-Player/blob/master/screenshots/screen_permissions_allowed.png)

## The Project
The app consists of 3 screens:
- Music List
- Music Player
- Settings

![Music List](https://github.com/jackster0306/MP3-Player/blob/master/screenshots/screen_music_list.png)
![](https://github.com/jackster0306/MP3-Player/blob/master/screenshots/screen_music_player.png)
![](https://github.com/jackster0306/MP3-Player/blob/master/screenshots/screen_settings.png)


All screens are also responsive for landscape mode

![](https://github.com/jackster0306/MP3-Player/blob/master/screenshots/screen_music_list_landscape.png)
![](https://github.com/jackster0306/MP3-Player/blob/master/screenshots/screen_music_player_landscape.png)
![](https://github.com/jackster0306/MP3-Player/blob/master/screenshots/screen_settings_landscape.png)



When a song is playing, a notification is displayed

![](https://github.com/jackster0306/MP3-Player/blob/master/screenshots/screen_notification.png)


