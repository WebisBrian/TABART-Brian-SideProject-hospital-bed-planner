# 🏥 Hospital Bed Planner

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Une application console Java conçue pour aider les gestionnaires de santé à affecter les patients aux lits disponibles dans un service hospitalier.

## 📋 Table des matières

- [Fonctionnalités](#-fonctionnalités)
- [Technologies](#-technologies)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Utilisation](#-utilisation)
- [Architecture](#-architecture)
- [Tests](#-tests)
- [Contribution](#-contribution)
- [Roadmap](#-roadmap)
- [Licence](#-licence)

## ✨ Fonctionnalités

- 🛏️ Gestion des lits d'hôpital (ajout, suppression, mise à jour)
- 👤 Gestion des patients (enregistrement, consultation)
- 📊 Attribution automatique des patients aux lits disponibles
- 🔍 Recherche et filtrage des lits par statut
- 📈 Statistiques d'occupation en temps réel
- 💾 Persistance des données avec MySQL

## 🛠️ Technologies

| Technologie | Version | Usage |
|------------|---------|-------|
| **Java** | 25 | Langage principal |
| **Maven** | 3.8+ | Gestion de build et dépendances |
| **MySQL** | 8.0+ | Base de données relationnelle |
| **JUnit Jupiter** | 6.0.1 | Framework de tests unitaires |
| **MySQL Connector/J** | 9.5.0 | Driver JDBC pour MySQL |

## 📦 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- **JDK 25** ou supérieur ([Télécharger](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.8+** ([Télécharger](https://maven.apache.org/download.cgi))
- **MySQL 8.0+** ([Télécharger](https://dev.mysql.com/downloads/mysql/))
- **Git** ([Télécharger](https://git-scm.com/downloads))

Vérifiez vos installations :

```bash
java -version
mvn -version
mysql --version
```

## 🚀 Installation

### 1. Cloner le repository

```bash
git clone https://github.com/WebisBrian/TABART-Brian-SideProject-hospital-bed-planner.git
cd TABART-Brian-SideProject-hospital-bed-planner
```

### 2. Configurer la base de données

Connectez-vous à MySQL et créez la base de données :

```sql
CREATE DATABASE hospital_bed_planner CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'hospital_user'@'localhost' IDENTIFIED BY 'votre_mot_de_passe';
GRANT ALL PRIVILEGES ON hospital_bed_planner.* TO 'hospital_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Initialiser le schéma de base de données

Exécutez le script SQL d'initialisation (à créer) :

```bash
mysql -u hospital_user -p hospital_bed_planner < src/main/resources/schema.sql
```

### 4. Compiler le projet

```bash
mvn clean install
```

## ⚙️ Configuration

### Fichier de configuration

Créez un fichier `src/main/resources/database.properties` :

```properties
# Configuration de la base de données
db.url=jdbc:mysql://localhost:3306/hospital_bed_planner?useSSL=false&serverTimezone=UTC
db.username=hospital_user
db.password=votre_mot_de_passe
db.driver=com.mysql.cj.jdbc.Driver

# Pool de connexions
db.pool.minSize=5
db.pool.maxSize=20
```

**⚠️ Important** : Ne commitez jamais ce fichier avec vos vrais identifiants. Utilisez `.gitignore` ou des variables d'environnement pour la production.

### Variables d'environnement (recommandé pour la production)

```bash
export DB_URL="jdbc:mysql://localhost:3306/hospital_bed_planner"
export DB_USERNAME="hospital_user"
export DB_PASSWORD="votre_mot_de_passe"
```

## 💻 Utilisation

### Lancer l'application

```bash
mvn exec:java -Dexec.mainClass="com.webisbrian.Main"
```

Ou après compilation :

```bash
java -cp target/hospital-bed-planner-1.0-SNAPSHOT.jar com.webisbrian.Main
```

### Menu principal

```
╔════════════════════════════════════════╗
║   HOSPITAL BED PLANNER - MENU          ║
╚════════════════════════════════════════╝

1. Afficher tous les lits
2. Ajouter un nouveau lit
3. Enregistrer un nouveau patient
4. Affecter un patient à un lit
5. Libérer un lit
6. Statistiques d'occupation
7. Quitter

Votre choix : _
```

### Exemples d'utilisation

#### Ajouter un lit

```
Entrez le numéro de chambre : 101
Entrez le type de lit (standard/intensive care) : standard
Lit ajouté avec succès !
```

#### Affecter un patient

```
Entrez l'ID du patient : 42
Lits disponibles :
- Lit #1 (Chambre 101) - Standard
- Lit #3 (Chambre 103) - Standard
Choisissez un lit : 1
Patient affecté avec succès au lit #1.
```

## 🏗️ Architecture

### Structure du projet

```
hospital-bed-planner/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/webisbrian/
│   │   │       ├── Main.java                 # Point d'entrée
│   │   │       ├── model/                    # Entités métier
│   │   │       │   ├── Bed.java
│   │   │       │   ├── Patient.java
│   │   │       │   └── Assignment.java
│   │   │       ├── dao/                      # Data Access Objects
│   │   │       │   ├── BedDAO.java
│   │   │       │   └── PatientDAO.java
│   │   │       ├── service/                  # Logique métier
│   │   │       │   ├── BedService.java
│   │   │       │   └── PatientService.java
│   │   │       └── util/                     # Utilitaires
│   │   │           ├── DatabaseConnection.java
│   │   │           └── IO.java
│   │   └── resources/
│   │       ├── database.properties           # Configuration DB
│   │       └── schema.sql                    # Schéma de la base
│   └── test/
│       └── java/                             # Tests unitaires
├── pom.xml                                   # Configuration Maven
├── README.md
└── .gitignore
```

### Diagramme de classes (simplifié)

```
┌─────────────┐       ┌──────────────┐       ┌─────────────┐
│   Patient   │       │     Bed      │       │ Assignment  │
├─────────────┤       ├──────────────┤       ├─────────────┤
│ - id        │       │ - id         │       │ - patientId │
│ - name      │◄──────│ - roomNumber │──────►│ - bedId     │
│ - condition │       │ - status     │       │ - startDate │
└─────────────┘       └──────────────┘       └─────────────┘
```

### Principes appliqués

- **Séparation des responsabilités** : DAO / Service / Présentation
- **Pattern DAO** pour l'accès aux données
- **Injection de dépendances** (manuelle)
- **SOLID principles**

## 🧪 Tests

### Lancer tous les tests

```bash
mvn test
```

### Lancer un test spécifique

```bash
mvn test -Dtest=BedServiceTest
```

### Rapport de couverture

```bash
mvn jacoco:report
# Ouvrir target/site/jacoco/index.html
```

## 🤝 Contribution

Les contributions sont les bienvenues ! Voici comment participer :

### 1. Forker le projet

```bash
git clone https://github.com/WebisBrian/TABART-Brian-SideProject-hospital-bed-planner.git
cd TABART-Brian-SideProject-hospital-bed-planner
```

### 2. Créer une branche de fonctionnalité

```bash
git checkout -b feature/ma-nouvelle-fonctionnalite
```

### 3. Commiter vos changements

```bash
git add .
git commit -m "feat: ajout de la fonctionnalité X"
```

Nous utilisons les [Conventional Commits](https://www.conventionalcommits.org/fr/) :
- `feat:` Nouvelle fonctionnalité
- `fix:` Correction de bug
- `docs:` Documentation
- `test:` Ajout de tests
- `refactor:` Refactoring

### 4. Pousser vers GitHub

```bash
git push origin feature/ma-nouvelle-fonctionnalite
```

### 5. Créer une Pull Request

Allez sur GitHub et créez une PR depuis votre branche vers `develop`.

### Code de conduite

- Écrire des tests pour toute nouvelle fonctionnalité
- Respecter les conventions de code Java
- Commenter le code complexe
- Mettre à jour la documentation

## 🗺️ Roadmap

### Version 1.0 (Actuelle)
- [x] Structure de base du projet
- [x] Configuration Maven et dépendances
- [ ] Modèles de données complets
- [ ] DAO et connexion MySQL
- [ ] Interface console basique

### Version 1.1 (Prochaine)
- [ ] Gestion complète des patients
- [ ] Algorithme d'affectation automatique
- [ ] Statistiques et rapports
- [ ] Tests unitaires (couverture > 80%)

### Version 2.0 (Future)
- [ ] Interface graphique (JavaFX)
- [ ] API REST (Spring Boot)
- [ ] Authentification et rôles
- [ ] Export PDF des rapports
- [ ] Notifications par email

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 👤 Auteur

**Brian TABART** ([@WebisBrian](https://github.com/WebisBrian))

## 📞 Support

Pour toute question ou suggestion :
- Ouvrir une [issue](https://github.com/WebisBrian/TABART-Brian-SideProject-hospital-bed-planner/issues)
- Contacter via GitHub

## 🙏 Remerciements

- Inspiré par les besoins réels de gestion hospitalière
- Merci à la communauté Java pour les outils et frameworks

---

⭐ Si ce projet vous est utile, n'hésitez pas à lui donner une étoile !

**Dernière mise à jour** : 19 novembre 2025