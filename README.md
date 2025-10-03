
-----

# MyCampus Companion 🎓

`MyCampus Companion` est une application Android native développée dans le cadre du cours de développement mobile du Master 2.  L'objectif est d'offrir aux étudiants un outil centralisant plusieurs services essentiels liés à la vie sur le campus. 

## ✨ Fonctionnalités

L'application implémente les quatre modules principaux décrits dans le cahier des charges :

* **📰 Actualités :** Consultation des actualités du campus via une API REST, avec un cache local SQLite pour un accès hors-ligne. 
* **📞 Annuaire :** Accès à un répertoire de contacts avec la possibilité de lancer un appel ou d'envoyer un SMS directement depuis l'application. 
* **🗺️ Géolocalisation :** Affichage de la position actuelle de l'utilisateur sur une carte, ainsi qu'un point d'intérêt fixe (la bibliothèque de l'ESATIC). 
* **📸 Signalement Multimédia :** Permet à un utilisateur de signaler un incident en créant un "ticket" contenant un titre, une description, une photo et les coordonnées GPS du lieu. 

## 🏗️ Architecture Technique

Le projet est construit sur une architecture **MVVM (Model-View-ViewModel)**, comme recommandé par Google et le cahier des charges, afin de garantir une séparation claire des responsabilités, une bonne testabilité et une maintenance facilitée.

Le flux de données suit le schéma suivant :

**Vue (Écran Compose) ➡️ ViewModel ➡️ Repository ➡️ Sources de Données (API / Base de données)**

* **Vue (`@Composable`) :** La couche d'interface utilisateur. Elle est "bête" : son seul rôle est d'afficher les données fournies par le ViewModel et de lui remonter les actions de l'utilisateur (clics, etc.).
* **ViewModel :** Le "cerveau" de la Vue. Il contient la logique de présentation, prépare les données pour l'affichage et réagit aux actions de l'utilisateur. Il ne sait pas *d'où* viennent les données, il les demande simplement au Repository.
* **Repository (non implémenté, mais partie de l'architecture cible) :** Le "chef d'orchestre" des données. Il centralise l'accès aux données et décide s'il doit les chercher sur le réseau (API) ou dans le cache local (base de données). Nos ViewModels actuels jouent ce rôle de manière simplifiée.
* **Sources de Données (Model) :**
    * **Distante :** L'API REST, interrogée avec **Retrofit**.
    * **Locale :** La base de données SQLite, gérée avec **Room**.

## 📁 Structure des Fichiers

Le projet est organisé par couches (`data`, `ui`) puis par fonctionnalités pour une meilleure lisibilité.

```
com.example.mycampuscompanion
│
├── data/                  //  LAYER: MODEL - Gestion des données
│   ├── local/             // Sources de données locales (Base de données)
│   │   ├── AppDatabase.kt   // Classe principale de la base de données Room
│   │   ├── PostDao.kt       // DAO pour les actualités
│   │   └── ReportDao.kt     // DAO pour les signalements
│   │
│   ├── model/             // Modèles de données (data classes)
│   │   ├── Contact.kt       // Structure d'un contact
│   │   ├── Post.kt          // Structure d'une actualité (Entité Room)
│   │   └── Report.kt        // Structure d'un signalement (Entité Room)
│   │
│   └── remote/            // Source de données distante (Réseau)
│       └── ApiService.kt    // Interface Retrofit pour les appels API
│
└── ui/                    // LAYER: VIEW & VIEWMODEL - Interface & logique UI
    ├── features/          // Un package par fonctionnalité
    │   ├── directory/
    │   │   └── AnnuaireScreen.kt  // VUE: Écran de l'annuaire
    │   ├── map/
    │   │   ├── MapScreen.kt       // VUE: Écran de la carte
    │   │   └── MapViewModel.kt    // VIEWMODEL: Logique de la carte
    │   ├── news/
    │   │   ├── NewsScreen.kt      // VUE: Écran des actualités
    │   │   └── NewsViewModel.kt   // VIEWMODEL: Logique des actualités
    │   └── reporting/
    │       ├── AddReportScreen.kt   // VUE: Écran d'ajout de signalement
    │       ├── ReportingListScreen.kt // VUE: Écran de la liste des signalements
    │       └── ReportingViewModel.kt  // VIEWMODEL: Partagé pour toute la fonctionnalité
    │
    ├── theme/             // Thème de l'application (couleurs, polices...)
    └── MainActivity.kt    // Point d'entrée, gère la navigation principale
```

## 🛠️ Technologies et Bibliothèques

* **Langage :** [Kotlin](https://kotlinlang.org/) (au lieu de Java, un choix motivé par les recommandations actuelles de Google pour le développement Android).
* **Interface Utilisateur :** [Jetpack Compose](https://developer.android.com/jetpack/compose) pour une UI déclarative et moderne.
* **Architecture :** MVVM (Model-View-ViewModel). 
* **Asynchronisme :** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) pour gérer les opérations en arrière-plan (réseau, base de données).
* **Réseau :** [Retrofit](https://square.github.io/retrofit/) pour les appels à l'API REST  et [Gson](https://github.com/google/gson) pour la conversion JSON. 
* **Base de Données :** [Room](https://developer.android.com/jetpack/androidx/releases/room) pour la persistance des données SQLite. 
* **Navigation :** [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) pour gérer la navigation entre les écrans.
* **Cartographie :** [osmdroid](https://github.com/osmdroid/osmdroid) pour l'affichage des cartes OpenStreetMap (choisi comme équivalent au Google Maps SDK ).
* **Chargement d'images :** [Coil](https://coil-kt.github.io/coil/) pour charger et afficher les images de manière asynchrone.
* **Gestion des permissions et activités :** [Activity Result APIs](https://developer.android.com/training/basics/intents/result).

-----