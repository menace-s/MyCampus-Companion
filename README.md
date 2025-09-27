
---

# MyCampus Companion

`MyCampus Companion` est une application Android native développée en Kotlin avec Jetpack Compose. Ce projet a pour but de mettre en pratique les concepts fondamentaux et avancés du développement Android moderne, de l'affichage de listes simples à la communication avec des serveurs distants.

## 🚀 Modules Réalisés (Jusqu'à la fin du Module 2A)

Voici les étapes clés qui ont été franchies pour construire les fonctionnalités actuelles de l'application.

---

### ### Module 1 : Annuaire et Téléphonie 📞

Ce module se concentre sur l'affichage d'une liste de données locales et l'interaction avec les fonctionnalités natives du téléphone.

1.  **Modélisation des Données (`Contact.kt`)**
    * **Description :** Création d'une `data class` Kotlin pour définir la structure d'un contact (id, nom, numéro, etc.). C'est le "moule" pour nos données.

2.  **Création de l'Interface Utilisateur (`AnnuaireScreen.kt`)**
    * **Description :** Mise en place d'un écran avec Jetpack Compose. Utilisation d'une `LazyColumn` pour afficher la liste des contacts de manière performante, en ne créant que les éléments visibles à l'écran.

3.  **Composants Réutilisables (`ContactCard.kt`)**
    * **Description :** Conception d'un Composable dédié à l'affichage d'un seul contact. Cette approche permet de garder le code propre et de le réutiliser facilement.

4.  **Implémentation de l'Interactivité**
    * **Description :** Utilisation des `IconButton` pour rendre les éléments cliquables. Déclenchement des `Intents` Android (`ACTION_DIAL` et `ACTION_SENDTO`) pour ouvrir les applications natives de téléphone et de messagerie avec les informations du contact pré-remplies.

---

### ### Module 2A : Actualités en Ligne (Networking) 📰

Cette partie du module 2 se concentre sur la récupération de données depuis une API REST distante et leur affichage dans une interface réactive.

1.  **Configuration du Client Réseau**
    * **Description :** Ajout de la permission `INTERNET` dans l'`AndroidManifest.xml`. Intégration de la bibliothèque **Retrofit** dans le projet via le fichier `build.gradle.kts` pour gérer les appels HTTP.

2.  **Définition de l'API (`ApiService.kt`)**
    * **Description :** Création d'une `data class` `Post` correspondant à la structure du JSON reçu. Écriture d'une `interface` Kotlin avec des annotations Retrofit (`@GET`) pour définir les "endpoints" de l'API à interroger.

3.  **Mise en place de l'Architecture MVVM**
    * **Description :** Création d'un **ViewModel** (`NewsViewModel.kt`) pour servir de "cerveau" à notre écran. Ce ViewModel est responsable de la logique métier : appeler l'API, gérer les données et exposer l'état de l'interface.

4.  **Gestion des États de l'Interface**
    * **Description :** Utilisation d'une `sealed interface` (`NewsState`) pour modéliser les différents états possibles de l'écran (Chargement, Succès, Erreur). Le ViewModel expose cet état via un `StateFlow`.

5.  **Affichage Réactif des Données (`NewsScreen.kt`)**
    * **Description :** L'interface utilisateur observe (`collectAsStateWithLifecycle`) l'état du ViewModel. Elle se "redessine" automatiquement pour afficher un indicateur de chargement, un message d'erreur ou la liste des actualités en fonction de l'état actuel.

---

### ### Prochaine Étape (Module 2B) : Cache Hors-Ligne avec Room/SQLite 💾

La prochaine étape consistera à finaliser le module 2 en ajoutant une base de données locale pour que l'application puisse fonctionner sans connexion internet.