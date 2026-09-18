# Frontend Health Quality Analytics

Interface React pour le suivi de la qualité des soins.

## Technologies

React, Vite, React Router, Axios, Recharts et CSS.

## Fonctionnalités

- Connexion et inscription.
- Navigation adaptée au rôle.
- Gestion des incidents, audits et actions correctives.
- Administration des utilisateurs et départements.
- Admissions mensuelles.
- Tableau de bord et graphiques.
- Téléchargement des rapports PDF et Excel.

## Organisation

- `src/pages` : pages de l'application.
- `src/components` : composants réutilisables.
- `src/services` : appels API.
- `src/api` : configuration Axios.
- `src/utils` : gestion de session.
- `src/index.css` : styles.

## Lancement local

Utiliser Node.js 22 à jour avec npm.
Démarrer le backend sur le port 8080.

Depuis ce dossier :

```powershell
npm ci
npm run dev
```

Ouvrir l'adresse indiquée par Vite, généralement :
http://localhost:5173

Vite transmet les requêtes `/api` au backend local sur le port 8080.

## Vérifications

```powershell
npm run lint
npm run build
```

Le build produit le dossier `dist`.

Un avertissement concernant la taille des fichiers JavaScript
ne signifie pas que la compilation a échoué.

## Lancement Docker

Depuis la racine du dépôt, après préparation de `.env` :

```powershell
docker compose up --build -d
```

Application : http://localhost:3000

Le Dockerfile compile React puis copie `dist` dans Nginx.
Nginx transmet `/api` au service backend et permet l'actualisation
des pages utilisant React Router.

## Authentification

Axios transmet le token JWT pour les appels protégés.
La session est conservée dans le stockage local du navigateur.

Les menus et routes dépendent du rôle.
Le backend reste responsable de l'autorisation des opérations.

## Vérifications manuelles

- Connexion et déconnexion.
- Accès direct à une page non autorisée.
- Création et modification des données selon le rôle.
- Filtres et pagination.
- Admissions et taux d'incidents.
- Téléchargement et ouverture des cinq rapports.