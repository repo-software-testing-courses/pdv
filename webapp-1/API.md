## RELEVANT INFORMATION
Every API search must be preceded by /api

## Pagination
If you have a method that can be paginated, at the end of the URL you must put this:

`?page=XXX&size=XXX`

Where `page` represents the page you want to show and `size` represents the number of items the page will have.

For example:

`/api/peliculas?page=0&size=5`

That will return a pagination from the first page, in groups of 5 items.

## Item Searches
Now we show the searches refered to book, film or show.



### OBTAIN EVERY BOOK/SHOW/FILM (PAGEABLE)

Obtain every book, show or film in the db

* ##### URL

  ``/api/peliculas``

  ``/api/libros``

  ``/api/series``

* ##### Method:

	`GET`

* ##### Success Response:

200 OK

```json
{
    "content": [
        {
            "url": "/pelicula/",
            "name": "Guardianes de la Galaxia 1",
            "synopsis": "Guardianes de la Galaxia Vol. 2 continúa las aventuras del equipo a medida que viajan cruzando\n los confines del cosmos. Los Guardianes tendrán que luchar para\n mantener su recién...",
            "image": "/img/Guardianes2.jpg",
            "trailer": "https://www.youtube.com/embed/12gvJgLE4us?rel=0&amp;controls=0&amp;showinfo=0",
            "year": 2017
        },
        {
            "url": "/pelicula/",
            "name": "Guardianes de la Galaxia 2",
            "synopsis": "Guardianes de la Galaxia Vol. 2 continúa las aventuras del equipo a medida que viajan cruzando\n los confines del cosmos. Los Guardianes tendrán que luchar para\n mantener su recién...",
            "image": "/img/Guardianes2.jpg",
            "trailer": "https://www.youtube.com/embed/12gvJgLE4us?rel=0&amp;controls=0&amp;showinfo=0",
            "year": 2017
        },
        {
            "url": "/pelicula/",
            "name": "Guardianes de la Galaxia 3",
            "synopsis": "Peter Quill, debe mantener una incomoda alianza con Gamora, Drax, Rocket y Groot para asegurar un artefacto que pone en peligro a toda la galaxia no caiga en las manos del temible Ronan el Acusador.",
            "image": "/img/Guardianes2.jpg",
            "trailer": "https://www.youtube.com/embed/dzj4P11Yr6E",
            "year": 2008
        }],
    "last": false,
    "totalElements": 22,
    "totalPages": 2,
    "size": 20,
    "number": 0,
    "sort": null,
    "first": true,
    "numberOfElements": 20
}
```

### OBTAIN A LIST OF FILMS/BOOKS/SHOWS 
It will return a list with all names of film, book or show without more information

* ##### URL

	 ``/api/peliculas/administracion ``
	 
	 ``/api/libros/administracion ``
	 
	 ``/api/series/administracion ``
	 
* ##### Method:

	`GET`
	
* ##### Success Response:

200 OK

```json
[
    {
        "url": "/serie/",
        "name": "The Big Bang Theory"
    },
    {
        "url": "/serie/",
        "name": "Narcos"
    },
    {
        "url": "/serie/",
        "name": "Friends"
    },
    {
        "url": "/serie/",
        "name": "Your lie in april"
    },
    {
        "url": "/serie/",
        "name": "Walking dead"
    },
    {
        "url": "/serie/",
        "name": "Vikingos"
    },
    {
        "url": "/serie/",
        "name": "One Tree Hill"
    },
    {
        "url": "/serie/",
        "name": "Once upon a time"
    },
    {
        "url": "/serie/",
        "name": "Altered Carbon"
    },
    {
        "url": "/serie/",
        "name": "Rick & Morty"
    },
    {
        "url": "/serie/",
        "name": "Dr.Who"
    },
    {
        "url": "/serie/",
        "name": "Belzebub"
    },
    {
        "url": "/serie/",
        "name": "Mahouka Koukou no Rettousei"
    },
    {
        "url": "/serie/",
        "name": "Juego de Tronos"
    },
    {
        "url": "/serie/",
        "name": "Daredevil"
    },
    {
        "url": "/serie/",
        "name": "Stranger Things"
    },
    {
        "url": "/serie/",
        "name": "Sherlock"
    },
    {
        "url": "/serie/",
        "name": "Fargo"
    },
    {
        "url": "/serie/",
        "name": "Los Simpson"
    },
    {
        "url": "/serie/",
        "name": "Sobrenatural"
    },
    {
        "url": "/serie/",
        "name": "The Flash"
    },
    {
        "url": "/serie/",
        "name": "Arrow"
    },
    {
        "url": "/serie/",
        "name": "Smallville"
    }
]

``` 
	
