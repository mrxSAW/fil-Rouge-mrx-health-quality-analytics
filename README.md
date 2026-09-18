# fil-Rouge-mrx-health-quality-analytics


# Health Quality Analytics

Application de suivi de la qualité des soins : incidents, audits,
actions correctives, admissions mensuelles, statistiques et rapports.

## Technologies

- Backend : Java 21, Spring Boot 3.5.5, Spring Security et JWT.
- Base de données : MySQL 8 et migrations Flyway.
- Cache : Redis.
- Frontend : React, Vite, Axios, React Router et Recharts.
- Rapports : PDFBox et Apache POI.
- Exécution : Docker Compose et Nginx.

## Organisation

- `backend/healthCareQualite` : API Spring Boot.
- `frontend/frontend-react` : application React.
- `docs` : documentation et maquettes.
- `compose.yaml` : lancement des quatre services.

## Lancement avec Docker

Installer et démarrer Docker Desktop avec les conteneurs Linux.

Depuis la racine du dépôt, dans PowerShell :

```powershell
Copy-Item .env.example .env
```

Personnaliser les mots de passe et le secret JWT dans `.env`.
Ce fichier ne doit pas être versionné.

Vérifier la configuration et lancer :

```powershell
docker compose config --quiet
docker compose up --build -d
docker compose ps
docker compose logs -f backend
```

Attendre le message de démarrage Spring Boot avant de se connecter.
Quitter l'affichage des logs avec Ctrl+C ne coupe pas les services.

## Adresses

- Application : http://localhost:3000
- Swagger : http://localhost:8081/swagger-ui.html
- API : http://localhost:8081/api

Le navigateur appelle `/api` sur le frontend.
Nginx transmet ces requêtes au backend.

## Première connexion locale

Le code actuel initialise ce compte sur une base neuve :

- Email : `admin@healthquality.ma`
- Mot de passe : `1234`

Ce compte est destiné à la démonstration locale.
Son initialisation doit être adaptée avant une mise en ligne.

Les inscriptions créent des comptes STAFF.
L'administrateur peut ensuite modifier leur rôle.

## Rôles

- ADMIN : administration et accès aux modules.
- QUALITY_MANAGER : audits, admissions, statistiques et rapports.
- QHSE_MANAGER : actions correctives, statistiques et rapports.
- STAFF : ses incidents et les actions qui lui sont attribuées.

Les contrôles du backend complètent les restrictions de navigation.

## Données et migrations

Docker utilise une nouvelle base MySQL dans un volume nommé.
Les données du MySQL installé sur le poste ne sont pas importées.

Flyway applique les migrations sur la base neuve.
Hibernate vérifie le schéma avec `ddl-auto=validate`.

Redis contient un cache temporaire qui peut être reconstruit.

## Commandes utiles

```powershell
docker compose logs -f backend
docker compose logs -f frontend
docker compose exec redis redis-cli ping
docker compose stop
docker compose start
docker compose down
```

`docker compose down` conserve le volume MySQL.
L'option `-v` supprime les volumes et donc les données MySQL.

## Vérifications

- Frontend : `npm run lint` et `npm run build`.
- Backend : tests Maven avec le profil Spring `test`.
- Parcours : connexion, droits, CRUD, statistiques et téléchargements.

La construction Docker du backend saute l'exécution des tests.
Elle ne remplace pas leur lancement séparé.

## Documentation détaillée

- [Backend](backend/healthCareQualite/README.md)
- [Frontend](frontend/frontend-react/README.md)