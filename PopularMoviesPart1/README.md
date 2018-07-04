# Popular Movies Part1

This app has been created during the Android Developer Nanodegree Program on Udacity. It's the first part of a project we were required to submit.

A user can navigate through the most popular movies or the top rated movies at the time, simply by selecting the corresponding mode by two buttons on the top of the main screen of the app. Also, he / she can select a movie and jump to a screen with more details about that movie (_DetailsActitivy_).

**Credits:** This product uses the TMDb API but is not endorsed or certified by TMDb.

## Implementation
I used a GridView for displaying the popular or top rated movies on the _MainActivity_. Each item, contains the movie's poster, release year and depending the sorting option selected the popularity number or rating average. The selection of the sorting option can easily be done by clicking the corresponding button of the two buttons at the top of the screen.

In order to fetch the data, I used AsyncTask in order to seperate this action from the main thread and execute it in the background.
In case of connectivity problem, the app does not crash, but displays the corresponding message informing the user about the problem. Also, in this case, there is a refresh button.

When the user select a specific movie, the DetailsActivity "opens". Then, there are details displayed about that movie, from the release date to which colection - series it belongs, or if it is a standalone movie and many more info.

## Libraries Used
- ButterKnife
- Gson
- Picasso


