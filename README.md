# AsteroidRadar

##### Note: This project constitutes Udacity's Android Kotlin Developer Nano-degree Program

### Description
Asteroid Radar is an app to view the asteroids detected by NASA that pass near Earth, you can view all the detected asteroids given a period of time with data such as the size, velocity, distance to earth and if they are potentially hazardous.

### The Project
The project follows a single activity, and Model-View-ViewModel architecture. 

The app consists of two fragments:
- MainFragment: shows the list of asteroids (can be sorted by today/weeks/hazardous filters)
- DetailFragment: shows detail of an asteroid instance when clicked from the MainFragment

### The Purpose
To satisfy the requirements of the project from Android Kotlin Developer Nanodegree at Udacity by employing 
various technical skills and best practises taught heretofore. This project demonstrates the following skills.

**Skills:** 
- Making network call to a RESTful API ([NASA API](https://api.nasa.gov/)), and consuming data
- Retrofit2, and Moshi converters 
- persisting data locally through the use of Room Persistance Library, and SQLite queries
- MVVM architecture
- Coil library to display images dynamically
- WorkManager API - to download data in the background once every 24 hours
- Databinding/Transformations
- Talkback facility 
- ListAdapters
- BindingAdapters



[Feedback from the moderator](https://review.udacity.com/#!/reviews/3517406)