### OBTAIN A FILM/BOOK/SHOW IN PARTICULAR
Look for a particular film, show or book given a name

* ##### URL

	 ``/api/peliculas/{name} ``
	 
	 ``/api/libros/{name} ``
	 
	 ``/api/series/{name} ``

* ##### Method:

	`GET`

* ##### URL Params

	* Required:

		`name=[String]`

* ##### Success Response:

200 OK

```json
{
    "url": "/pelicula/",
    "name": "Guardianes de la Galaxia 1",
    "synopsis": "Guardianes de la Galaxia Vol. 2 continúa las aventuras del equipo a medida que viajan cruzando\n los confines del cosmos. Los Guardianes tendrán que luchar para\n mantener su recién...",
    "image": "/img/Guardianes2.jpg",
    "trailer": "https://www.youtube.com/embed/12gvJgLE4us?rel=0&amp;controls=0&amp;showinfo=0",
    "year": 2017
}

```

* ##### Error Response:

404 NOT FOUND

### ADD FILM/SHOW/BOOK
Adds a particular film/show/book to the db, given an admin.

* ##### URL

	`` /api/peliculas``
	
	`` /api/series``
	
	`` /api/libros``

* ##### Method:

	`POST`

* ##### URL Params

	* Required:

```json
    {
    "name": "Guardianes de la Galaxia ...",
    "synopsis": "Guardianes de la Galaxia ….",
    "image": "/img/GuardianesX.jpg",
    "trailer": "https://www.youtube.com/embed/12gvJgLE4us?rel=0&amp;controls=0&amp;showinfo=0",
    "year": 2017
}

```

* ##### Success Response:

200 OK

```json
{
    "id": 23,
    "url": "/pelicula/",
    "name": "Guardianes de la Galaxia...",
    "synopsis": "Guardianes de la Galaxia ….",
    "image": "/img/GuardianesX.jpg",
    "trailer": "https://www.youtube.com/embed/12gvJgLE4us?rel=0&amp;controls=0&amp;showinfo=0",
    "year": 2017,
    "firstInList": false
}

```

* ##### Error Response:
In case the name you put is the same of any item in the db

226 IM USED

### DELETE A FILM/BOOK/SHOW IN PARTICULAR
Look for a particular film, show or book given a name, and delete it. You must be an admin to do so.

* ##### URL

	 ``/api/peliculas/{name} ``
	 
	 ``/api/series/{name} ``
	 
	 ``/api/libros/{name} ``

* ##### Method:

	`DELETE`

* ##### URL Params

	* Required:

		`name=[String]`

* ##### Success Response:

200 OK

```json
{
    "id": 23,
    "url": "/pelicula/",
    "name": "Guardianes de la Galaxia...",
    "synopsis": "Guardianes de la Galaxia ….",
    "image": "/img/GuardianesX.jpg",
    "trailer": "https://www.youtube.com/embed/12gvJgLE4us?rel=0&amp;controls=0&amp;showinfo=0",
    "year": 2017,
    "firstInList": false
}

```

* ##### Error Response:
  In case the name you put does not exist in the db

404 NOT FOUND

### MODIFY A FILM/BOOK/SHOW IN PARTICULAR
Look for a particular film, show or book given a name, and delete it. You must be an admin to do so.

* ##### URL

	 ``/api/peliculas/{name} ``
	 
	 ``/api/series/{name} ``
	 
	 ``/api/libros/{name} ``

* ##### Method:

	`PUT`

* ##### URL Params

	* Required:

		`name=[String]`

