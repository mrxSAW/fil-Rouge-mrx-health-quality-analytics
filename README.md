# Health Quality Analytics

Application web de suivi de la qualité des soins permettant de gérer les incidents, les audits, les actions correctives et les indicateurs de qualité d’un établissement de santé.

Projet réalisé dans le cadre du fil rouge ENAA.

## Application en ligne

- **Frontend** : https://health-quality-frontend.onrender.com
- **Documentation Swagger** : https://fil-rouge-mrx-health-quality-analytics.onrender.com/swagger-ui.html
- **API** : https://fil-rouge-mrx-health-quality-analytics.onrender.com/api
- **Dépôt GitHub** : https://github.com/mrxSAW/fil-Rouge-mrx-health-quality-analytics

Le backend utilise une instance gratuite Render. Après une période d’inactivité, la première requête peut prendre davantage de temps pendant son redémarrage.

## Fonctionnalités

- Authentification avec JWT.
- Gestion des utilisateurs, des rôles et des départements.
- Consultation du profil de l’utilisateur connecté.
- Déclaration et suivi des incidents.
- Filtrage des incidents par type, gravité et statut.
- Gestion des audits et suivi de la conformité.
- Gestion des actions correctives et de leurs échéances.
- Suivi des admissions mensuelles.
- Tableau de bord avec indicateurs et graphiques.
- Export de rapports PDF et Excel.
- Historique des rapports générés et téléchargement des fichiers enregistrés.
- Pagination des listes.
- Thèmes clair et sombre.

## Technologies

| Partie | Technologies |
|---|---|
| Backend | Java 21, Spring Boot 3.5.5 |
| Sécurité | Spring Security, JWT |
| Accès aux données | Spring Data JPA, Hibernate |
| Base de données | MySQL |
| Migrations | Flyway |
| Cache | Redis / Valkey compatible Redis |
| Frontend | React 19, Vite, React Router |
| Appels HTTP | Axios |
| Graphiques | Recharts |
| Rapports PDF | Apache PDFBox |
| Rapports Excel | Apache POI |
| Conteneurs | Docker, Docker Compose, Nginx |
| Vérifications automatisées | GitHub Actions, ESLint, Maven |
| Analyse de code | Qodana |

## Architecture

```text
Navigateur
    |
    v
Frontend React
    |
    | /api/*
    v
Backend Spring Boot
    |
    +-- MySQL : stockage des données
    |
    +-- Redis / Valkey : cache
```

Le frontend utilise le chemin `/api` pour communiquer avec le backend.

Selon l’environnement :

- En développement, Vite transmet les requêtes au backend local.
- Avec Docker Compose, Nginx transmet les requêtes au conteneur backend.
- Sur Render, une règle de réécriture transmet les requêtes à l’API déployée.

Les contrôles d’accès sont appliqués côté backend.

## Organisation du dépôt

```text
fil-Rouge-mrx-health-quality-analytics/
├── .github/
│   └── workflows/
│       ├── ci.yml
│       └── qodana.yml
├── backend/
│   └── healthCareQualite/
│       ├── src/
│       ├── pom.xml
│       ├── Dockerfile
│       ├── qodana.yaml
│       └── README.md
├── frontend/
│   └── frontend-react/
│       ├── src/
│       ├── package.json
│       ├── vite.config.js
│       ├── Dockerfile
│       └── README.md
├── docs/
├── .env.example
├── compose.yaml
└── README.md
```

## Rôles

| Rôle | Usage principal |
|---|---|
| `ADMIN` | Administration des comptes, des départements et des modules |
| `QUALITY_MANAGER` | Suivi qualité, audits, indicateurs et rapports |
| `QHSE_MANAGER` | Suivi QHSE, actions correctives, indicateurs et rapports |
| `STAFF` | Déclaration et suivi des incidents accessibles à son compte, actions attribuées |

Les autorisations précises dépendent des règles définies dans les contrôleurs et services du backend. L’affichage d’un menu ne remplace pas le contrôle d’accès à l’API.

## Lancement local avec Docker Compose

