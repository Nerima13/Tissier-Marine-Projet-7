## Stack Technique

• Java 24

• Spring Boot 3.5.7

• Spring MVC / Web

• Spring Data JPA + MySQL

• Spring Security (authentification + hashage BCrypt)

• Thymeleaf pour l’interface

• Bootstrap 5 pour la mise en forme

• Maven pour la gestion du projet

• JUnit 5, Mockito pour les tests

• Surefire et JaCoCo pour les rapports



## Structure du projet

Tissier-Marine-Projet-7/

├── src/main/java/com/nnk/springboot/

│ ├── controllers/

│ ├── domain/

│ ├── repositories/

│ ├── services/

│ ├── config/

│ └── PoseidonApplication.java

├── src/main/resources/

│ ├── templates/ (vues Thymeleaf)

│ └── application.properties

├── src/test/java/com/nnk/springboot/ (tests unitaires)

├── target/ (dossier généré par Maven)

├── report/ (captures d’exécution)

└── pom.xml



## Fonctionnalités principales

CRUD complet pour :

* BidList
* CurvePoint
* Rating
* RuleName
* Trade
* User (accès limité aux ADMIN)



Chaque entité possède :

* Un controller
* Un service métier
* Un repository JPA
* Des vues Thymeleaf (list, add, update)



## Sécurité

• Authentification basée sur une session

• Gestion des utilisateurs avec rôle (ADMIN / USER)

• Mot de passe hashé avec BCryptPasswordEncoder

• Accès filtré via Spring Security + SecurityFilterChain



## Tests

1. **Rapport d'exécution en captures d'écran**

Dossier : report/

Ce dossier contient toutes mes captures d'exécution montrant le bon fonctionnement de l'application.



**2. Rapport JaCoCo**

Dossier : target/site/jacoco/index.html

Contient : 

* Pourcentage de couverture
* Détails par classe / package



**3. Rapport Surefire**

Dossier : target/reports/surefire.html

Montre :

* Résultats des tests
* Réussites / échecs



## Lancer le projet

1. **Configurer la base de données MySQL**

Modifier application.properties :

spring.datasource.url=jdbc:mysql://localhost:3306/demo

spring.datasource.username=root

spring.datasource.password=xxxx

spring.jpa.hibernate.ddl-auto=update



**2. Lancer avec Maven**

mvn spring-boot:run



**3. Accéder à l'application**

http://localhost:8080/