* ##### Data Params
```json
{
    "name": "Guardianes de la Galaxia ...",
    "synopsis": "Guardianes de la Galaxia ….",
    "image": "/img/GuardianesX.jpg",
    "trailer": "https://www.youtube.com/embed/12gvJgLE4us?rel=0&amp;controls=0&amp;showinfo=0",
    "year": 2017
}
```


* ##### Success Response:

200 OK

```json

{
    "url": "/pelicula/",
    "name": "Guardianes de la Galaxia 2",
    "synopsis": "Guardianes de la Galaxia ….",
    "image": "/img/GuardianesX.jpg",
    "trailer": "https://www.youtube.com/embed/12gvJgLE4us?rel=0&amp;controls=0&amp;showinfo=0",
    "year": 2017
}

```

* ##### Error Response:
  In case the name you put does not exist in the db

404 NOT FOUND

### OBTAIN THE BEST RATED FILM/BOOK/SHOW
You obtain a list sorted by best ratings of the items you were looking for (books, films or shows)

* ##### URL

	``/api/peliculas/mejorvaloradas``

   	``/api/libros/mejorvalorados``

   	``/api/series/mejorvaloradas``


* ##### Method:

	`GET`


* ##### Success Response:

200 OK

```json
{
    "content": [
        {
            "url": "/pelicula/",
            "name": "Buscando a Nemo",
            "synopsis": "El pequeño Nemo, un pececillo hijo único que perdió a su madre antes de nacer, es muy querido y excesivamente protegido por su padre. Nemo ha sido pescado y sacadi de la gran barrera del arrecife australiano y ahora vive en una pequeña pecera en la oficina de un dentista de Sidney. El tímido padre de Nemo se embarcará en una peligrosa aventura donde conoce Dory. Juntos vane...",
            "image": "/img/films/buscandoAnemo.jpg",
            "trailer": "https://www.youtube.com/embed/wZdpNglLbt8",
            "year": 2003
        },
        {
            "url": "/pelicula/",
            "name": "Gladiator",
            "synopsis": "Máximo Décimo Meridio, un leal general hispano del ejército de la Antigua Roma, que es traicionado por Cómodo, el ambicioso hijo del emperador, quien ha asesinado a su padre y se ha hecho con el trono. Forzado a convertirse en esclavo, Máximo triunfa como gladiador mientras anhela vengar la muerte de su familia y su emperador",
            "image": "/img/films/gladiador.jpg",
            "trailer": "https://www.youtube.com/embed/s6v-bUY_wS8",
            "year": 2000
        }],
    "last": true,
    "totalElements": 20,
    "totalPages": 1,
    "size": 20,
    "number": 0,
    "sort": null,
    "first": true,
    "numberOfElements": 20
}

```

* ##### Error Response:

404 NOT FOUND

### OBTAIN THE LAST ADDED FILMS/BOOKS/SHOWS
Get a the last added indicated films,books or shows in URL

* ##### URL

	``/api/peliculas/ultimas/{num}``
	
	``/api/libros/ultimas/{num}``
	
	``/api/series/ultimas/{num}``


* ##### Method:

	`GET`

* ##### URL Params

	* Required:

		`num=[number]`

* ##### Success Response:
	* URL Example:
	``/api/peliculas/ultimas/3``

200 OK