### Prérequis

- Git.
- Docker Desktop démarré avec les conteneurs Linux.
- Docker Compose.

Java, Maven et Node.js ne sont pas nécessaires sur le poste pour cette méthode : la compilation s’effectue dans les images Docker.

### 1. Récupérer le projet

```bash
git clone https://github.com/mrxSAW/fil-Rouge-mrx-health-quality-analytics.git
cd fil-Rouge-mrx-health-quality-analytics
```

### 2. Préparer la configuration

Dans PowerShell :

```powershell
Copy-Item .env.example .env
```

Sous Linux ou macOS :

```bash
cp .env.example .env
```

Personnaliser les variables suivantes dans `.env` :

| Variable | Description |
|---|---|
| `MYSQL_ROOT_PASSWORD` | Mot de passe administrateur MySQL |
| `MYSQL_PASSWORD` | Mot de passe du compte MySQL utilisé par le backend |
| `JWT_SECRET` | Secret de signature des tokens JWT |

Remplacer les valeurs d’exemple par des valeurs propres à l’environnement. Ne pas publier le fichier `.env` ni les secrets.

### 3. Démarrer les services

```bash
docker compose config --quiet
docker compose up --build -d
docker compose ps
```

Cette commande démarre quatre services :

- `mysql`
- `redis`
- `backend`
- `frontend`

L’option `-d` lance les conteneurs en arrière-plan.

Pour suivre le démarrage du backend :

```bash
docker compose logs -f backend
```

Quitter les logs avec `Ctrl+C` ne coupe pas les services.

### 4. Accéder à l’application

| Service | Adresse locale |
|---|---|
| Frontend | http://localhost:3000 |
| Swagger | http://localhost:8081/swagger-ui.html |
| API | http://localhost:8081/api |

Le backend écoute sur le port `8080` dans son conteneur et est exposé sur le port `8081` du poste.

## Développement sans Docker

### Prérequis

- Java 21.
- Maven.
- Node.js 22 compatible avec la version de Vite du projet.
- MySQL et Redis accessibles.

### Backend

Depuis la racine du dépôt :

```bash
cd backend/healthCareQualite
```

Configurer les connexions MySQL, Redis et le secret JWT avec les variables d’environnement adaptées, puis lancer :

```bash
mvn spring-boot:run
```

Par défaut, le backend est accessible sur :

```text
http://localhost:8080
```

### Frontend

Dans un second terminal, depuis la racine du dépôt :

```bash
cd frontend/frontend-react
npm ci
npm run dev
```

Vite affiche l’adresse du frontend dans le terminal, généralement :

```text
http://localhost:5173
```

Le proxy de développement défini dans `vite.config.js` transmet `/api` vers `http://localhost:8080`.

## Base de données et cache

MySQL conserve les données métier.

Avec Docker Compose, les données sont stockées dans le volume nommé `mysql_data`. La base MySQL déjà installée sur le poste n’est pas importée automatiquement.

Flyway gère les migrations du schéma. Hibernate vérifie sa cohérence avec :

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Redis contient des données temporaires de cache. Il doit être accessible lorsque les fonctionnalités qui utilisent le cache sont sollicitées.

Une nouvelle base hébergée ne contient pas automatiquement les données de la base locale.

## Déploiement en ligne

| Composant | Hébergement |
|---|---|
| Frontend React | Render — Static Site |
| Backend Spring Boot | Render — Web Service Docker |
| MySQL | Aiven |
| Cache Redis compatible | Render — Key Value / Valkey |

### Configuration du backend Render

| Paramètre | Valeur |
|---|---|
| Branche | `main` |
| Région | Frankfurt |
| Root Directory | Vide |
| Dockerfile Path | `backend/healthCareQualite/Dockerfile` |
| Docker Build Context Directory | `backend/healthCareQualite` |

Variables à configurer selon l’environnement :

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC MySQL de la base hébergée |
| `SPRING_DATASOURCE_USERNAME` | Utilisateur MySQL |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe MySQL |
| `SPRING_DATA_REDIS_URL` | URL interne du service Key Value |
| `APP_JWT_SECRET` | Secret JWT propre au déploiement |

