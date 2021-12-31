# Inleiding

Een handige backend applicatie om het gebruik van educatieve woordkaarten in te zetten bij het aanleren van de de Nederlandse taal.

De woordkaarten worden aangemaakt en klaargezet voor de studenten, elke module kan een x aantal kaarten bevatten en is dan weer te geven op een bepaald moment. De student gaat door deze woordkaarten heen en als deze is afgerond dan is het wachten op de volgende module. Leerkrachten kunnen de voortgang per klas bekijken, zo is er inzicht in de progressie die de studenten maken.

De verschillende endpoints maken het mogelijk om gebruikers en woordkaarten aan te maken. Een woordkaart te bekijken, progressie inzien van verschillende klassen en wanneer nodig kan een woordkaart aangepast worden door een leerkracht.

De admin is er in dit project hardcoded ingezet, vanuit deze user kunnen er meerdere gebruikers aangemaakt worden.



# De Applicatie starten

Zorg dat je het project hebt gecloned of gedownload vanaf Github naar jouw lokale machine.

Bij de application.properties pas je wanneer nodig de volgende gegevens aan naar je eigen PostgreSQL server:

`spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres`

Vervolgens gebruik je de mogelijkheden van je IDE om de applicatie uit te voeren of je gebruikt een terminal. Zorg dat je in de terminal in de map zit van het project en gebruik het volgende command:

`./mvnw spring-boot:run`



# Dependencies

### Spring Data JPA

Spring Data JPA, onderdeel van de grotere Spring Data-familie, maakt het eenvoudig om op JPA gebaseerde repositories eenvoudig te implementeren. Deze module behandelt verbeterde ondersteuning voor op JPA gebaseerde gegevenstoegangslagen. Het maakt het gemakkelijker om Spring-powered applicaties te bouwen die datatoegangstechnologieën gebruiken.

### Spring Boot Starter Web

Starter voor het bouwen van web, inclusief RESTful, applicaties met Spring MVC. Gebruikt Tomcat als de standaard ingebedde container.

### Spring Boot Starter Security

Starter voor het gebruik van Spring Security.

### PostgreSQL JDBC driver

Met het PostgreSQL JDBC-stuurprogramma (kortweg PgJDBC) kunnen Java-programma's verbinding maken met een PostgreSQL-database met behulp van standaard, database-onafhankelijke Java-code. Is een open source JDBC-stuurprogramma geschreven in Pure Java (Type 4) en communiceert in het PostgreSQL-native netwerkprotocol.

### Project Lombok

Een project dat mogelijkheden toevoegt aan de Java compiler. Hierdoor heb je als ontwikkelaar extra features tot je beschikking die niet standaard in Java zitten. Het doel van Lombok is vooral om het schrijven of genereren van zogenaamde boiler plate code, die helaas in Java vaak nodig is, overbodig te maken. Door je klasse van Lombok annotaties te voorzien, vertel je de compiler deze code – build time – voor je te genereren.

### Spring Boot Starter Test

Starter voor het testen van Spring Boot applicaties met bibliotheken van JUnit Jupiter, Hamcrest and Mockito.

### Mockito Core

Mockito mock objects bibliotheek core API en implementatie.

# Gebruikersrollen

Deze backend maakt gebruik van drie user-rollen:

**ADMIN**

Beheerder met alle rechten, mag TEACHERS en STUDENT aanmaken als ook woordkaarten.

**TEACHER**

Gebruikers met de rol TEACHER mag studenten aanmaken, woordkaarten wijzigen en studenten, klassen en modules bekijken.

**STUDENT**

Gebruikers met de rol STUDENT mogen woordkaarten bekijken en hun eigen gegevens inzien.

# Gebruikers

Een overzicht van de automatisch aangemaakte gebruikers.

Username:	admin@gmail.com
Password:	password

Username:	teacher@gmail.com
Password:	password

Username:	student@gmail.com
Password:	password
Klas:		1A

Username:	student2@gmail.com
Password:	password
Klas:		2A



# WoordKaarten

Een overzicht van de automatisch aangemaakte woordkaarten.

Id: f35016c3-c285-4697-9db6-3091db206dfa
Text: Hond
Module: 1

Id: 6f06f16c-a90a-4c87-8a44-e3a06f4aa4e8
Text: Kat
Module: 1

Id: 130c6440-c310-475b-91c7-7613508f3c58
Text: Leeuw
Module: 2

# Werking / Testen

Om de werking te testen dient er via de PUT api/cards/view endpoint kaarten bekeken te worden door studenten. Er kan via Postman een PUT verstuurd worden met als basic authenticatie die van een student. In de publieke Postman collectie heb ik al een set klaargezet om de applicatie te testen.

Gebruik deze link om naar een publieke Postman omgeving te gaan met vooraf ingevulde requests:
[https://www.postman.com/ttrinh85/workspace/public/collection/17433272-223b9c9b-b936-443c-a80f-a74d26a10a34](https://www.postman.com/ttrinh85/workspace/public/collection/17433272-223b9c9b-b936-443c-a80f-a74d26a10a34)


# Endpoints


* POST 	/api/users/create
* POST 	/api/cards/create
* PUT 		/api/cards/view
* PUT 		/api/cards/modify
* GET 		/api/users/
* GET 		/api/cards/all
* GET 		/api/cards/class/{CLAZZ}
* GET 		/api/cards/module/{MODULE}
* GET 		/api/cards/viewed/{MODULE}
* DELETE 	/api/cards/delete
* DELETE 	/api/users