```json
[
    {
        "id": 22,
        "directors": " Andrew Stanton, Lee Unkrich",
        "actors": " Albert Brooks, Ellen DeGeneres, Alexander Gould",
        "url": "/pelicula/",
        "name": "Buscando al memo",
        "synopsis": "El pequeño Alfonso, un pececillo hijo único que perdió a su madre antes de nacer, es muy querido y excesivamente protegido por su padre. Nemo ha sido pescado y sacadi de la gran barrera del arrecife australiano y ahora vive en una pequeña pecera en la oficina de un dentista de Sidney. El tímido padre de Nemo se embarcará en una peligrosa aventura donde conoce Dory. Juntos vane...",
        "image": "/img/films/buscandoAnemo.jpg",
        "trailer": "https://www.youtube.com/embed/wZdpNglLbt8",
        "year": 2003,
        "genders": [
            {
                "name": "Aventuras"
            },
            {
                "name": "Animacion"
            }
        ]
    },
    {
        "id": 21,
        "directors": "Christopher Nolan",
        "actors": " Matthew McConaughey, Anne Hathaway, Jessica Chastain",
        "url": "/pelicula/",
        "name": "Interestellar",
        "synopsis": "Un grupo de astronautas se lanza al espacio para buscar un futuro para la raza humana que parece perdido en 'Interstellar'. Ahora que la Tierra se acerca poco a poco al fin de su sus días debido a una más que preocupante escasez de comida por el mal estado de las tierras. Cooper deberá elegir entre quedarse con sus hijos o liderar esta expedición, que aprovechará los descubrimientos...",
        "image": "/img/films/interestellar.jpg",
        "trailer": "https://www.youtube.com/embed/UoSSbmD9vqc",
        "year": 2014,
        "genders": [
            {
                "name": "Thriller"
            },
            {
                "name": "Ciencia-ficcion"
            }
        ]
    },
    {
        "id": 20,
        "directors": " James Cameron",
        "actors": " Leonardo DiCaprio, Kate Winslet, Billy Zane",
        "url": "/pelicula/",
        "name": "Titanic",
        "synopsis": "Jack es un chico pobre de barrio que no trafica con drogas pues porque aun no se han inventado todavía que se cuela en un barco con un amigo el cual muere luego bum spoiler y que se enamora de una chica un pelin guarri porque le mola que la pinten en bolas y tiene un grave problema para compartir lo cual lleva a Jack a morir cuando se hunde el barco y no le comparte el tablon flotante",
        "image": "/img/films/titanic.jpg",
        "trailer": "https://www.youtube.com/embed/2e-eXJ6HgkQ",
        "year": 1997,
        "genders": [
            {
                "name": "Romantica"
            },
            {
                "name": "Historica"
            },
            {
                "name": "Drama"
            }
        ]
    }
]

```

### OBTAIN THE RELATED FILMS,BOOKS,SHOWS(PAGEABLE)
Get the films, books or shows related to a specific film, book or show indicated in URL by id.

* ##### URL

	``/api/peliculas/relacionadas/{id}``
	
	``/api/libros/relacionadas/{id}``
	
	``/api/series/relacionadas/{id}``


* ##### Method:

	`GET`

* ##### URL Params

	* Required:

		`id=[number]`

* ##### Success Response:
	* URL Example:
	``/api/series/relacionadas/1``

200 OK

```json
{
    "content": [
        {
            "id": 5,
            "directors": "Frank Darabont",
            "actors": " Andrew Lincoln, Norman Reedus, Melissa McBride",
            "url": "/serie/",
            "name": "Walking dead",
            "synopsis": "Una serie americana en torno al drama, el terror y la ciencia ficción que narra cómo un grupo de personajes intenta sobrevivir en un mundo devastado por el apocalípsis zombie. La trama arranca cuando el protagonista, el sheriff Rick Grimes, se despierta en un hospital tras estar varios…",
            "image": "/img/shows/theWalkingDead.jpg",
            "trailer": "https://www.youtube.com/embed/R1v0uFms68U ",
            "year": 2010,
            "genders": [
                {
                    "name": "Terror"
                },
                {
                    "name": "Aventuras"
                },
                {
                    "name": "Accion"
                },
                {
                    "name": "Fantasia"
                }
            ]
        },
        {
            "id": 6,
            "directors": "Michael Hirst",
            "actors": " Gustaf Skarsgård, Katheryn Winnick, Alexander Ludwig",
            "url": "/serie/",
            "name": "Vikingos",
            "synopsis": "La serie está basada en los relatos semilegendarios de Ragnar Lodbrok, reconocido como uno de los primeros reyes de Suecia y Dinamarca, durante el siglo VIII.",
            "image": "/img/shows/vkingos.jpg",
            "trailer": "https://www.youtube.com/embed/5aASH8HMJbo ",
            "year": 2013,
            "genders": [
                {
                    "name": "Historica"
                },
                {
                    "name": "Accion"
                },
                {
                    "name": "Aventuras"
                }
            ]
        }
    ],
    "last": false,
    "totalElements": 15,
    "totalPages": 8,
    "size": 2,
    "number": 0,
    "sort": null,
    "first": true,
    "numberOfElements": 2
    
}

```

