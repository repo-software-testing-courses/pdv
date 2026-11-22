package com.trackorjargh.database;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trackorjargh.javaclass.Book;
import com.trackorjargh.javaclass.CommentBook;
import com.trackorjargh.javaclass.CommentFilm;
import com.trackorjargh.javaclass.CommentShow;
import com.trackorjargh.javaclass.Film;
import com.trackorjargh.javaclass.Gender;
import com.trackorjargh.javaclass.PointBook;
import com.trackorjargh.javaclass.PointFilm;
import com.trackorjargh.javaclass.PointShow;
import com.trackorjargh.javaclass.Shows;
import com.trackorjargh.javaclass.User;
import com.trackorjargh.javarepository.BookRepository;
import com.trackorjargh.javarepository.CommentBookRepository;
import com.trackorjargh.javarepository.CommentFilmRepository;
import com.trackorjargh.javarepository.CommentShowRepository;
import com.trackorjargh.javarepository.FilmRepository;
import com.trackorjargh.javarepository.GenderRepository;
import com.trackorjargh.javarepository.PointBookRepository;
import com.trackorjargh.javarepository.PointFilmRepository;
import com.trackorjargh.javarepository.PointShowRepository;
import com.trackorjargh.javarepository.ShowRepository;
import com.trackorjargh.javarepository.UserRepository;

@Component
public class DatabaseInformationLoader {

	private static final Logger log = LoggerFactory.getLogger(DatabaseInformationLoader.class);

	@Autowired
	private FilmRepository filmRepository;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private GenderRepository genderRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ShowRepository showRepository;
	@Autowired
	private CommentFilmRepository commentFilmRepository;
	@Autowired
	private CommentBookRepository commentBookRepository;
	@Autowired
	private CommentShowRepository commentShowRepository;
	@Autowired
	private PointFilmRepository pointFilmRepository;
	@Autowired
	private PointBookRepository pointBookRepository;
	@Autowired
	private PointShowRepository pointShowRepository;

	@PostConstruct
	private void init() {
		if (filmRepository.findById(1L) == null) {
			log.info("Loading data in BBDD");
			loadData();
		} else {
			log.info("The data are already loaded in the bbdd");
		}
	}
	