Exemple de structure d’URL MySQL :

```text
jdbc:mysql://HOTE:PORT/BASE?sslMode=REQUIRED&serverTimezone=UTC
```

Le backend et le service Key Value doivent être dans le même workspace et la même région pour utiliser la connexion interne Render.

### Configuration du frontend Render

| Paramètre | Valeur |
|---|---|
| Type | Static Site |
| Branche | `main` |
| Root Directory | `frontend/frontend-react` |
| Build Command | `npm ci && npm run build` |
| Publish Directory | `dist` |

Configurer les règles suivantes dans cet ordre :

| Source | Destination | Action |
|---|---|---|
| `/api/*` | `https://fil-rouge-mrx-health-quality-analytics.onrender.com/api/*` | Rewrite |
| `/*` | `/index.html` | Rewrite |

La première règle transmet les appels à l’API. La seconde permet d’accéder directement aux routes React et de rafraîchir leurs pages.

## Tests et qualité du code

### Backend

Depuis `backend/healthCareQualite`, avec les dépendances nécessaires au profil de test disponibles :

```bash
mvn -B -Dspring.profiles.active=test clean verify
```

Le workflow CI fournit notamment un service Redis pour cette vérification.

### Frontend

Depuis `frontend/frontend-react` :

```bash
npm ci
npm run lint
npm run build
```

### GitHub Actions

Le workflow `ci.yml` réalise :

1. La compilation et les tests du backend.
2. Le lint et la compilation du frontend.
3. La construction des images Docker après réussite des vérifications précédentes.

Le workflow `qodana.yml` analyse le code du backend et produit un rapport consultable dans les artefacts GitHub Actions.

Un workflow Qodana réussi ne signifie pas nécessairement qu’aucune remarque n’a été détectée.

La construction Docker du backend utilise `-DskipTests` : elle ne remplace pas l’exécution séparée des tests Maven.

## Commandes Docker utiles

Afficher l’état des services :

```bash
docker compose ps
```

Consulter les logs :

```bash
docker compose logs -f backend
docker compose logs -f frontend
```

Vérifier Redis :

```bash
docker compose exec redis redis-cli ping
```

Réponse attendue :

```text
PONG
```

Arrêter puis redémarrer les conteneurs :

```bash
docker compose stop
docker compose start
```

Supprimer les conteneurs et le réseau du projet :

```bash
docker compose down
```

Cette dernière commande conserve le volume MySQL. L’ajout de l’option `-v` supprime également les volumes et leurs données.

## Dépannage

| Symptôme | Vérification |
|---|---|
| `Communications link failure` | Adresse, port, identifiants et disponibilité de MySQL |
| `Unable to connect to Redis` | Disponibilité de Redis et configuration de sa connexion |
| Erreur HTTP 401 | Présence et validité du token JWT |
| Erreur HTTP 403 | Autorisations du compte connecté |
| Erreur HTTP 500 | Logs backend, notamment l’exception et les lignes `Caused by:` |
| Page React introuvable après actualisation | Règle `/*` vers `/index.html` |
| Frontend visible mais appels API en échec | Règle `/api/*`, statut HTTP et réponse de l’API |
| Première requête lente sur Render | Redémarrage éventuel de l’instance gratuite |

## Configuration avant exposition publique

- Utiliser un secret JWT propre au déploiement.
- Remplacer les identifiants de démonstration éventuellement initialisés par le backend.
- Garder les mots de passe dans les variables d’environnement.
- Ne pas publier de tokens ou d’identifiants dans les captures et les logs.
- Utiliser des données fictives pour les démonstrations.
- Prévoir des sauvegardes adaptées pour les données à conserver.

## Documentation complémentaire

- [Documentation backend](backend/healthCareQualite/README.md)
- [Documentation frontend](frontend/frontend-react/README.md)
- [Documents et maquettes](docs/)
- [Workflows GitHub Actions](.github/workflows/)