### SPECIFIC CONTENT SEARCH (PAGEABLE)
Look for a particular film, show or book by genre or title

* ##### URL

	``/api/busqueda/{optionSearch}/peliculas/{name}/page``
	
	``/api/busqueda/{optionSearch}/series/{name}/page``
	
	``/api/busqueda/{optionSearch}/libros/{name}/page``


* ##### Method:

	`GET`

* ##### URL Params

	* Required:

		`name=[String]`
		`optionSearch["titulo","genero"]`

* ##### Success Response:
	* URL Example:
	``/api/busqueda/titulo/peliculas/venga/page``

200 OK

```json
{
    "content": [
        {
            "url": "/pelicula/",
            "name": "Star Wars La venganza de los Sith",
            "synopsis": "Último capítulo de la trilogía de precuelas de Star Wars, en el que Anakin Skywalker definitivamente se pasa al lado oscuro. En el Episodio III aparece el General Grievous, un ser implacable mitad-alien mitad-robot, el líder del ejército separatista Droid. Los Sith son los amos del lado oscuro de la Fuerza y los enemigos de los Jedi. Fueron prácticamente exterminados por los...",
            "image": "/img/films/starWars3.jpg",
            "trailer": "https://www.youtube.com/embed/kqkfjBKmWc4",
            "year": 2005
        },
        {
            "url": "/pelicula/",
            "name": "Los vengadores",
            "synopsis": "Cuando un enemigo inesperado surge como una gran amenaza para la seguridad mundial, Nick Fury, director de la Agencia SHIELD, decide reclutar a un equipo para salvar al mundo de un desastre casi seguro. Adaptación del cómic de Marvel \"Los Vengadores\", el legendario grupo de superhéroes formado por Ironman, Hulk, Thor y el Capitán América entre otros.",
            "image": "/img/films/losVengadores.jpg",
            "trailer": "https://www.youtube.com/embed/HQIiYqOVTWo",
            "year": 2012
        }
    ],
    "last": true,
    "totalElements": 2,
    "totalPages": 1,
    "size": 20,
    "number": 0,
    "sort": null,
    "first": true,
    "numberOfElements": 2
}

```

* ##### Error Response:
In case any params introduced were found in the db

404 NOT FOUND


## Graphic Searches
Every method related to the information collect for the graphics

### OBTAIN THE GRAPHIC'S INFORMATION
Api used to return an array with the best rated book/show/film information and not every field, just the neccesary, in order to show them in the graphics.   

* ##### URL

	``/api/peliculas/grafico``

	``/api/libros/grafico``

	``/api/series/grafico``


* ##### Method:

	`GET`

* ##### Success Response:

200 OK

```json
[
    {
        "name": "Sherlock Holmes: la liga de los pelirojos",
        "points": 5
    },
    {
        "name": "La luna de Plutón",
        "points": 5
    },
    {
        "name": "Los pilares de la Tierra",
        "points": 4.5
    },
    {
        "name": "El clan del oso cavernario: Los Hijos de la Tierra",
        "points": 4.5
    },
    {
        "name": "Cronicas de la dragonlance: la reina de la oscuridad",
        "points": 4.1
    },
    {
        "name": "Crepúsculo",
        "points": 4
    },
    {
        "name": "Arthas: El Ascenso de El Rey Exánime",
        "points": 4
    },
    {
        "name": "El bosque de los arboles muertos",
        "points": 3.9
    },
    {
        "name": "La biblioteca de los muertos",
        "points": 3.8
    },
    {
        "name": "El retorno del rey",
        "points": 3.5
    }
]

```

* ##### Error Response:

404 NOT FOUND

### OBTAIN THE GRAPHIC'S INFORMATION BY GENRE
Obtains the graphic's necessary information by genre

* ##### URL

	`/api/generos/grafico`

* ##### Success Response:

200 OK

