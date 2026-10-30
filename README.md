# AtlasRH — Gestion des Ressources Humaines

## Présentation

**AtlasRH** est une application backend développée avec Spring Boot permettant de gérer les ressources humaines d'une entreprise de 250 collaborateurs.

L'application expose une API REST qui facilite la gestion des départements, des employés, des contrats de travail et des demandes de congé.

Le projet a pour objectif de mettre en pratique les concepts de développement backend avec Java, Spring Boot, Spring Data JPA, Hibernate et Liquibase.

## Objectifs du projet

- Centraliser les informations relatives aux employés.
- Organiser les employés par département.
- Gérer les contrats de travail.
- Permettre aux employés de soumettre des demandes de congé.
- Permettre aux managers de consulter et de traiter les demandes de congé de leurs collaborateurs.
- Calculer le solde annuel des congés.

## Technologies utilisées

| Technologie | Utilisation |
|---|---|
| Java 17 | Langage de programmation |
| Spring Boot 4.1.1 | Framework backend |
| Spring Web | Création des API REST |
| Spring Data JPA | Accès aux données |
| Hibernate | Mapping objet-relationnel (ORM) |
| MySQL | Système de gestion de base de données |
| Liquibase | Gestion des migrations de la base de données |
| Jakarta Validation | Validation des données |
| MapStruct 1.6.3 | Mapping entre DTO et entités |
| Lombok | Réduction du code répétitif |
| Maven | Gestion des dépendances et du build |
| Postman | Test manuel des API |

## Fonctionnalités principales

### 1. Gestion des départements — RH

- Créer un département.
- Consulter la liste des départements.
- Consulter un département par son identifiant.
- Modifier les informations d'un département.
- Supprimer un département.
- Vérifier l'unicité du nom du département.

### 2. Gestion des employés — RH

- Ajouter un employé.
- Consulter tous les employés.
- Consulter un employé par son identifiant.
- Filtrer les employés par département.
- Modifier les informations d'un employé.
- Supprimer un employé.
- Vérifier l'unicité du matricule et de l'adresse e-mail.
- Associer un employé à un département et éventuellement à un manager.

### 3. Gestion des contrats — RH

- Créer un contrat de travail.
- Consulter tous les contrats.
- Consulter un contrat par son identifiant.
- Consulter les contrats d'un employé.
- Modifier un contrat.
- Supprimer un contrat.

Les types de contrats disponibles sont :

- CDI
- CDD
- STAGE

### 4. Gestion des types de congé — RH

- Créer un type de congé.
- Consulter les types de congé.
- Modifier un type de congé.
- Supprimer un type de congé.
- Définir le nombre de jours autorisés par année.
- Définir si le congé est payé ou non.

### 5. Gestion des demandes de congé — Employé et Manager

**Employé :**
- Soumettre une demande de congé.
- Consulter ses demandes de congé.
- Consulter le détail d'une demande.
- Annuler une demande lorsque les règles métier le permettent.

**Manager :**
- Consulter les demandes en attente de ses collaborateurs.
- Approuver une demande de congé.
- Refuser une demande avec un commentaire.

Les demandes utilisent les statuts suivants :

- `PENDING` : en attente de validation.
- `APPROVED` : approuvée.
- `REJECTED` : refusée.
- `CANCELLED` : annulée.

### 6. Gestion du solde des congés

- Consulter le solde de congé d'un employé.
- Calculer les jours de congé consommés.
- Calculer les jours restants.
- Prendre en compte uniquement les demandes approuvées.
- Calculer les jours ouvrables sur la période demandée.

## Architecture du projet

Le projet suit une organisation en couches afin de séparer les responsabilités :

```text
src/
└── main/
    ├── java/
    │   └── ma/atlasrh/
    │       ├── config/
    │       ├── controller/
    │       ├── dto/
    │       ├── entity/
    │       ├── enums/
    │       ├── exception/
    │       ├── mapper/
    │       ├── repository/
    │       └── service/
    │
    └── resources/
        ├── db/
        │   └── changelog/
        │       ├── db.changelog-master.xml
        │       └── ...
        └── application.properties
```

### Rôle des différentes couches

| Couche | Responsabilité |
|---|---|
| Controller | Exposer les endpoints REST et recevoir les requêtes HTTP |
| Service | Contenir les règles métier |
| Repository | Communiquer avec la base de données |
| Entity | Représenter les tables de la base de données |
| DTO | Définir les données échangées avec l'API |
| Mapper | Convertir les DTO en entités et inversement |
| Exception | Gérer les exceptions et les erreurs de l'application |
| Config | Regrouper la configuration de l'application |

## Base de données

Le projet utilise **MySQL** comme système de gestion de base de données.

Nom de la base de données :

```text
atlasrh
```

Les migrations sont gérées avec **Liquibase** afin de suivre les évolutions du schéma de la base de données.

Le fichier principal des migrations est :

```text
src/main/resources/db/changelog/db.changelog-master.xml
```

Hibernate utilise le mode `validate` pour vérifier la correspondance entre les entités JPA et le schéma créé par Liquibase.