	private void loadData() {
		// Test Data User
		User u1 = new User("oscar", "1234", "oscarmola@gmail.com", "/img/default-user.png", true, "ROLE_USER");
		userRepository.save(u1);
		User u2 = new User("jesus", "1234", "jesusmola@gmail.com", "/img/userFoto.jpg", true, "ROLE_USER",
				"ROLE_MODERATOR", "ROLE_ADMIN");
		userRepository.save(u2);
		User u3 = new User("alfonso", "1234", "fonycas@hotmail.com", "/img/Alfonso_Casanova.jpg", true, "ROLE_USER",
				"ROLE_MODERATOR");
		userRepository.save(u3);

		// Test Data Gender
		Gender g1 = new Gender("Aventuras");
		genderRepository.save(g1);
		Gender g2 = new Gender("Terror");
		genderRepository.save(g2);
		Gender g3 = new Gender("Romantica");
		genderRepository.save(g3);
		Gender g4 = new Gender("Comedia");
		genderRepository.save(g4);
		Gender g5 = new Gender("Thriller");
		genderRepository.save(g5);
		Gender g6 = new Gender("Accion");
		genderRepository.save(g6);
		Gender g7 = new Gender("Animacion");
		genderRepository.save(g7);
		Gender g8 = new Gender("Superheroes");
		genderRepository.save(g8);
		Gender g9 = new Gender("Drama");
		genderRepository.save(g9);
		Gender g10 = new Gender("Ciencia-ficcion");
		genderRepository.save(g10);
		Gender g11 = new Gender("Historica");
		genderRepository.save(g11);
		Gender g12 = new Gender("Musical");
		genderRepository.save(g12);
		Gender g13 = new Gender("Fantasia");
		genderRepository.save(g13);

		// Test Data Film
		Film f0 = new Film("Guardianes de la Galaxia 1",
				"Chris Pratt, Zoe Saldana, Bradley Cooper, Dave Bautista, Vin Diesel, Michael Rooker, Karen Gillan, Pom Klementieff, Elizabeth Debicki, Chris Sullivan, Sean Gunn, Sylvester Stallone, Kurt Russell",
				"James Gunn",
				"Guardianes de la Galaxia Vol. 2 continúa las aventuras del equipo a medida que viajan cruzando\n los confines del cosmos. Los Guardianes tendrán que luchar para\n mantener su recién...",
				"/img/Guardianes2.jpg", "https://www.youtube.com/embed/12gvJgLE4us?rel=0&amp;controls=0&amp;showinfo=0",
				2017);
		f0.getGenders().add(g1);
		filmRepository.save(f0);

		Film f2 = new Film("Guardianes de la Galaxia 2",
				"Chris Pratt, Zoe Saldana, Bradley Cooper, Dave Bautista, Vin Diesel, Michael Rooker, Karen Gillan, Djimon Hounsou, John C. Reilly, Glenn Close, Lee Pace, Benicio del Toro",
				"James Gunn",
				"Peter Quill, debe mantener una incomoda alianza con Gamora, Drax, Rocket y Groot para asegurar un artefacto que pone en peligro a toda la galaxia no caiga en las manos del temible Ronan el Acusador.",
				"/img/Guardianes2.jpg", "https://www.youtube.com/embed/dzj4P11Yr6E", 2008);

		f2.getGenders().add(g1);
		f2.getGenders().add(g8);
		f2.getGenders().add(g4);
		f2.getGenders().add(g6);
		f2.getGenders().add(g10);
		filmRepository.save(f2);

		Film f3 = new Film("Insidious", "Patrick Wilson, Rose Byrne, Barbara Hershey", "James Wan",
				"La historia se centra en una pareja cuyo hijo, Óscar, inexplicablemente entra en estado de coma y se convierte en un recipiente para fantasmas en una dimensión astral que quiere habitar su cuerpo",
				"/img/films/insidious.jpg", "https://www.youtube.com/embed/FRW3K0LlsD0", 2010);

		f3.getGenders().add(g2);
		f3.getGenders().add(g5);
		filmRepository.save(f3);

		Film f4 = new Film("Mi gran boda Griega",
				"Nia Vardalos, Christina Eleusiniotis, John Corbett, Michael Constantine, Lainie Kazan, Andrea Martin",
				" Joel Zwick",
				"La trama gira alrededor de Jesús Portokalos, una mujer de ascendencia griego-vallecana, que se enamora de un chico fuenlabreño llamado Óscar Miller. En el transcurso de la película, los protagonistas tienen que luchar por vencer las diferencias culturales",
				"/img/films/miGranBodaGriega.jpg", "https://www.youtube.com/embed/nnVMh3uDwwE", 2002);

		f4.getGenders().add(g3);
		f4.getGenders().add(g4);
		filmRepository.save(f4);

		Film f5 = new Film("Your name",
				"Ryunosuke Kamiki, Mone Kamishiraishi, Masami Nagasawa,Etsuko Ichihara, Ryo Narita, Aoi Yūki",
				"Makoto Shinkai",
				"Aki y Mitsuha descubren un día que durante el sueño sus cuerpos se intercambian, y comienzan a comunicarse por medio de notas. A medida que consiguen superar torpemente un reto tras otro, se va creando entre los dos un vínculo que poco a poco se convierte en algo más romántico",
				"/img/films/yourName.jpg", "https://www.youtube.com/embed/eHS8cPgzLsI", 2017);

		f5.getGenders().add(g3);
		f5.getGenders().add(g5);
		f5.getGenders().add(g7);
		f5.getGenders().add(g12);
		filmRepository.save(f5);

		Film f6 = new Film("Gladiator",
				"Russell Crowe, Joaquin Phoenix, Connie Nielsen, Ralf Möller, Oliver Reed, Djimon Hounsou, Derek Jacobi, John Shrapnel y Richard Harris",
				"Ridley Scott",
				"Máximo Décimo Meridio, un leal general hispano del ejército de la Antigua Roma, que es traicionado por Cómodo, el ambicioso hijo del emperador, quien ha asesinado a su padre y se ha hecho con el trono. Forzado a convertirse en esclavo, Máximo triunfa como gladiador mientras anhela vengar la muerte de su familia y su emperador",
				"/img/films/gladiador.jpg", "https://www.youtube.com/embed/s6v-bUY_wS8", 2000);

		f6.getGenders().add(g6);
		f6.getGenders().add(g11);
		filmRepository.save(f6);

		Film f7 = new Film("Indiana Johnes: El templo maldito",
				"Harrison Ford, Kate Capshaw,Amrish Puri,Jonathan Ke Quan, Roshan Seth, Philip Stone, Roy Chiao",
				"Steven Spielberg",
				" Indiana Jones llega accidentalmente al norte de India, donde los desesperados habitantes de un pequeño pueblo le piden ayuda para encontrar una piedra mágica que les han robado",
				"/img/films/indianJohns.jpg", "https://www.youtube.com/embed/jFqK5xyPQQc", 1984);

		f7.getGenders().add(g1);
		f7.getGenders().add(g6);
		filmRepository.save(f7);

		Film f8 = new Film("Jesucristo Superstar",
				"Jeff Fenholt, Ben Vereen, Yvonne Elliman, Barry Dennen, Bob Bingham, Phil Jethro, Michael Jason, Dennis Buckleys, Paul Ainsley, Ted Neeley",
				"Robert Stigwood",
				"Famoso musical de rock, basado en una exitosa obra de Broadway, que relata la historia de Jesús de Nazaret por medio de canciones. Se vendieron millones de discos de su banda sonora en todo el mundo.",
				"/img/films/jesucristo.jpg", "https://www.youtube.com/embed/edTECo3gCd4", 1970);

		f8.getGenders().add(g11);
		f8.getGenders().add(g12);
		filmRepository.save(f8);

		Film f9 = new Film("Star Wars La venganza de los Sith", "Hayden Christensen, Natalie Portman, Ewan McGregor",
				"George Lucas",
				"Último capítulo de la trilogía de precuelas de Star Wars, en el que Anakin Skywalker definitivamente se pasa al lado oscuro. En el Episodio III aparece el General Grievous, un ser implacable mitad-alien mitad-robot, el líder del ejército separatista Droid. Los Sith son los amos del lado oscuro de la Fuerza y los enemigos de los Jedi. Fueron prácticamente exterminados por los...",
				"/img/films/starWars3.jpg", "https://www.youtube.com/embed/kqkfjBKmWc4", 2005);

		f9.getGenders().add(g1);
		f9.getGenders().add(g6);
		f9.getGenders().add(g10);
		filmRepository.save(f9);

		Film f10 = new Film("El exorcista", " Ellen Burstyn, Max von Sydow, Linda Blair", "William Friedkin",
				"Adaptación de la novela de William Peter Blatty que se inspiró en un exorcismo real ocurrido en Washington en 1949. Regan, una niña de doce años, es víctima de fenómenos paranormales como la levitación o la manifestación de una fuerza sobrehumana. Su madre, aterrorizada, tras someter a su hija a múltiples análisis médicos que no ofrecen ningún resultado, acude a un sacerdote ...",
				"/img/films/theExorcist.jpg", "https://www.youtube.com/embed/HTPg9f3Win0 ", 1973);

		f10.getGenders().add(g2);
		f10.getGenders().add(g5);
		filmRepository.save(f10);

		Film f11 = new Film("La princesa de Mononoke", "Yôji Matsuda, Yuriko Ishida, Yûko Tanaka", "Hayao Miyazaki",
				"Con el fin de curar la herida que le ha causado un jabalí enloquecido, el joven Ashitaka sale en busca del dios Ciervo, pues sólo él puede liberarlo del sortilegio. A lo largo de su periplo descubre cómo los animales del bosque luchan contra hombres que están dispuestos a destruir la Naturaleza.",
				"/img/films/laPrincesa.jpg", "https://www.youtube.com/embed/WVjVkpk7wKg", 1997);

		f11.getGenders().add(g1);
		f11.getGenders().add(g6);
		f11.getGenders().add(g7);
		f11.getGenders().add(g10);
		filmRepository.save(f11);

		Film f12 = new Film("Scooby-doo y el misterio de Wrestlemania", " Frank Welker, Mindy Cohn, Grey DeLisle",
				"Brandon Vietti",
				"Scooby y sus amigos ganan unas entradas para el show de Wrestlemania, pero allí son sorprendidos con la aparición de un oso fantasma.",
				"/img/films/scoobyDoo.jpg", "https://www.youtube.com/embed/VD88HIKoFzk", 2014);

		f12.getGenders().add(g7);
		f12.getGenders().add(g1);
		f12.getGenders().add(g13);
		filmRepository.save(f12);

		Film f13 = new Film("Los vengadores", "Robert Downey Jr., Chris Evans, Scarlett Johansson", "Joss Whedon",
				"Cuando un enemigo inesperado surge como una gran amenaza para la seguridad mundial, Nick Fury, director de la Agencia SHIELD, decide reclutar a un equipo para salvar al mundo de un desastre casi seguro. Adaptación del cómic de Marvel \"Los Vengadores\", el legendario grupo de superhéroes formado por Ironman, Hulk, Thor y el Capitán América entre otros.",
				"/img/films/losVengadores.jpg", "https://www.youtube.com/embed/HQIiYqOVTWo", 2012);

		f13.getGenders().add(g1);
		f13.getGenders().add(g8);
		f13.getGenders().add(g4);
		f13.getGenders().add(g6);
		f13.getGenders().add(g10);
		filmRepository.save(f13);

		Film f14 = new Film("Capitan America", "Chris Evans, Hugo Weaving, Samuel L. Jackson", "Joe Johnston",
				"Tras los devastadores acontecimientos acaecidos en Nueva York con Los Vengadores, Steve Rogers, alias el Capitán América, vive tranquilamente en Washington D.C. intentando adaptarse al mundo moderno. Pero cuando atacan a un colega de S.H.I.E.L.D., Steve se ve envuelto en una trama de intrigas que representa una amenaza para el mundo. Se unirá entonces a la Viuda Negra para ",
				"/img/films/capitanAmerica.jpg", "https://www.youtube.com/embed/B5nTjpO4LZ0", 2011);

		f14.getGenders().add(g1);
		f14.getGenders().add(g8);
		f14.getGenders().add(g4);
		f14.getGenders().add(g6);
		filmRepository.save(f14);

		Film f15 = new Film("Doctor Strange", "Benedict Cumberbatch, Chiwetel Ejiofor, Rachel McAdams",
				"Scott Derrickson",
				"La vida del Dr. Stephen Strange cambia para siempre tras un accidente automovilístico que le deja muy malheridas sus manos. Cuando la medicina tradicional falla, se ve obligado a buscar esperanza y una cura en un lugar impensable: una comunidad aislada en Nepal llamada Kamar-Taj. Rápidamente descubre que éste no es sólo un centro de recuperación, sino también la primera línea...",
				"/img/films/drstrange.jpg", "https://www.youtube.com/embed/DYyMsYgZDJM", 2016);

		f15.getGenders().add(g1);
		f15.getGenders().add(g8);
		f15.getGenders().add(g4);
		f15.getGenders().add(g6);
		f15.getGenders().add(g10);
		filmRepository.save(f15);

		Film f16 = new Film("El padrino", " Marlon Brando, Al Pacino, James Caan", "Francis Ford Coppola",
				"Finales de los años 40 en Nueva York. Vito Corleone es, en la jerga del crimen organizado, un padrino o don, el cabecilla de la Mafia. Michael, un libre pensador que desafió a su padre al alistarse en el cuerpo de Marines en la Segunda Guerra Mundial, regresa como capitán y héroe de guerra",
				"/img/films/elPadrino.jpg", "https://www.youtube.com/embed/sY1S34973zA", 1972);

		f16.getGenders().add(g11);
		f16.getGenders().add(g9);
		filmRepository.save(f16);

		Film f17 = new Film("La naranja mecánica", " Malcolm McDowell, Patrick Magee, Michael Bates",
				" Stanley Kubrick",
				"Las aventuras de un joven cuyos principales intereses son la violación, la ultra-violencia, y Beethoven. Alex es el jefe de la banda de los drugos, que tienen sus propios métodos para divertirse y descargar su tremenda agresividad, ya sea dando una paliza a un mendigo o entrando en una casa para destrozar lo que hay dentro y violar a la mujer que viva allí. Sin embargo...",
				"/img/films/laNaranjaMecanica.jpg", "https://www.youtube.com/embed/xHFPi_3kc1U", 1971);

		f17.getGenders().add(g11);
		f17.getGenders().add(g6);
		filmRepository.save(f17);

		Film f18 = new Film("El rey león", "Matthew Broderick, Jeremy Irons, James Earl Jones",
				"Roger Allers, Rob Minkoff",
				"Óscar es un león, hijo del rey de la selva Mufasa. Cierto día, jugando al bascket con amigos unos tipos del barrio le metieron en un lio, y su padre le decia una y otra vez con tu tio y con tu tia te irás a Bel-Air",
				"/img/films/reyLeon.jpg", "https://www.youtube.com/embed/4sj1MT05lAA", 1994);

		f18.getGenders().add(g1);
		f18.getGenders().add(g7);
		filmRepository.save(f18);

		Film f19 = new Film("Titanic", " Leonardo DiCaprio, Kate Winslet, Billy Zane", " James Cameron",
				"Jack es un chico pobre de barrio que no trafica con drogas pues porque aun no se han inventado todavía que se cuela en un barco con un amigo el cual muere luego bum spoiler y que se enamora de una chica un pelin guarri porque le mola que la pinten en bolas y tiene un grave problema para compartir lo cual lleva a Jack a morir cuando se hunde el barco y no le comparte el tablon flotante",
				"/img/films/titanic.jpg", "https://www.youtube.com/embed/2e-eXJ6HgkQ", 1997);

		f19.getGenders().add(g3);
		f19.getGenders().add(g11);
		f19.getGenders().add(g9);
		filmRepository.save(f19);

		Film f20 = new Film("Interestellar", " Matthew McConaughey, Anne Hathaway, Jessica Chastain",
				"Christopher Nolan",
				"Un grupo de astronautas se lanza al espacio para buscar un futuro para la raza humana que parece perdido en 'Interstellar'. Ahora que la Tierra se acerca poco a poco al fin de su sus días debido a una más que preocupante escasez de comida por el mal estado de las tierras. Cooper deberá elegir entre quedarse con sus hijos o liderar esta expedición, que aprovechará los descubrimientos...",
				"/img/films/interestellar.jpg", "https://www.youtube.com/embed/UoSSbmD9vqc", 2014);

		f20.getGenders().add(g5);
		f20.getGenders().add(g10);
		filmRepository.save(f20);

		Film f21 = new Film("Buscando a nemo", " Albert Brooks, Ellen DeGeneres, Alexander Gould",
				" Andrew Stanton, Lee Unkrich",
				"El pequeño Alfonso, un pececillo hijo único que perdió a su madre antes de nacer, es muy querido y excesivamente protegido por su padre. Nemo ha sido pescado y sacadi de la gran barrera del arrecife australiano y ahora vive en una pequeña pecera en la oficina de un dentista de Sidney. El tímido padre de Nemo se embarcará en una peligrosa aventura donde conoce Dory. Juntos vane...",
				"/img/films/buscandoAnemo.jpg", "https://www.youtube.com/embed/wZdpNglLbt8", 2003);

		f21.getGenders().add(g1);
		f21.getGenders().add(g7);
		filmRepository.save(f21);

		// Test Data Book
		Book b1 = new Book("Los Juegos del Hambre", "",
				"Los juegos del hambre se desarrolla en un país llamado Panem, lo que es en realidad una civilización postapocalíptica ubicada en lo que antes era América del Norte.",
				"/img/los_juegos_del_hambre.jpg", 2008);
		b1.getGenders().add(g1);
		b1.getGenders().add(g9);
		bookRepository.save(b1);

		Book b2 = new Book("El retorno del rey", "J.R.R. Tolkien",
				"Los ejércitos del Señor Oscuro van extendiendo cada vez más su maléfica sombra por la Tierra Media. Hombres, elfos y enanos unen sus fuerzas para presentar batalla a Sauron y sus huestes. Ajenos a estos preparativos, Frodo y Sam siguen adentrándose en el país de Mordor en su heroico viaje para destruir el Anillo de Poder en las Grietas del Destino",
				"/img/books/elRetornoDelRey.jpg", 1955);

		b2.getGenders().add(g1);
		b2.getGenders().add(g13);
		bookRepository.save(b2);

		Book b3 = new Book("Graceling", "Kristin Cashore",
				"Hay gente en estas tierras que tiene poderes extraordinarios a los que llamamos gracias. Una gracia puede tener un valor infinito o puede ser totalmente inútil. Una gracia puede hacer que alguien sea veloz como el viento, o que sea capaz de predecir el tiempo, mientras que otras sólo harán que hables al revés sin pensar o te subas a los árboles. Mi nombre es Katsa...",
				"/img/books/graceling.jpg", 2008);

		b3.getGenders().add(g1);
		b3.getGenders().add(g13);
		bookRepository.save(b3);

		Book b4 = new Book("Los pilares de la Tierra", "Ken Folleti",
				"El gran maestro de la narrativa erótica y suspense nos transporta a la Edad Media, a un fascinante mundo de orgías, damas, caballeros, pugnas eróticas , profilácticos y ciudades con látex. El amor y el sexo se entrecruzan vibrantemente en este magistral tapiz cuyo centro es la construcción de un sex shop. La historia se inicia con el ahorcamiento público de Óscar...",
				"/img/books/losPilaresDeLaTierra.jpg", 1989);

		b4.getGenders().add(g5);
		b4.getGenders().add(g11);
		b4.getGenders().add(g3);
		bookRepository.save(b4);

		Book b5 = new Book("El juego de Ender", "Orson Scott",
				"La Tierra se ve amenazada por una especie extraterrestre de insectos, seres que se comunican telepáticamente y que se consideran totalmente distintos de los humanos, a los que quieren destruir. Para vencerlos, la humanidad necesita un genio militar, y por ello se permite el nacimiento de Ender, que es el tercer hijo de una pareja en un mundo que ha limitado ...",
				"/img/books/elJuegoDeEnder.jpg", 1985);

		b5.getGenders().add(g10);
		b5.getGenders().add(g1);
		b5.getGenders().add(g6);
		bookRepository.save(b5);

		Book b6 = new Book("El capitan Alatriste", "Arturno Pérez Reverte",
				"No era el hombre más honesto ni el más piadoso, pero era un hombre valiente... Con estas palabras empieza El capitán Alatriste, la historia de un soldado veterano de los tercios de Flandes que malvive como espadachín a sueldo en el Madrid del siglo XVII.",
				"/img/books/elCapitanAlatriste.jpg", 1996);

		b6.getGenders().add(g11);
		b6.getGenders().add(g1);
		b6.getGenders().add(g6);
		bookRepository.save(b6);

		Book b7 = new Book("La Biblia", "Dios",
				"Todo comenzó con Adán y Eva en el paraiso, pero cuando les echaron de ahí... tuvieron 2 hijos: Caín y Abel...¿Qué `pasó con las mujeres a partir de entonces? Estas dudas y muchas otras más aqui",
				"/img/books/laBiblia.jpg", 0);

		b7.getGenders().add(g1);
		b7.getGenders().add(g2);
		b7.getGenders().add(g3);
		b7.getGenders().add(g4);
		b7.getGenders().add(g5);
		b7.getGenders().add(g6);
		b7.getGenders().add(g7);
		b7.getGenders().add(g8);
		b7.getGenders().add(g9);
		b7.getGenders().add(g10);
		b7.getGenders().add(g11);
		b7.getGenders().add(g12);
		b7.getGenders().add(g13);
		bookRepository.save(b7);

		Book b8 = new Book("La biblioteca de los muertos", "Glenn Cooper",
				"Bretaña, siglo VII. En la abadía de Vectis crece Octavus, un niño al que le auguran poderes diabólicos. Octavus no tarda en empezar a escribir una lista de nombres y fechas sin ningún sentido aparente. Pero poco después, cuando una muerte en la abadía coincide con un nombre y una fecha de la lista, el miedo se apodera de los monjes. Nueva York, en la actualidad...",
				"/img/books/laBibliotecaDeLosMuertos.jpg", 2009);

		b8.getGenders().add(g5);
		b8.getGenders().add(g3);
		bookRepository.save(b8);

		Book b9 = new Book("Harry potter y el prisionero de Azkaban", "J.K. Rowling",
				"De la prisión de Azkaban se ha escapado un terrible villano, Sirius Black, un asesino en serie que fue cómplice de lord Voldemort y que, dicen los rumores, quiere vengarse de Harry por haber destruido a su maestro",
				"/img/books/harryPotterYazkaban.jpg", 1999);

		b9.getGenders().add(g1);
		b9.getGenders().add(g13);
		bookRepository.save(b9);

		Book b10 = new Book("El clan del oso cavernario: Los Hijos de la Tierra", "Jean M. Auel",
				"Narra como Ayla, una niña Cromagnon de 5 años, queda aislada de su tribu por culpa de un terremoto. Pese a ser de una estirpe más desarrollada, es acogida por un grupo de Neardentales, que basa sus razonamientos y comportamientos en las experiencias de sus antepasados.",
				"/img/books/elClanDeOsoCavernario.jpg", 1980);

		b10.getGenders().add(g11);
		b10.getGenders().add(g9);
		b10.getGenders().add(g1);
		bookRepository.save(b10);

		Book b11 = new Book("El bosque de los arboles muertos", "Ana Alcolea",
				"Beatriz va a pasar sus vacaciones veraniegas en un antiguo castillo en Escocia, algo que no le agrada demasiado. Preferiría quedarse con sus amigos en la piscina o en la playa, pero su suspenso en Inglés ha hecho que su madre recurra a una antigua amiga para que la acoja durante el verano y así Beatriz pueda practicar el idioma. Lo que parece un aburrido verano...",
				"/img/books/elBosqueDeLosArbolesMuertos.jpg", 2010);

		b11.getGenders().add(g2);
		b11.getGenders().add(g9);
		b11.getGenders().add(g3);
		b11.getGenders().add(g5);
		bookRepository.save(b11);

		Book b12 = new Book("Cronicas de la dragonlance: la reina de la oscuridad", "Margaret Weiss",
				"Al cabo de cinco años, un grupo de amigos se reúne en El Último Hogar, mientras vientos de guerra asolan el mundo en el que viven. Un arma mágica de increíble poder caerá en las manos de estos amigos y los obligará a emprender una larga aventura que decidirá el destino de Krynn, su mundo.",
				"/img/books/dragondOfSpringDawning.jpg", 1985);

		b12.getGenders().add(g1);
		b12.getGenders().add(g13);
		bookRepository.save(b12);

		Book b13 = new Book("Sherlock Holmes: la liga de los pelirojos", "Arthur Conan Doyle",
				"Wilson, un prestamista pelirrojo, acude a Sherlock Holmes y a Watson para resolver el misterio de la súbita disolución de la «Liga de los pelirrojos», de la que él había sido miembro. El detective londinense no tarda en averiguar que tras esta disolución se encuentra John Clay, uno de los delincuentes más inteligentes y peligrosos de Inglaterra.",
				"/img/books/sherlockHolmasYlosPelirojos.jpg", 1891);

		b13.getGenders().add(g5);
		b13.getGenders().add(g11);
		b13.getGenders().add(g1);
		bookRepository.save(b13);

		Book b14 = new Book("Crepúsculo", "Stephenie Meyer",
				"Cuando Isabella Swan se muda a Forks, una pequeña localidad del estado de Washington en la que nunca deja de llover, piensa que es lo más aburrido que le podía haber ocurrido en la vida. Pero su vida da un giro excitante y aterrador una vez que se encuentra con el misterioso y seductor Edward Cullen. Hasta ese momento, Edward se las ha arreglado para mantener...",
				"/img/books/crepusculo.jpg", 2005);

		b14.getGenders().add(g3);
		b14.getGenders().add(g1);
		b14.getGenders().add(g13);
		bookRepository.save(b14);

		Book b15 = new Book("A 3 metros bajo el cielo", "Federico Moccia",
				"\"Babi es una estudiante modelo y la hija perfecta. Step, en cambio, es violento y descarado. Provienen de dos mundos completamente distintos. A pesar de todo, entre los dos nacerá un amor más allá de todas las convenciones. Un amor controvertido por el que deberán luchar más de lo que esperaban. Babi...",
				"/img/books/TresMetro.jpg", 2004);

		b15.getGenders().add(g3);
		b15.getGenders().add(g1);
		bookRepository.save(b15);

		Book b16 = new Book("El quijote", "Miguel de Cervantes",
				"Como todo caballero necesita una dama, convierte el recuerdo de una campesina de la que estuvo enamorado en la hermosa Dulcinea del Toboso. Y a sí mismo se pone el nombre de Don Quijote, como el famoso caballero Lanzarote (Lancelot). Sale así al campo, con un aspecto ridículo, con la idea de realizar hazañas heroicas.",
				"/img/books/elQuijote.jpg", 1615);

		b16.getGenders().add(g1);
		b16.getGenders().add(g11);
		b16.getGenders().add(g8);
		bookRepository.save(b16);

		Book b17 = new Book("Beethoven", "Jan Swafford",
				"Beethoven, el genio romántico, hombre atormentado y fascinante capaz de componer las piezas más sublimes, vivió su tiempo con extraordinaria intensidad. Jan Swafford recrea de manera amena y profunda la vida del hombre, del compositor y del genio.",
				"/img/books/beethoven.jpg", 2017);

		b17.getGenders().add(g12);
		b17.getGenders().add(g11);
		bookRepository.save(b17);

		Book b18 = new Book("Las fronteras del significado", "Charles Rosen",
				"Charles Rosen explora cuestiones relacionadas con la composición, la interpretación y la escucha de la obra de distintos compositores, y nos ofrece así una aproximación a la música en la que tan importantes como las piezas musicales son las tradiciones en las que éstas se inscriben y se concretan...",
				"/img/books/lasFronterasDelSignificado.jpg", 2017);
		b18.getGenders().add(g12);
		b18.getGenders().add(g11);
		bookRepository.save(b18);

		Book b19 = new Book("Asesinato en el Orient Express", "Agatha Christie",
				"En un lugar aislado de la antigua Yugoslavia, en plena madrugada, una fuerte tormenta de nieve obstaculiza la línea férrea por donde circula el Orient Express. Procedente de la exótica Estambul, en él viaja el detective Hércules Poirot...",
				"/img/books/asesinatoEnOrienteExpress.jpg", 1974);

		b19.getGenders().add(g1);
		b19.getGenders().add(g5);
		bookRepository.save(b19);

		Book b20 = new Book("La cumbre escarlata", "Nancy Holder",
				"Ambientada en el siglo XIX, en la remota y montañosa región de Cumbria, al norte de Inglaterra, La cumbre escarlata sigue a la joven escritora Edith Cushing, quien descubre que su nuevo y encantador esposo, Sir Thomas Sharpe, no es quien parece ser...",
				"/img/books/laCumbreEscarlata.jpg", 2015);

		b20.getGenders().add(g2);
		b20.getGenders().add(g5);
		b20.getGenders().add(g1);
		bookRepository.save(b20);

		Book b21 = new Book("Arthas: El Ascenso de El Rey Exánime", "Christie Golden",
				"Es una entidad de poder incalculable y maldad sin parangón; su gélida alma ha sido consumida totalmente por sus planes de destruir toda lo que está vivo. Pero esto no siempre fue así. Mucho antes de que su alma se fundiera con...",
				"/img/books/Arthas.jpg", 2010);

		b21.getGenders().add(g6);
		b21.getGenders().add(g8);
		b21.getGenders().add(g1);
		bookRepository.save(b21);
		
		Book b22 = new Book("La luna de Plutón", "Dross Rotzank","Mi nuevo libro, la luna de plutón, ya está a la venta en las mejores librerias de Uruguay, Paraguay, Argentina, Chile...",
				"/img/books/lunaPluton.jpg",2010);
		
		b22.getGenders().add(g1);
		b22.getGenders().add(g2);
		b22.getGenders().add(g3);
		b22.getGenders().add(g4);
		b22.getGenders().add(g5);
		b22.getGenders().add(g6);
		b22.getGenders().add(g7);
		b22.getGenders().add(g8);
		b22.getGenders().add(g9);
		b22.getGenders().add(g10);
		bookRepository.save(b22);
		
				
		// Test Data Show
		Shows sh1 = new Shows("The Big Bang Theory", "", "",
				"La serie comienza con la llegada de Penny, aspirante a actriz, al apartamento vecino, que comparten Sheldon y Leonard, dos físicos que trabajan en el Instituto Tecnológico de California (Caltech). Leonard se enamora desde el primer momento de Penny.",
				"/img/the_big_bang_theroy.jpg", "https://www.youtube.com/embed/_uQXxvZ3afQ", 2007);

		sh1.getGenders().add(g4);
		sh1.getGenders().add(g10);
		showRepository.save(sh1);

		Shows sh2 = new Shows("Narcos", "Pedro Pascal, Wagner Moura, Boyd Holbrook",
				"Carlo Bernard, Chris Brancato, Doug Miro",
				"En esta exitosa serie nos adentraremos en la vida de Pablo Escobar, el lider de la droga de Colombia en los años 80 que aterrorizó al país durante años. Héroe para algunos, monstruo para otros, esta serie cuenta su historia lo mas fielmente posible",
				"/img/shows/narcos.jpg", "https://www.youtube.com/embed/U7elNhHwgBU", 2015);

		sh2.getGenders().add(g6);
		sh2.getGenders().add(g11);
		showRepository.save(sh2);

		Shows sh3 = new Shows("Friends", "Jennifer Aniston, Courteney Cox, Lisa Kudrow", "David Crane, Marta Kauffman",
				"En esta exitosa serie viviremos las divertidas desventuras de seis veinteañeros que se enfrentan a los obstáculos del trabajo, la vida y el amor en los 90 en Manhattan.",
				"/img/shows/friends.jpg", "https://www.youtube.com/embed/V5hOm8_3mJA", 1994);

		sh3.getGenders().add(g4);
		sh3.getGenders().add(g3);
		showRepository.save(sh3);

		Shows sh4 = new Shows("Your lie in april", "Natsuki Hanae, Risa Taneda, Ayane Sakura", "Kyohei Ishiguro",
				"Your lie in April trata de un prodigio del piano que no puede escuchar su música después de haber sufrido un trauma. Un día, a Kousei le presentan a una violinista llamada Kaori Miyazono. A pesar de que su primera impresión de ella fue terrible...",
				"/img/shows/yourLieInAbril.jpg", "https://www.youtube.com/embed/3aL0gDZtFbE", 2014);

		sh4.getGenders().add(g12);
		sh4.getGenders().add(g7);
		sh4.getGenders().add(g3);
		showRepository.save(sh4);

		Shows sh5 = new Shows("Walking dead", " Andrew Lincoln, Norman Reedus, Melissa McBride", "Frank Darabont",
				"Una serie americana en torno al drama, el terror y la ciencia ficción que narra cómo un grupo de personajes intenta sobrevivir en un mundo devastado por el apocalípsis zombie. La trama arranca cuando el protagonista, el sheriff Rick Grimes, se despierta en un hospital tras estar varios…",
				"/img/shows/theWalkingDead.jpg", "https://www.youtube.com/embed/R1v0uFms68U ", 2010);

		sh5.getGenders().add(g2);
		sh5.getGenders().add(g1);
		sh5.getGenders().add(g6);
		sh5.getGenders().add(g13);
		showRepository.save(sh5);

		Shows sh6 = new Shows("Vikingos", " Gustaf Skarsgård, Katheryn Winnick, Alexander Ludwig", "Michael Hirst",
				"La serie está basada en los relatos semilegendarios de Ragnar Lodbrok, reconocido como uno de los primeros reyes de Suecia y Dinamarca, durante el siglo VIII.",
				"/img/shows/vkingos.jpg", "https://www.youtube.com/embed/5aASH8HMJbo ", 2013);

		sh6.getGenders().add(g11);
		sh6.getGenders().add(g6);
		sh6.getGenders().add(g1);
		showRepository.save(sh6);

		Shows sh7 = new Shows("One Tree Hill", "Mark Schwahn", "Chad Michael Murray, James Lafferty, Hilarie Burton",
				"La serie narraba la historia de un grupo de jóvenes y sus familias en un pueblo pequeño llamado Tree Hill, a través de la escuela secundaria y después de la universidad.",
				"/img/shows/oneTreeHill.jpg", "https://www.youtube.com/embed/noDPlG88grk", 2003);

		sh7.getGenders().add(g3);
		showRepository.save(sh7);

		Shows sh8 = new Shows("Once upon a time", "Adam Horowitz, Edward Kitsis",
				"Ginnifer Goodwin, Jennifer Morrison, Lana Parrilla",
				"La serie sigue a varios personajes de cuentos de hadas que fueron transportados al mundo real y robaron sus recuerdos originales mediante una poderosa maldición.",
				"/img/shows/onceUponATime.jpg", "https://www.youtube.com/embed/Rga4rp4j5TY", 2011);

		sh8.getGenders().add(g1);
		sh8.getGenders().add(g3);
		sh8.getGenders().add(g3);
		showRepository.save(sh8);

		Shows sh9 = new Shows("Altered Carbon", "Joel Kinnaman, James Purefoy, Martha Higareda", "Laeta Kalogridis",
				"Altered Carbon se desarrolla en medio del siglo XXV, una época en la que las personas ya no mueren, técnicamente, sino que sus mentes y consciencias son transferidas de un cuerpo a otro...",
				"/img/shows/alteredCarbon.jpg", "https://www.youtube.com/embed/dhFM8akm9a4", 2018);

		sh9.getGenders().add(g1);
		sh9.getGenders().add(g3);
		sh9.getGenders().add(g3);
		showRepository.save(sh9);

		Shows sh10 = new Shows("Rick & Morty", "Dan Harmon, Justin Roiland",
				"Chris Parnell, Spencer Grammer, Sarah Chalke",
				"Rick, un científico alcohólico, secuestra a su influenciable nieto, Morty, para vivir peligrosas aventuras a través de nuestro cosmos y universos paralelos.",
				"/img/shows/rickYmorty.jpg", "www.youtube.com/embed/WNhH00OIPP0 ", 2013);

		sh10.getGenders().add(g4);
		sh10.getGenders().add(g1);
		sh10.getGenders().add(g7);
		sh10.getGenders().add(g10);
		showRepository.save(sh10);

		Shows sh11 = new Shows("Dr.Who", "Michael Ryan", "Peter McAllum, Kate Hardy, Richard Clark",
				"Doctor Who es un programa de televisión que trata de las aventuras de un misterioso hombre conocido sólo como el Doctor. El Doctor viaja a través del espacio y el tiempo en una nave llamada TARDIS",
				"/img/shows/doctorWho.jpg", "https://www.youtube.com/embed/SKAOzznX9zA ", 2012);

		sh11.getGenders().add(g1);
		sh11.getGenders().add(g5);
		sh11.getGenders().add(g8);
		sh11.getGenders().add(g2);
		showRepository.save(sh11);

		Shows sh12 = new Shows("Belzebub", "Shizuka Itô, Katsuyuki Konishi, Takahiro Mizushima", "Nobuhiro Takamoto",
				"La historia trata sobre el delincuente juvenil más fuerte, Oga Tatsumi, quien está en el primer año de la Escuela Ishiyama, la escuela para delincuentes. Pero un día mientras dormía cerca a un río, mira a un hombre flotar en el río, quien de repente se parte...",
				"/img/shows/beelzebub.jpg", "https://www.youtube.com/embed/5lyYR7StfrQ", 2011);

		sh12.getGenders().add(g7);
		sh12.getGenders().add(g1);
		sh12.getGenders().add(g6);
		sh12.getGenders().add(g13);
		showRepository.save(sh12);

		Shows sh13 = new Shows("Mahouka Koukou no Rettousei", "Yûichi Nakamura, Saori Hayami, Yumi Uchiyama",
				"Manabu Ono",
				"La Magia no es algo de las leyendas ni los cuentos de hadas, se convirtió en tecnología desde hace casi un siglo. El proceso mágico ha sido sistematizado y con ayuda de dispositivo llamado CAD, los procesos pueden ser acelerados significativamente y esto ...",
				"/img/shows/mahouka.jpg", "https://www.youtube.com/embed/U-gkwdGooDU", 2015);

		sh13.getGenders().add(g7);
		sh13.getGenders().add(g13);
		sh13.getGenders().add(g1);
		showRepository.save(sh13);

		Shows sh14 = new Shows("Juego de Tronos", "George R.R.Martin",
				"Sean Bean, Emilia Clarke, Peter Dinklage, Kit Harington",
				"La historia se desarrolla en un mundo ficticio de carácter medieval donde hay Siete Reinos. Hay tres líneas argumentales principales: la crónica de la guerra civil dinástica por el control de Poniente entre varias familias nobles que aspiran al Trono...",
				"/img/shows/jdt.jpg", "https://www.youtube.com/embed/g1IICkElV0M", 2011);

		sh14.getGenders().add(g6);
		sh14.getGenders().add(g13);
		sh14.getGenders().add(g1);
		showRepository.save(sh14);

		Shows sh15 = new Shows("Daredevil", "Charlie Cox, Vincent D'Onofrio, Deborah Ann Woll", "Drew Goddard",
				"Matt Murdock es un abogado de la cocina del infierno que tuvo un accidente de pequeño con productos quimicos y ahora es un superheroe que castiga a los criminales que la justicia no castiga durante el dia, el lo hace por la noche contra las fuerzas del mal como La Mano",
				"/img/shows/daredevil.jpg", "https://www.youtube.com/embed/KQzRs6UCbuI", 2015);

		sh15.getGenders().add(g6);
		sh15.getGenders().add(g8);
		sh15.getGenders().add(g1);
		showRepository.save(sh15);

		Shows sh16 = new Shows("Stranger Things", "Millie Bobby Brown, Finn Wolfhard, Winona Ryder",
				"Matt Duffer, Ross Duffer",
				"Homenaje a los clásicos misterios sobrenaturales de los años 80 es la historia de un niño que desaparece en el pequeño pueblo de Hawkins, Indiana, sin dejar rastro en 1983. En su búsqueda desesperada, tanto sus amigos y familiares como el sheriff...",
				"/img/shows/strangerThings.jpg", "https://www.youtube.com/embed/XWxyRG_tckY", 2016);

		sh16.getGenders().add(g2);
		sh16.getGenders().add(g10);
		sh16.getGenders().add(g5);
		showRepository.save(sh16);

		Shows sh17 = new Shows("Sherlock", "Benedict Cumberbach,John Wilson", "Arthur Conan Doyle",
				"Moderna actualización del mito de Sherlock Holmes, ambientado en el Londres del siglo XXI. Sus insuperables habilidades de deducción y su arrogante distanciamiento...",
				"/img/shows/sherlock.jpg", "https://www.youtube.com/embed/Nj7ZSUkTTVI ", 2010);

		sh17.getGenders().add(g1);
		sh17.getGenders().add(g5);
		sh17.getGenders().add(g11);
		showRepository.save(sh17);

		Shows sh18 = new Shows("Fargo", "Billy Bob Thornton, Martin Freeman, Allison Tolman", "John Fargo",
				"Lester Nygaard, un apocado vendedor de seguros de una pequeña población de Minnesota, ve cómo su mundo cambia por completo con la llegada de los nazis a minesota...",
				"/img/shows/fargo.jpg", "https://www.youtube.com/embed/EB4PmbfG4bw", 2014);

		sh18.getGenders().add(g1);
		sh18.getGenders().add(g9);
		showRepository.save(sh18);

		Shows sh19 = new Shows("Los Simpson", " Dan Castellaneta, Nancy Cartwright, Julie Kavner",
				" James L. Brooks, Matt Groening, Sam Simon",
				"La afamada serie de television que cuenta las aventuras y desventuras de una familia que vive en Springfield, que no vamos a...",
				"/img/shows/losSimpson.jpg", "https://www.youtube.com/embed/ypZPaxbP8g8", 1989);

		sh19.getGenders().add(g4);
		showRepository.save(sh19);

		Shows sh20 = new Shows("Sobrenatural", "Jared Padalecki,Jensen Ackles", "Eric Kripke",
				"Un misterioso mensaje telefónico de su padre, que hace mucho tiempo que ha abandonado el hogar familiar, lleva a los hermanos Dean y Sam Winchester hasta California. Aunque no ...",
				"/img/shows/sobrenatural.jpg", "https://www.youtube.com/embed/74WD8FUgS6g", 2005);

		sh20.getGenders().add(g2);
		sh20.getGenders().add(g1);
		sh20.getGenders().add(g13);
		sh20.getGenders().add(g6);
		showRepository.save(sh20);

		Shows sh21 = new Shows("The Flash", "Grant Gustin, Candice Patton, Danielle Panabaker",
				"Greg Berlanti, Geoff Johns, Andrew Kreisberg",
				"Sigue las veloces aventuras de Barry Allen, un joven común y corriente con el deseo latente de ayudar a los demás. Cuando una...",
				"/img/shows/theFlash.jpg", "https://www.youtube.com/embed/Yj0l7iGKh8g", 2005);

		sh21.getGenders().add(g2);
		sh21.getGenders().add(g11);
		sh21.getGenders().add(g5);
		sh21.getGenders().add(g8);
		showRepository.save(sh21);

		Shows sh22 = new Shows("Arrow", "Stephen Amell, Katie Cassidy, David Ramsey ",
				"Greg Berlanti, Marc Guggenheim, Andrew Kreisberg",
				"Adaptación libre de un personaje de DC Comics, playboy de día, que, durante la noche, usa su arco y sus flechas contra el crimen. Tras haber desaparecido y haber sido dado por muerto en un violento...",
				"/img/shows/arrow.jpg", "https://www.youtube.com/embed/uFdqDMxlx88", 2012);

		sh22.getGenders().add(g8);
		sh22.getGenders().add(g1);
		sh22.getGenders().add(g6);
		showRepository.save(sh22);

		Shows sh23 = new Shows("Smallville", "Tom Welling, Michael Rosenbaum, Allison Mack", "Alfred Gough, Miles Millar",
				"Serie que narra los inicios de Superman -Clark Kent- en su pueblo natal, Smallville. Allí vivía con sus padres, estudiaba en el instituto local y conoció a su primera novia, Lana Lang, y a su futuro rival, Lex Luthor.",
				"/img/shows/smallville.jpg", "https://www.youtube.com/embed/AF4eljyAhVw ", 2001);

		sh23.getGenders().add(g8);
		sh23.getGenders().add(g1);
		sh23.getGenders().add(g6);
		showRepository.save(sh23);

		// Test Data Comment Film
		CommentFilm cf2 = new CommentFilm("A mi me gustó mas la 2, pero bueno no está mal");
		cf2.setFilm(f2);
		cf2.setUser(u1);
		commentFilmRepository.save(cf2);

		CommentFilm cf3 = new CommentFilm("Qué miedo pasé, esa noche dormí abrazado a mi osito de peluche");
		cf3.setFilm(f3);
		cf3.setUser(u1);
		commentFilmRepository.save(cf3);

		CommentFilm cf4 = new CommentFilm("k dices tio, no es pa tanto, vaya cagueta");
		cf4.setFilm(f3);
		cf4.setUser(u2);
		commentFilmRepository.save(cf4);

		CommentFilm cf5 = new CommentFilm(
				"Esta peli la vi con mi novia, no esta mal, te ries, pero hubiera preferido esdla");
		cf5.setFilm(f4);
		cf5.setUser(u3);
		commentFilmRepository.save(cf5);

		CommentFilm cf6 = new CommentFilm("Es que eres un poco calzonazossss!!! jajajaja ");
		cf6.setFilm(f4);
		cf6.setUser(u2);
		commentFilmRepository.save(cf6);

		CommentFilm cf7 = new CommentFilm("¬¬");
		cf7.setFilm(f4);
		cf7.setUser(u3);
		commentFilmRepository.save(cf7);

		CommentFilm cf8 = new CommentFilm(
				"Buah, me ha encantado, me he descargado y todo la banda sonora, que por si a alguien le interesa se llaman RADWIMPS");
		cf8.setFilm(f5);
		cf8.setUser(u3);
		commentFilmRepository.save(cf8);

		CommentFilm cf9 = new CommentFilm(
				"Yo me rei mucho cuando cayó el meteorito, lo dije de broma pero al final resultó que cayó de verdad xD");
		cf9.setFilm(f5);
		cf9.setUser(u1);
		commentFilmRepository.save(cf9);

		CommentFilm cf10 = new CommentFilm(
				"No me gustan las peliculas de gladiadiores, me parace absurdo el final y gratuito, y el emperador lo hace todo mal en fin... ");
		cf10.setFilm(f6);
		cf10.setUser(u2);
		commentFilmRepository.save(cf10);

		CommentFilm cf11 = new CommentFilm(
				"Esta es la peli que hablan en Big Bang que si quitas a indiana Johnes no habria cambiado nada XDD ");
		cf11.setFilm(f7);
		cf11.setUser(u3);
		commentFilmRepository.save(cf11);

		CommentFilm cf12 = new CommentFilm(
				"A mi me ha gustado, lo unico que a la iglesia no le mola mucho porque al parecer la peli acaba con la muerte de jesucristo, y da un mensaje equivocado en teoria");
		cf12.setFilm(f8);
		cf12.setUser(u1);
		commentFilmRepository.save(cf12);

		CommentFilm cf13 = new CommentFilm("Un poco bodrio, pero bueno, no pueden gustarme todas");
		cf13.setFilm(f9);
		cf13.setUser(u2);
		commentFilmRepository.save(cf13);

		CommentFilm cf14 = new CommentFilm("Las he visto mejores");
		cf14.setFilm(f10);
		cf14.setUser(u2);
		commentFilmRepository.save(cf14);

		CommentFilm cf15 = new CommentFilm("Esta la vi en el cine y me arrepenti de gastarme el dinero");
		cf15.setFilm(f11);
		cf15.setUser(u2);
		commentFilmRepository.save(cf15);

		CommentFilm cf16 = new CommentFilm("Una buena pelicula, mis dieses");
		cf16.setFilm(f12);
		cf16.setUser(u2);
		commentFilmRepository.save(cf16);

		CommentFilm cf17 = new CommentFilm("Una buena pelicula, mis dieses");
		cf17.setFilm(f13);
		cf17.setUser(u2);
		commentFilmRepository.save(cf17);

		CommentFilm cf18 = new CommentFilm("Una buena pelicula, mis dieses");
		cf18.setFilm(f14);
		cf18.setUser(u2);
		commentFilmRepository.save(cf18);

		CommentFilm cf19 = new CommentFilm("Una buena pelicula, mis dieses");
		cf19.setFilm(f15);
		cf19.setUser(u2);
		commentFilmRepository.save(cf19);

		CommentFilm cf20 = new CommentFilm("Una buena pelicula, mis dieses");
		cf20.setFilm(f16);
		cf20.setUser(u2);
		commentFilmRepository.save(cf20);

		CommentFilm cf21 = new CommentFilm("Una buena pelicula, mis dieses");
		cf21.setFilm(f17);
		cf21.setUser(u2);
		commentFilmRepository.save(cf21);

		CommentFilm cf22 = new CommentFilm("Una buena pelicula, mis dieses");
		cf22.setFilm(f18);
		cf22.setUser(u2);
		commentFilmRepository.save(cf22);

		CommentFilm cf23 = new CommentFilm("Una buena pelicula, mis dieses");
		cf23.setFilm(f19);
		cf23.setUser(u2);
		commentFilmRepository.save(cf23);

		CommentFilm cf24 = new CommentFilm("Una buena pelicula, mis dieses");
		cf24.setFilm(f20);
		cf24.setUser(u2);
		commentFilmRepository.save(cf24);

		CommentFilm cf25 = new CommentFilm("Una buena pelicula, mis dieses");
		cf25.setFilm(f21);
		cf25.setUser(u2);
		commentFilmRepository.save(cf25);

		// Test Data Comment Show
		CommentShow cs1 = new CommentShow("Esta serie es muy buena");
		cs1.setShow(sh1);
		cs1.setUser(u1);
		commentShowRepository.save(cs1);

		CommentShow cs2 = new CommentShow("Esta serie es muy buena");
		cs2.setShow(sh2);
		cs2.setUser(u1);
		commentShowRepository.save(cs2);

		CommentShow cs3 = new CommentShow("Esta serie es muy buena");
		cs3.setShow(sh3);
		cs3.setUser(u1);
		commentShowRepository.save(cs3);

		CommentShow cs4 = new CommentShow("Esta serie es muy buena");
		cs4.setShow(sh4);
		cs4.setUser(u2);
		commentShowRepository.save(cs4);

		CommentShow cs5 = new CommentShow("Esta serie es muy buena");
		cs5.setShow(sh5);
		cs5.setUser(u1);
		commentShowRepository.save(cs5);

		CommentShow cs6 = new CommentShow("Esta serie es muy buena");
		cs6.setShow(sh6);
		cs6.setUser(u3);
		commentShowRepository.save(cs6);

		CommentShow cs7 = new CommentShow("Esta serie es muy buena");
		cs7.setShow(sh7);
		cs7.setUser(u2);
		commentShowRepository.save(cs7);

		CommentShow cs8 = new CommentShow("Esta serie es muy buena");
		cs8.setShow(sh2);
		cs8.setUser(u1);
		commentShowRepository.save(cs8);

		CommentShow cs9 = new CommentShow("Esta serie es muy buena");
		cs9.setShow(sh9);
		cs9.setUser(u1);
		commentShowRepository.save(cs9);

		CommentShow cs10 = new CommentShow("Esta serie es muy buena");
		cs10.setShow(sh10);
		cs10.setUser(u3);
		commentShowRepository.save(cs10);

		CommentShow cs11 = new CommentShow("Esta serie es muy buena");
		cs11.setShow(sh11);
		cs11.setUser(u1);
		commentShowRepository.save(cs11);

		CommentShow cs12 = new CommentShow("Esta serie es muy buena");
		cs12.setShow(sh12);
		cs12.setUser(u3);
		commentShowRepository.save(cs12);

		CommentShow cs13 = new CommentShow("Esta serie es muy buena");
		cs13.setShow(sh13);
		cs13.setUser(u2);
		commentShowRepository.save(cs13);

		CommentShow cs14 = new CommentShow("Esta serie es muy buena");
		cs14.setShow(sh14);
		cs14.setUser(u2);
		commentShowRepository.save(cs14);

		CommentShow cs15 = new CommentShow("Esta serie es muy buena");
		cs15.setShow(sh15);
		cs15.setUser(u1);
		commentShowRepository.save(cs15);

		CommentShow cs16 = new CommentShow("Esta serie es muy buena");
		cs16.setShow(sh16);
		cs16.setUser(u2);
		commentShowRepository.save(cs16);

		CommentShow cs17 = new CommentShow("Esta serie es muy buena");
		cs17.setShow(sh17);
		cs17.setUser(u3);
		commentShowRepository.save(cs17);

		CommentShow cs18 = new CommentShow("Esta serie es muy buena");
		cs18.setShow(sh18);
		cs18.setUser(u1);
		commentShowRepository.save(cs18);

		CommentShow cs19 = new CommentShow("Esta serie es muy buena");
		cs19.setShow(sh19);
		cs19.setUser(u2);
		commentShowRepository.save(cs19);

		CommentShow cs21 = new CommentShow("Rollazo BUUUUUUUUUUUUUUUUHHHHHHHHHHH");
		cs21.setShow(sh21);
		cs21.setUser(u3);
		commentShowRepository.save(cs21);

		CommentShow cs22 = new CommentShow("Esta serie es muy buena");
		cs22.setShow(sh22);
		cs22.setUser(u2);
		commentShowRepository.save(cs22);

		CommentShow cs23 = new CommentShow("Lo ha petado");
		cs23.setShow(sh23);
		cs23.setUser(u1);
		commentShowRepository.save(cs23);

		// Test Data Comment Book
		CommentBook cb1 = new CommentBook("Este libro es muy bueno");
		cb1.setBook(b1);
		cb1.setUser(u1);
		commentBookRepository.save(cb1);

		CommentBook cb2 = new CommentBook("Este libro es muy bueno");
		cb2.setBook(b2);
		cb2.setUser(u2);
		commentBookRepository.save(cb2);

		CommentBook cb3 = new CommentBook("Este libro es muy bueno");
		cb3.setBook(b3);
		cb3.setUser(u1);
		commentBookRepository.save(cb3);

		CommentBook cb4 = new CommentBook("Este libro es muy bueno");
		cb4.setBook(b4);
		cb4.setUser(u3);
		commentBookRepository.save(cb4);

		CommentBook cb5 = new CommentBook("Este libro es muy bueno");
		cb5.setBook(b5);
		cb5.setUser(u2);
		commentBookRepository.save(cb5);

		CommentBook cb6 = new CommentBook("Este libro es muy bueno");
		cb6.setBook(b6);
		cb6.setUser(u1);
		commentBookRepository.save(cb6);

		CommentBook cb7 = new CommentBook("Este libro es muy bueno");
		cb7.setBook(b7);
		cb7.setUser(u1);
		commentBookRepository.save(cb7);

		CommentBook cb8 = new CommentBook("Este libro es muy bueno");
		cb8.setBook(b8);
		cb8.setUser(u3);
		commentBookRepository.save(cb8);

		CommentBook cb9 = new CommentBook("Este libro es muy bueno");
		cb9.setBook(b9);
		cb9.setUser(u3);
		commentBookRepository.save(cb9);

		CommentBook cb10 = new CommentBook("Este libro es muy bueno");
		cb10.setBook(b10);
		cb10.setUser(u2);
		commentBookRepository.save(cb10);

		CommentBook cb11 = new CommentBook("Este libro es muy bueno");
		cb11.setBook(b11);
		cb11.setUser(u2);
		commentBookRepository.save(cb11);

		CommentBook cb12 = new CommentBook("Este libro es muy bueno");
		cb12.setBook(b12);
		cb12.setUser(u1);
		commentBookRepository.save(cb12);

		CommentBook cb13 = new CommentBook("Este libro es muy bueno");
		cb13.setBook(b13);
		cb13.setUser(u1);
		commentBookRepository.save(cb13);

		CommentBook cb14 = new CommentBook("Este libro es muy bueno");
		cb14.setBook(b14);
		cb14.setUser(u3);
		commentBookRepository.save(cb14);

		CommentBook cb15 = new CommentBook("Este libro es muy bueno");
		cb15.setBook(b15);
		cb15.setUser(u2);
		commentBookRepository.save(cb15);

		CommentBook cb16 = new CommentBook("Este libro es muy bueno");
		cb16.setBook(b16);
		cb16.setUser(u1);
		commentBookRepository.save(cb16);

		CommentBook cb17 = new CommentBook("Este libro es muy bueno");
		cb17.setBook(b17);
		cb17.setUser(u2);
		commentBookRepository.save(cb17);

		CommentBook cb18 = new CommentBook("Este libro es muy bueno");
		cb18.setBook(b1);
		cb18.setUser(u3);
		commentBookRepository.save(cb18);

		CommentBook cb19 = new CommentBook("Este libro es muy bueno");
		cb19.setBook(b19);
		cb19.setUser(u3);
		commentBookRepository.save(cb19);

		CommentBook cb20 = new CommentBook("Este libro es muy bueno");
		cb20.setBook(b20);
		cb20.setUser(u2);
		commentBookRepository.save(cb20);

		CommentBook cb21 = new CommentBook("Este libro es muy bueno");
		cb21.setBook(b21);
		cb21.setUser(u1);
		commentBookRepository.save(cb21);
		
		CommentBook cb22 = new CommentBook("MEJOR LIBRO DEL MUNDO");
		cb22.setBook(b22);
		cb22.setUser(u2);
		commentBookRepository.save(cb22);

		// Test Data Point Film
		PointFilm pf5 = new PointFilm((double) 1.5);
		pf5.setFilm(f2);
		pf5.setUser(u1);
		pointFilmRepository.save(pf5);

		PointFilm pf6 = new PointFilm((double) 3.9);
		pf6.setFilm(f3);
		pf6.setUser(u1);
		pointFilmRepository.save(pf6);

		PointFilm pf7 = new PointFilm((double) 2.3);
		pf7.setFilm(f4);
		pf7.setUser(u3);
		pointFilmRepository.save(pf7);

		PointFilm pf8 = new PointFilm((double) 4.3);
		pf8.setFilm(f5);
		pf8.setUser(u2);
		pointFilmRepository.save(pf8);

		PointFilm pf9 = new PointFilm((double) 4.9);
		pf9.setFilm(f6);
		pf9.setUser(u3);
		pointFilmRepository.save(pf9);

		PointFilm pf10 = new PointFilm((double) 2.6);
		pf10.setFilm(f7);
		pf10.setUser(u1);
		pointFilmRepository.save(pf10);

		PointFilm pf11 = new PointFilm((double) 3);
		pf11.setFilm(f8);
		pf11.setUser(u1);
		pointFilmRepository.save(pf11);

		PointFilm pf12 = new PointFilm((double) 3.5);
		pf12.setFilm(f9);
		pf12.setUser(u1);
		pointFilmRepository.save(pf12);

		PointFilm pf13 = new PointFilm((double) 1);
		pf13.setFilm(f10);
		pf13.setUser(u1);
		pointFilmRepository.save(pf13);

		PointFilm pf14 = new PointFilm((double) 2);
		pf14.setFilm(f11);
		pf14.setUser(u1);
		pointFilmRepository.save(pf14);

		PointFilm pf15 = new PointFilm((double) 4);
		pf15.setFilm(f14);
		pf15.setUser(u1);
		pointFilmRepository.save(pf14);

		PointFilm pf16 = new PointFilm((double) 3);
		pf16.setFilm(f15);
		pf16.setUser(u1);
		pointFilmRepository.save(pf15);

		PointFilm pf17 = new PointFilm((double) 2);
		pf17.setFilm(f16);
		pf17.setUser(u1);
		pointFilmRepository.save(pf16);

		PointFilm pf18 = new PointFilm((double) 1.5);
		pf18.setFilm(f17);
		pf18.setUser(u1);
		pointFilmRepository.save(pf17);

		PointFilm pf19 = new PointFilm((double) 4.4);
		pf19.setFilm(f18);
		pf19.setUser(u1);
		pointFilmRepository.save(pf18);

		PointFilm pf20 = new PointFilm((double) 4.5);
		pf20.setFilm(f19);
		pf20.setUser(u1);
		pointFilmRepository.save(pf19);

		PointFilm pf22 = new PointFilm((double) 3.5);
		pf22.setFilm(f20);
		pf22.setUser(u1);
		pointFilmRepository.save(pf22);

		PointFilm pf21 = new PointFilm((double) 5);
		pf21.setFilm(f21);
		pf21.setUser(u1);
		pointFilmRepository.save(pf21);

		PointFilm pf24 = new PointFilm((double) 3.4);
		pf24.setFilm(f12);
		pf24.setUser(u1);
		pointFilmRepository.save(pf24);

		PointFilm pf23 = new PointFilm((double) 2.6);
		pf23.setFilm(f13);
		pf23.setUser(u1);
		pointFilmRepository.save(pf23);

		// Test Data Point Show
		PointShow ps1 = new PointShow((double) 3.5);
		ps1.setShow(sh1);
		ps1.setUser(u1);
		pointShowRepository.save(ps1);

		PointShow ps2 = new PointShow((double) 4.3);
		ps2.setShow(sh2);
		ps2.setUser(u1);
		pointShowRepository.save(ps2);

		PointShow ps3 = new PointShow((double) 2.4);
		ps3.setShow(sh3);
		ps3.setUser(u2);
		pointShowRepository.save(ps3);

		PointShow ps4 = new PointShow((double) 4.2);
		ps4.setShow(sh4);
		ps4.setUser(u1);
		pointShowRepository.save(ps4);

		PointShow ps5 = new PointShow((double) 1.4);
		ps5.setShow(sh5);
		ps5.setUser(u3);
		pointShowRepository.save(ps5);

		PointShow ps6 = new PointShow((double) 5);
		ps6.setShow(sh6);
		ps6.setUser(u1);
		pointShowRepository.save(ps6);

		PointShow ps7 = new PointShow((double) 2.7);
		ps7.setShow(sh7);
		ps7.setUser(u1);
		pointShowRepository.save(ps7);

		PointShow ps8 = new PointShow((double) 2.2);
		ps8.setShow(sh8);
		ps8.setUser(u1);
		pointShowRepository.save(ps8);

		PointShow ps9 = new PointShow((double) 3.6);
		ps9.setShow(sh9);
		ps9.setUser(u1);
		pointShowRepository.save(ps9);

		PointShow ps10 = new PointShow((double) 4.5);
		ps10.setShow(sh10);
		ps10.setUser(u1);
		pointShowRepository.save(ps10);

		PointShow ps11 = new PointShow((double) 4.7);
		ps11.setShow(sh11);
		ps11.setUser(u1);
		pointShowRepository.save(ps11);

		PointShow ps12 = new PointShow((double) 4);
		ps12.setShow(sh12);
		ps12.setUser(u1);
		pointShowRepository.save(ps12);

		PointShow ps13 = new PointShow((double) 2.5);
		ps13.setShow(sh1);
		ps13.setUser(u1);
		pointShowRepository.save(ps13);

		PointShow ps14 = new PointShow((double) 3);
		ps14.setShow(sh14);
		ps14.setUser(u1);
		pointShowRepository.save(ps14);

		PointShow ps15 = new PointShow((double) 4);
		ps15.setShow(sh15);
		ps15.setUser(u1);
		pointShowRepository.save(ps15);

		PointShow ps16 = new PointShow((double) 3.6);
		ps16.setShow(sh16);
		ps16.setUser(u1);
		pointShowRepository.save(ps16);

		PointShow ps17 = new PointShow((double) 4.2);
		ps17.setShow(sh17);
		ps17.setUser(u1);
		pointShowRepository.save(ps17);

		PointShow ps18 = new PointShow((double) 4.6);
		ps18.setShow(sh18);
		ps18.setUser(u1);
		pointShowRepository.save(ps18);

		PointShow ps19 = new PointShow((double) 4.9);
		ps19.setShow(sh19);
		ps19.setUser(u1);
		pointShowRepository.save(ps19);

		PointShow ps20 = new PointShow((double) 5);
		ps20.setShow(sh20);
		ps20.setUser(u1);
		pointShowRepository.save(ps20);

		PointShow ps21 = new PointShow((double) 3.2);
		ps21.setShow(sh21);
		ps21.setUser(u1);
		pointShowRepository.save(ps21);

		PointShow ps22 = new PointShow((double) 2.2);
		ps22.setShow(sh22);
		ps22.setUser(u1);
		pointShowRepository.save(ps22);

		PointShow ps23 = new PointShow((double) 1);
		ps23.setShow(sh23);
		ps23.setUser(u1);
		pointShowRepository.save(ps23);

		// Test Data Point Book
		PointBook pb1 = new PointBook((double) 2);
		pb1.setBook(b1);
		pb1.setUser(u1);
		pointBookRepository.save(pb1);

		PointBook pb2 = new PointBook((double) 3.5);
		pb2.setBook(b2);
		pb2.setUser(u1);
		pointBookRepository.save(pb2);

		PointBook pb3 = new PointBook((double) 2.6);
		pb3.setBook(b3);
		pb3.setUser(u1);
		pointBookRepository.save(pb3);

		PointBook pb4 = new PointBook((double) 4.5);
		pb4.setBook(b4);
		pb4.setUser(u1);
		pointBookRepository.save(pb4);

		PointBook pb5 = new PointBook((double) 3.1);
		pb5.setBook(b5);
		pb5.setUser(u1);
		pointBookRepository.save(pb5);

		PointBook pb6 = new PointBook((double) 2);
		pb6.setBook(b6);
		pb6.setUser(u1);
		pointBookRepository.save(pb6);

		PointBook pb7 = new PointBook((double) 3.5);
		pb7.setBook(b7);
		pb7.setUser(u1);
		pointBookRepository.save(pb7);

		PointBook pb8 = new PointBook((double) 3.8);
		pb8.setBook(b8);
		pb8.setUser(u1);
		pointBookRepository.save(pb8);

		PointBook pb9 = new PointBook((double) 3);
		pb9.setBook(b9);
		pb9.setUser(u1);
		pointBookRepository.save(pb9);

		PointBook pb10 = new PointBook((double) 4.5);
		pb10.setBook(b10);
		pb10.setUser(u1);
		pointBookRepository.save(pb10);

		PointBook pb11 = new PointBook((double) 3.9);
		pb11.setBook(b11);
		pb11.setUser(u1);
		pointBookRepository.save(pb11);

		PointBook pb12 = new PointBook((double) 4.1);
		pb12.setBook(b12);
		pb12.setUser(u1);
		pointBookRepository.save(pb12);

		PointBook pb13 = new PointBook((double) 5);
		pb13.setBook(b13);
		pb13.setUser(u1);
		pointBookRepository.save(pb13);

		PointBook pb14 = new PointBook((double) 4);
		pb14.setBook(b14);
		pb14.setUser(u1);
		pointBookRepository.save(pb14);

		PointBook pb15 = new PointBook((double) 2.9);
		pb15.setBook(b15);
		pb15.setUser(u1);
		pointBookRepository.save(pb15);

		PointBook pb16 = new PointBook((double) 3);
		pb16.setBook(b16);
		pb16.setUser(u1);
		pointBookRepository.save(pb16);

		PointBook pb17 = new PointBook((double) 3.5);
		pb17.setBook(b17);
		pb17.setUser(u1);
		pointBookRepository.save(pb17);

		PointBook pb18 = new PointBook((double) 1.9);
		pb18.setBook(b18);
		pb18.setUser(u1);
		pointBookRepository.save(pb18);

		PointBook pb19 = new PointBook((double) 1);
		pb19.setBook(b19);
		pb19.setUser(u1);
		pointBookRepository.save(pb19);

		PointBook pb20 = new PointBook((double) 3.2);
		pb20.setBook(b20);
		pb20.setUser(u1);
		pointBookRepository.save(pb20);

		PointBook pb21 = new PointBook((double) 4);
		pb21.setBook(b21);
		pb21.setUser(u1);
		pointBookRepository.save(pb21);
		
		PointBook pb22 = new PointBook((double) 5);
		pb22.setBook(b22);
		pb22.setUser(u1);
		pointBookRepository.save(pb22);

	}
}