```json
[
    {
        "name": "Aventuras",
        "numItems": 44
    },
    {
        "name": "Terror",
        "numItems": 11
    },
    {
        "name": "Romantica",
        "numItems": 17
    },
    {
        "name": "Comedia",
        "numItems": 12
    },
    {
        "name": "Thriller",
        "numItems": 16
    },
    {
        "name": "Accion",
        "numItems": 24
    },
    {
        "name": "Animacion",
        "numItems": 11
    },
    {
        "name": "Superheroes",
        "numItems": 14
    },
    {
        "name": "Drama",
        "numItems": 8
    },
    {
        "name": "Ciencia ficcion",
        "numItems": 13
    },
    {
        "name": "Historica",
        "numItems": 17
    },
    {
        "name": "Musical",
        "numItems": 6
    },
    {
        "name": "Fantasia",
        "numItems": 12
    }
]
```

* ##### Error Response:

404 NOT FOUND




## User Searches
Every Search related to Users

### OBTAIN A USER
Returns the user, given the name. Only an Admin can do this action.

* ##### URL

	 `/api/usuarios/{name}`

* ##### Method:

	`GET`

* ##### URL Params

	* Required:

		`name=[String]`

* ##### Success Response:
	* URL Example:
	``/api/usuario/oscar``

200 OK
```json
{
    "name": "oscar",
    "email": "oscarmola@gmail.com",
    "activatedUser": true,
    "roles": [
        "ROLE_USER"
    ]
}


```

* ##### Error Response:

404 NOT FOUND

### OBTAIN A LIST OF USERS
It will return a list with names of all  users without any additional information

You need to be administrator to get this information

* ##### URL

	 ``/api/usuarios/administracion ``
	 
	 
* ##### Method:

	`GET`
	
* ##### Success Response:

200 OK

```json
[
    {
        "name": "oscar"
    },
    {
        "name": "jesus"
    },
    {
        "name": "alfonso"
    }
]


``` 

* ##### Error Response:
If you are not admin

```json

{
    "timestamp": 1523812981828,
    "status": 403,
    "error": "Forbidden",
    "message": "Access is denied",
    "path": "/api/usuarios/administracion"   
}
```

### ADD USER
Adds an user in the db

* ##### URL

	 `/api/usuarios`

* ##### Method:

	`POST`

* ##### Data Params

```json
{
    "name": "pedro",
    "password": "1234",
    "email": "oscarsotosanchez@gmail.com",
    "activatedUser": false,
    "roles": [
        "ROLE_USER"
    ]
}
```		


* ##### Success Response:
200 OK

```json
{
    "id": 4,
    "name": "pedro",
    "password": "$2a$10$fhvz6XUF4up.Z7r5GrC9IunqHRXB/f2KlFpjnAfStXrx7D7jq2K96",
    "email": "oscarsotosanchez@gmail.com",
    "image": "/img/default-user.png",
    "activatedUser": false,
    "roles": [
        "[ROLE_USER]"
    ]
}
```

* ##### Error Response:
If an user with the same name already exists

226 IM USED

### DELETE USER
Deletes an specific user from the db. This action can only do it an admin.

* ##### URL

	 `/api/usuarios/{name}`

* ##### Method:

	`DELETE`

* ##### URL Params

	* Required:

		`name=[String]`

* ##### Success Response:

200 OK

```json
{
    "name": "oscar",
    "email": "oscarmola@gmail.com",
    "activatedUser": true,
    "roles": [
        "ROLE_USER"
    ]
}


```

* ##### Error Response:
In case the user is not found, which means, it does not exist an user with that name.

404 NOT FOUND

### EDIT USER
Changes the user's parameters given that user's name. This can only do it an admin.

* ##### URL

	 `/api/usuarios/{name}`

* ##### Method:

	`PUT`

* ##### URL Params

	* Required:

		`name=[String]`

* ##### Data Params

```json
{
    "name": "oscar",
    "password": "12345",
    "email": "oscarsotosanchez@gmail.com",
    "activatedUser": true,
    "roles": [
        "ROLE_USER",
        "ROLE_ADMIN"
    ]
}
```

* ##### Success Response:
	* URL Example:
	``/api/usuarios/oscar``
200 OK

```json
{
    "name": "oscar",
    "email": "oscarsotosanchez@gmail.com",
    "activatedUser": true,
    "roles": [
        "ROLE_USER",
        "ROLE_ADMIN"
    ]
}
```