## API REST — Endpoints

Base URL :

```text
http://localhost:8080
```

### Départements

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/departments` | Créer un département |
| GET | `/api/departments` | Lister les départements |
| GET | `/api/departments/{id}` | Consulter un département |
| PUT | `/api/departments/{id}` | Modifier un département |
| DELETE | `/api/departments/{id}` | Supprimer un département |

### Employés

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/employees` | Ajouter un employé |
| GET | `/api/employees` | Lister les employés |
| GET | `/api/employees/{id}` | Consulter un employé |
| GET | `/api/employees/by-department/{departmentId}` | Lister les employés d'un département |
| PUT | `/api/employees/{id}` | Modifier un employé |
| DELETE | `/api/employees/{id}` | Supprimer un employé |

### Contrats

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/contracts` | Créer un contrat |
| GET | `/api/contracts` | Lister les contrats |
| GET | `/api/contracts/{id}` | Consulter un contrat |
| GET | `/api/contracts/by-employee/{employeeId}` | Contrats d'un employé |
| PUT | `/api/contracts/{id}` | Modifier un contrat |
| DELETE | `/api/contracts/{id}` | Supprimer un contrat |

### Types de congé

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/leave-types` | Créer un type de congé |
| GET | `/api/leave-types` | Lister les types de congé |
| GET | `/api/leave-types/{id}` | Consulter un type de congé |
| PUT | `/api/leave-types/{id}` | Modifier un type de congé |
| DELETE | `/api/leave-types/{id}` | Supprimer un type de congé |

### Demandes de congé

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/leave-requests` | Soumettre une demande |
| GET | `/api/leave-requests/{id}` | Consulter une demande |
| GET | `/api/leave-requests/by-employee/{employeeId}` | Demandes d'un employé |
| GET | `/api/leave-requests/by-manager/{managerId}/pending` | Demandes en attente d'un manager |
| PATCH | `/api/leave-requests/{id}/approve?managerId={managerId}` | Approuver une demande |
| PATCH | `/api/leave-requests/{id}/reject?managerId={managerId}&managerComment={comment}` | Refuser une demande |
| PATCH | `/api/leave-requests/{id}/cancel` | Annuler une demande |

### Solde des congés

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/leave-balances?employeeId={id}&leaveTypeId={id}&year={year}` | Consulter le solde annuel |

## Gestion des erreurs et validation

L'application utilise :

- Jakarta Validation pour valider les données reçues.
- `@Valid` pour déclencher la validation des DTO.
- `@RestControllerAdvice` pour centraliser la gestion des exceptions.
- Des exceptions métier pour gérer les ressources inexistantes, les doublons et les règles métier.

Les principaux codes HTTP utilisés sont :

| Code | Signification |
|---|---|
| 200 | OK |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request |
| 404 | Not Found |
| 409 | Conflict |
| 500 | Internal Server Error |

## Installation et exécution

### Prérequis

Avant de lancer le projet, il faut installer :

- JDK 17
- MySQL 8.x
- Maven (ou utiliser le Maven Wrapper fourni)
- Git

### 1. Cloner le projet

```bash
git clone <URL_DU_DEPOT_GITHUB>
```

### 2. Accéder au dossier

```bash
cd atlasrh
```

### 3. Créer la base de données

Dans MySQL :

```sql
CREATE DATABASE atlasrh;
```

### 4. Configurer la connexion à MySQL

Dans `src/main/resources/application.properties`, configurer les paramètres de connexion :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/atlasrh
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}

spring.jpa.hibernate.ddl-auto=validate

spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
```

Adapter le nom d'utilisateur et le mot de passe selon votre configuration MySQL.

### 5. Compiler le projet

Sous Windows :

```powershell
.\mvnw.cmd clean compile
```

### 6. Lancer l'application

```powershell
.\mvnw.cmd spring-boot:run
```

L'API sera accessible à l'adresse :

```text
http://localhost:8080
```

## Tests

Les endpoints peuvent être testés avec **Postman**.

Les scénarios de test couvrent notamment :

- La création, la consultation, la modification et la suppression des ressources.
- La validation des champs obligatoires.
- La détection des doublons.
- La gestion des ressources inexistantes.
- Le contrôle des règles métier liées aux congés.
- L'approbation et le refus des demandes.
- Le calcul du solde annuel des congés.

Il est recommandé d'utiliser une base de données dédiée aux tests afin d'éviter de modifier les données de développement.

## Remarque sur les profils

Le projet distingue trois profils métier : **Responsable RH, Manager et Employé**.

Les fonctionnalités métier sont organisées selon ces profils. Cependant, l'authentification et la gestion des autorisations par utilisateur ne sont pas implémentées dans cette version. Le `managerId` utilisé dans les opérations de validation sert à vérifier les relations métier avec les employés, et ne constitue pas une authentification sécurisée.

## Auteur

Projet réalisé dans le cadre du Sprint 3 — Développement Java / Spring Boot.

**AtlasRH — Gestion des Ressources Humaines**
