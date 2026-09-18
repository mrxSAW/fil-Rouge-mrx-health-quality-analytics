# Backend Health Quality Analytics

API REST développée avec Java 21 et Spring Boot 3.5.5.

## Fonctionnalités

- Authentification JWT et permissions par rôle.
- Gestion des utilisateurs et départements.
- Gestion des incidents, audits et actions correctives.
- Admissions mensuelles.
- Statistiques globales et par département.
- Export PDF et Excel.
- Cache Redis et migrations Flyway.

## Organisation du code

Le package principal est `org.example.healthcarequalite`.

- `controller` : endpoints REST.
- `service` : règles métier.
- `repository` : accès aux données.
- `entity` : entités JPA.
- `dto` : données des requêtes et réponses.
- `mapper` : conversions entre entités et DTO.
- `security` : authentification et autorisation.
- `config` : configuration de l'application.

## Lancement local

Prérequis : Java 21, MySQL 8 et Redis.

Configurer les connexions et le secret JWT dans les propriétés
ou avec les variables d'environnement.

Depuis ce dossier :

```powershell
.\mvnw.cmd spring-boot:run
```

Swagger local : http://localhost:8080/swagger-ui.html

## Lancement Docker

Depuis la racine du dépôt, après préparation de `.env` :

```powershell
docker compose up --build -d backend
```

Compose démarre aussi MySQL et Redis.

Swagger Docker : http://localhost:8081/swagger-ui.html

## Tests

Depuis ce dossier :

```powershell
.\mvnw.cmd "-Dspring.profiles.active=test" test
```

Le profil `test` utilise H2 et désactive Flyway.
Ces tests ne remplacent pas la vérification des migrations sur MySQL.

## Principales routes

| Préfixe | Fonction |
|---|---|
| `/api/auth` | Connexion et inscription |
| `/api/users` | Utilisateurs |
| `/api/departments` | Départements |
| `/api/incidents` | Incidents |
| `/api/audits` | Audits |
| `/api/corrective-actions` | Actions correctives |
| `/api/monthly-admissions` | Admissions |
| `/api/statistics` | Statistiques |
| `/api/reports` | Rapports |

Consulter Swagger pour les paramètres et formats des DTO.

Les routes protégées attendent l'en-tête
`Authorization: Bearer <token>`.

Les dates de l'API utilisent le format `yyyy-MM-dd`.

## Calculs

- Conformité : critères conformes × 100 / critères évalués.
- Qualité globale : moyenne des scores des audits.
- Risque : 0,4 × incidents critiques + 0,3 × critères non conformes
  + 0,3 × actions en retard.
- Taux d'incidents : incidents × 1 000 / admissions du mois.

Le score de risque n'est pas un pourcentage.
Le taux d'incidents est non calculable si les admissions sont absentes
ou égales à zéro.

## Base de données

Les migrations sont dans `src/main/resources/db/migration`.

Conserver les migrations déjà appliquées.
Créer une nouvelle migration versionnée pour chaque évolution.

La configuration Docker utilise Flyway et `ddl-auto=validate`.
`baseline-on-migrate` reste désactivé pour la base Docker neuve.

## Construction de l'image

Le Dockerfile compile le JAR avec Maven puis l'exécute avec Java 21.
Les tests sont à lancer séparément.