* ##### Error Response:
In case does not exist a user with that name.

404 NOT FOUND

### ACTIVATE USER
You can activate user from api

* ##### URL

	 ``/api/activar/{name}``

* ##### Method:

	`GET`

* ##### URL Params

	* Required:

		`name=[String]`

* ##### Success Response:
	* URL Example:
	``/api/activar/pepe`

200 OK

```json
{
    "activatedUser": true
}

```


### CHECK USER/EMAIL
Checks if exists a user given a name, returning true or false depending if exists or not or the email is being used or not.

* ##### URL

	 ``/api/comprobarusuario/{name}``

	 ``/api/comprobarusuario/{email}``

* ##### Method:

	`GET`

* ##### URL Params

	* Required:

		`name=[String]` / `email=[String]`

* ##### Success Response:
	* URL Example:
	``/api/comprobarusuario/oscar`

200 OK

`true`

* ##### Error Response:

404 NOT FOUND

### OBTAIN USER'S LISTS
Returns the lists of a logged user. Its necessary being an user to do this action.

* ##### URL

	 `/api/listas`

* ##### Method:

	`GET`

* ##### Success Response:

200 OK

```json
[
    {
        "id": 2,
        "name": "roto"
    },
    {
        "id": 3,
        "name": "hola"
    }
]
```

* ##### Error Response:

404 NOT FOUND

### OBTAIN ALL LISTS AND HIS CONTENT
Get all lists and the content of this lists

You need to put your authorization 

* ##### URL

	 `/api/listas/contenido`

* ##### Method:

	`GET`

* ##### Success Response:

200 OK

```json
[
    {
        "name": "new",
        "printList": [
            {
                "url": "/pelicula/",
                "name": "Guardianes de la Galaxia 2"
            },
            {
                "url": "/pelicula/",
                "name": "Guardianes de la Galaxia 3"
            }
        ]
    },
    {
        "name": "MOLA",
        "printList": [
            {
                "url": "/serie/",
                "name": "Narcos"
            }
        ]
    }
]
```

### ADD A CREATED LIST MADE BY THE USER
Creates an empty list with a specific name for the logged user

* ##### URL

	 `/api/listas`

* ##### Method:

	`POST`


* ##### Success Response:

200 OK

```json
[
    {
        "id": 4,
        "name": "Lista nueva"
    }
]

```

* ##### Error Response:
In case you try to create a list with the same name

226 IM USED

### ADD CONTENT TO A LIST
Adds a show/film/book in a list previously created. It returns tru or false depending if it adds or not the item. It is necessary to be a user to do so.  

* ##### URL

	 `/api/listas`

* ##### Method:

	`PUT`

* ##### Success Response:

200 OK

`true`

* ##### Error Response:
In case the name of the list is not found

404 NOT FOUND

### DELETE USER'S LIST
Delete the list given a name's list, wich belongs to a user. Returns true or false if the deleting was succesful or not. It is necessary to be an user to do so.

* ##### URL

	 `/api/listas/{nameList}`

* ##### Method:

	`DELETE`

* ##### URL Params

	* Required:

		`nameList=[String] `

* ##### Success Response:

200 OK

`true`

* ##### Error Response:

404 NOT FOUND

### DELETE USER'S LIST CONTENT
Delete an item from a specific list. Returns true or false depending the content was deleted successfuly or not. You must be a user to do so.

* ##### URL

	 `/api/listas/{nameList}/{typeContent}/{nameContent}
`

* ##### Method:

	`DELETE`

* ##### URL Params

	* Required:

		`nameList=[String]`  List name you want to delete some content  
		`typeContent=[String]` Type of content you want to delete {pelicula, serie, libro}
		`nameContent={String}` Item name you want to delete


* ##### Success Response:

200 OK

`true`

* ##### Error Response:
There is no content in the list you want to substract something

404 NOT FOUND

## Comment Searches

### SHOW COMMENTS

* ##### URL

	``/api/peliculas/comentarios/{name}``
	
	``/api/series/comentarios/{name}``
	
	``/api/libros/comentarios/{name}``

* ##### Method:

	`GET`

