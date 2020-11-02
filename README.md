# Projet C2 2020 - Magasin En Ligne

### Documentation
* [Problématique](#problématique)
* [Idée et Avantages](#idée-et-avantages)
* [Microservices](#microservices)
* [Technologies](#technologies)
* [Utilisation](#utilisation)

##### Problématique
A cause de la propagation de virus corona (covid-19) et l'état économique mauvais, un grand nombre de personnes ont commencé à utiliser la plateforme WhatsApp afin de vendre des produits en ligne.

Le processus est le suivant:

Supposant que V(vendeur), C (client-qui est dans la liste des contacts de V), F(fournisseur) et X(produit)
1)	V publie des photos sur son "WhatsApp Status" pour que le plus grand nombre possible de C voient ces photos
2)	C souhaite acheter un de ces produits... se communique avec V directement et ordre X
3)	V s'il a X dans sa maison (qui est le stock) livre X à C qui a fait l'ordre. Et reçoit l'argent
4)	V s'il n’a pas X, il fait un ordre à F...  F livre X à V après un certain temps...V livre X à C qui a fait l'ordre et reçoit l'argent


Le processus est représenté par le diagramme ci-dessous:

![processus_1](/images/processus_1.png)

Les désavantages de ce processus sont:
1)	Les C possibles de V sont les contacts de ce dernier (c'est à dire le nombre est limité)
2)	La connexion est directe entre V et C
3)	V livre X à C, c'est à dire plus de cout sur la livraison
4)	Utilisation de WhatsApp doit tenir compte des client (différents âges et différents sexes) c'est à dire la publication des photos doit être bien dirigée

##### Idée et Avantages
Mon idée consiste de:
1)	Faire un magasin en ligne (une application)
2)	Ce magasin sauvegarde tous les vendeurs (on peut partager les vendeurs suivant la ville ou le village) dans la base de données
3)	Chaque vendeur s'identifie par un id, nom, prénom, téléphone, adresse...etc.
4)	La base de données sauvegarde en plus les produits de chaque vendeur
5)	Alors C (qui est un contact de V) entre à cette application, peut voir les produits de V, en plus il peut voir les produits de V1, V2... et tous les vendeurs
6)	C ordre X de V et X1 de V1
7)	La livraison se fait par l'exploitant(E) de l’application, E apporte X de V et X1 de V1 et les livre à C


Le processus est représenté par le diagramme ci-dessous:
![processus_2](/images/processus_2.png)
 


Les avantages de mon idée:
1)	Connexion entre C et V se termine…tous communiquent avec le magasin
2)	Plus de produits, plus des clients, plus des vendeurs, plus de compétition
3)	Le revenu de cet application (les charges sur la livraison) 
4)	La collection des data (les produits préfèrent des clients, alors qu'on peut diriger des publicités sur un échantillon des clients)
5)	Les regroupements et le filtrage des produits est intéressant pour les clients
6)	Utilisation d'une plateforme qui a une seule fonction
7)	La disponibilité d'améliorer l'application 1(le magasin fait un entrepôt pour les produits…alors le vendeur stock ses produits dans cet entrepôt)
8)	La disponibilité d'améliorer l'application 2(le magasin peut devenir un fournisseur pour les vendeurs, c'est à dire il peut être un vendeur avec le temps)


##### Microservices 
Les microservices utilisés sont:
1)	Microservice Vendeur: responsable de stocker les utilisateurs enregistrés comme vendeur
2)	Microservice Client: responsable de stocker les utilisateurs enregistrés comme client, stocker les produits préférés
3)	Microservice Produits: responsable de stocker, identifier, ajouter les produits pour un vendeur donné
4)	Microservice Ordre: responsable de faire un ordre de X, depuis V à C
5)	Microservice Livraison et Paiement: responsable de faire la livraison de X et paiement (cash on delivery)


##### Technologies
Les technologies utilisées sont :
1)	SpringBoot pour les services
2)	Android pour l’application mobile
3)	Accès aux données par Restfull Api

##### Utilisation
###### Note importante: le projet est en local (localhost), pour le démarrer il faut bien savoir connecter le cellulaire à la mashine local (localhost)
1) [entrer le mode developpeur](https://www.howtogeek.com/129728/how-to-access-the-developer-options-menu-and-enable-usb-debugging-on-android-4.2/#:~:text=To%20enable%20Developer%20Options%2C%20open,times%20to%20enable%20Developer%20Options.)
2) ouvrir [chrome inspect](chrome://inspect) puis suivre les étapes illustrées dans l'image ci-dessous
![connect_localhost](/images/connect_localhost.jpg)
3) installer [mongoDbCompass](https://www.mongodb.com/try/download/compass)
