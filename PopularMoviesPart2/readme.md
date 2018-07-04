# Popular Movies Part2

This app has been created during the Android Developer Nanodegree Program on Udacity. It's the second part of a project we were required to submit.

A user can navigate through the most popular movies or the top rated movies at the time, simply by selecting the corresponding item on the navigation drawer menu. Also, he / she can select a movie and jump to a screen with more details about that movie (_DetailsActivity_). In addition the user can now add / remove movies from a list of his favorite
movies. New additions are the trailers and the reviews displayed on details activity. Lastly, the user can share the first trailer of the opened details movie.

**Credits:** This product uses the TMDb API but is not endorsed or certified by TMDb.

## Implementation
- **Main**

    I used a RecyclerView with 2 columns (4 on landscape mode) for displaying the popular, top rated or favorite movies on the _MainActivity_. Each item, contains the movie's poster, release year and depending the sorting option selected the popularity number or rating average. The selection of the sorting option can easily be done by clicking the corresponding item on the navigation drawer menu.

    In order to fetch the data, I used AsyncTaskLoader in order to separate this action from the main thread and execute it in the background. If it is popular or top rated then the data are loaded using the TMDB api. Otherwise, using the db, and that is why favorites are also available while offline. **Note** that on favorite's details I decided to exclude reviews, since if a user decide to add a movie to his favorite he doesn't need those reviews. He already made up his mind about that movie, and storing those reviews wouldn't be really useful.

    Since I used Navigation Drawer, I also used fragments. I decided to create on base fragment, the MovieFragment, that is the super class of the other tree (PopularFragment, TopRatedFragment and FavoriteFragment). Using transactions each time the correct one is being displayed.
    Note that, each time a new fragment is replacing the old one, we destroy the unrelated with the new fragment loader.

    In case of connectivity problem, the app does not crash, but displays the corresponding message informing the user about the problem. Also, in this case, there is a refresh button. Also, when rotating or pressing home button or resume from wake up, the state is as close to the previous as possible if not identical, and thus because of using loader and onSaveInstance.

- **Details**

    When the user select a specific movie, the DetailsActivity "opens". Then, there are details displayed about that movie, from the release date to which collection - series it belongs, or if it is a standalone movie and many more info. It also displays trailers and reviews. In case any of that is missing the whole CardView, normally containing this info, is set to Gone visibility. In the case of trailers, note that we only display the videos provided by the API that are on Youtube and its type is Trailer.

    **Note** if the user press the share button or the play on a trailer thumbnail, but there is no internet connection, then nothing happens, and a toast is being shown in order to inform the user about the issue. Also if the opened movie, doesn't have any valid video available, then if the user press the share button, a corresponding message is being shown using a toast again. 

    In case of landscape mode, I decided to set the AppBarLayout collapsed by default, cause otherwise, the AppBarLayout would fill up all the screen. Also, I decided to use a different xml layout, to simple rearrange the different CardViews in order to look better on landscape.

## Libraries Used
- ButterKnife
- Gson
- Picasso
- Carouselview