* ##### URL Params

	* Required:

		`name=[String] `


* ##### Success Response:

200 OK

```json
[
    {
        "id": 1,
        "user": {
            "name": "oscar",
            "email": "oscarmola@gmail.com",
            "activatedUser": true,
            "roles": [
                "ROLE_USER"
            ]
        },
        "comment": "Esta pelicula es muy buena"
    }
]
```

* ##### Error Response:
There is no such item or comment

404 NOT FOUND

### ADD COMMENT
Look for a particular film, show or book given a name

* ##### URL

	 ``/api/peliculas/comentarios/{name}``
	 
	 ``/api/series/comentarios/{name}``
	 
	 ``/api/libros/comentarios/{name}``

* ##### Method:

	`POST`

* ##### URL Params

	* Required:

		`name=[String] `

* ##### Data Params:

```json
{
  "comment": "Esta pelicula es muy buena"
}
´´´

* ##### Success Response:

200 OK

```json
{
    "id": 26,
    "comment": "Esta pelicula es muy buena"
}

```

* ##### Error Response:
The item you put does not exist

404 NOT FOUND

### DELETE COMMENT
Deletes the comment given an id. You must be admin or mod to do it.

* ##### URL

	 ``/api/peliculas/comentarios/{id}``
	 
	 ``/api/series/comentarios/{id}``
	 
	 ``/api/libros/comentarios/{id}``

* ##### Method:

	`DELETE`

* ##### URL Params

	* Required:

		`name=[Long]`

* ##### Success Response:

200 OK

```json
{
    "id": 1,
    "user": {
        "name": "oscar",
        "email": "oscarmola@gmail.com",
        "activatedUser": true,
        "roles": [
            "ROLE_USER"
        ]
    },
    "comment": "Esta pelicula es muy buena"
}

```

* ##### Error Response:
There is no comment with such id

404 NOT FOUND

## Rate Searches
Searches related with the rate in every item (book, shows or films)

### OBTAIN RATE
Obtains the rate of a specific book/show/film

* ##### URL

	 ``/api/peliculas/puntos/{name}``
	 
	 ``/api/series/puntos/{name}``
	 
	 ``/api/libros/puntos/{name}``

* ##### Method:

	`GET`

* ##### URL Params

	* Required:

		`name=[String]`

* ##### Success Response:

200 OK

```json
[
    {
        "id": 1,
        "film": {
            "name": "Guardianes de la Galaxia 2"
        },
        "user": {
            "name": "oscar"
        },
        "points": 5
    },
    {
        "id": 2,
        "film": {
            "name": "Guardianes de la Galaxia 2"
        },
        "user": {
            "name": "oscar"
        },
        "points": 2.3
    },
    {
        "id": 3,
        "film": {
            "name": "Guardianes de la Galaxia 2"
        },
        "user": {
            "name": "jesus"
        },
        "points": 2.3
    },
    {
        "id": 4,
        "film": {
            "name": "Guardianes de la Galaxia 2"
        },
        "user": {
            "name": "alfonso"
        },
        "points": 5
    }
]

```

* ##### Error Response:
There is no such an item with that name

404 NOT FOUND

### ADD RATE
Adds a rate to an item. If you have already rated this api it will modify the rate instead.

* ##### URL

	``/api/peliculas/puntos/{name}``
	
	``/api/series/puntos/{name}``
	
	``/api/libros/puntos/{name}``
	
* ##### Method:

	`POST`

* ##### URL Params

	* Required:

		`name=[String]`

* ##### Data Params:
{    
        "points": 5
}

* ##### Success Response:
200 OK

```json
{
    "id": 3,
    "film": {
        "name": "Guardianes de la Galaxia 2"
    },
    "user": {
        "name": "jesus"
    },
    "points": 5
}

```

* ##### Error Response:
There is no such item with that name

404 NOT FOUND


## Images

### Upload images
Upload a imagen to the db and returns the URL

* ##### URL

	 `/api/subirimagen`

* ##### Method:

	`POST`

* ##### Data Params:

	`key image value upload file`

    
* ##### Success Response:

200 OK

```json

/imagen/image-Captura.jpg

```

* ##### Error Response:

404 NOT FOUND
