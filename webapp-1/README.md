# TrackOrJargh
Desarrollo de Aplicaciones Web - URJC (Móstoles) - 2017/2018

[![Go to video](https://i.ytimg.com/vi/LXvQL_JV4TI/maxresdefault.jpg)](https://youtu.be/LXvQL_JV4TI)

# 1st & 2nd PHASE

### 1.Authors:
- Óscar Soto Sánchez o.sotos@alumnos.urjc.es [OscarSotoSanchez](https://github.com/OscarSotoSanchez)
- Alfonso Casanova Muñoz a.casanovam@alumnos.urjc.es [fonyc](https://github.com/fonyc)
- Harender Singh Dhanoya hs.dhanoya@alumnos.urjc.es [HarenderS](https://github.com/HarenderS)
- Pedro Manuel Garrido Ortiz pm.garrido@alumnos.urjc.es [garrido18](https://github.com/garrido18)
- Jesús Elvira Piqueras j.elvira@alumnos.urjc.es [krate95](https://github.com/krate95)


### 2.Organization tools
[Github projects](https://github.com/krate95/TrackOrJargh/projects)


### 3.Database entities

| Entity                    | Description                                       |
| ------------------------- |:-------------------------------------------------:|
| Movies                    | Movie info                                        |
| Shows                     | Shwos info                                        |
| Books                     | Books info                                        |
| Comments_movies           | Joins comments and movies                         |
| Points_movies             | Joins points and movies                           |
| Comments_shows            | Joins comments and shows                          |
| Points_shows              | Joins points and shows                            |
| Comments_books            | Joins comments and books                          |
| Points_books              | Joins points and books                            |
| Gender                    | Genders of the movies, shows and books            |
| Actors                    | Shows and movies actors                           |    
| Users                     | Users of the webpage (normal,moderator and admin) |
| Lists                     | User's lists of following content                 |


### 4.Advanced features
Reccomended content.

Searching by tags and genders.


### 5.Graphics
Global rating of the content.


### 6.Advanced technology features

Verification email sent when an new user signs up in the site.

Pdf generation on user's demand with a list of the viewed content.

Facebook account login.

# 3rd PHASE

### 1. Screenshots
#### NON-REGISTERED USERS
###### INDEX:

![Index1](/TrackOrJargh/src/main/resources/static/img/documentation/index1.jpg)
This image carousel shows the last 3 items added to the web (book,show & film)

![Index2](/TrackOrJargh/src/main/resources/static/img/documentation/index2.jpg)
![Index3](/TrackOrJargh/src/main/resources/static/img/documentation/index3.jpg)
And this last 2 images also shows the index as you scroll down, revealing the Graphics which can show you all the content in the 
web ordered by genres and best valorations at the moment. 

###### FILMS:
![films](/TrackOrJargh/src/main/resources/static/img/documentation/peliculas.jpg)
Here you can search every film in the web, without any criteria or ordered by the most valorated ones. 

###### SHOWS:
![shows](/TrackOrJargh/src/main/resources/static/img/documentation/peliculas.jpg)
Exactly as the previous image, but with the shows

###### BOOKS:
![libros](/TrackOrJargh/src/main/resources/static/img/documentation/libros.jpg)
Same goes with books

###### SEARCH:
![search](/TrackOrJargh/src/main/resources/static/img/documentation/busqueda.jpg)
In this page, you can search every item in the web (books,films or shows) by genre or name. 

###### LOGIN:
![books](/TrackOrJargh/src/main/resources/static/img/documentation/login.jpg)
Here you can log in if you already have an account,register using Facebook, or you can create an account (with e-mail confirmation)

###### ERROR:
![error](/TrackOrJargh/src/main/resources/static/img/documentation/error.jpg)

#### REGISTERED USERS
###### INDEX:
![indexRegisteredUser](/TrackOrJargh/src/main/resources/static/img/documentation/indexLoggedUser.jpg)
As you can see, registered users has 2 new features: Administration and MyProfile

###### ADMINISTRATION:
![administration](/TrackOrJargh/src/main/resources/static/img/documentation/admin.jpg)
This page allows an admin to change content name or even delete it. Admins also can delete a registered account 

###### UPLOAD CONTENT:
![upload](/TrackOrJargh/src/main/resources/static/img/documentation/UploadContent.jpg)
Here an admin can upload any item (film,show or book) adding an image, genre, description... etc 

###### MY PROFILE:
![MyProfile](/TrackOrJargh/src/main/resources/static/img/documentation/miCuenta.jpg)
Here you can check you username, email, and all other users stuff

###### FILM/SHOWS/BOOK w/ New Feature(Add to List):
![addList](/TrackOrJargh/src/main/resources/static/img/documentation/addFilm.jpg)
As you can see, in every item of films/shows/books now appears a green button to add that concrete item to some of your lists

###### MY LISTS:
![MyLists](/TrackOrJargh/src/main/resources/static/img/documentation/myLists.jpg)
Here you can add a new list, delete a created list and also create a PDF with all your favourite lists 

### 2. Diagrams

#### NAVIGATION DIAGRAM
![navigationDiagram](/TrackOrJargh/src/main/resources/static/img/documentation/nav.jpg)
#### JAVA CLASSES DIAGRAM
![javaDiagram](/TrackOrJargh/src/main/resources/static/img/documentation/UMLFinalJava.png)
#### DB DIAGRAM
![dbDiagram](/TrackOrJargh/src/main/resources/static/img/documentation/dbDiagram.jpg)


[Docker usage](DOCKER.md)

[API](API.md)

# 5th PHASE

### 1. How to use SPA aplication with Angular

[Angular usage](/Angular/README.md)

### 2. Diagrams

#### ANGULAR CLASS AND TEMPLATES DIAGRAM
![angularUML](/TrackOrJargh/src/main/resources/static/img/documentation/angularUML.jpg)



