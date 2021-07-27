# DEMO Akka actors

https://hpi.de/fileadmin/user_upload/fachgebiete/naumann/lehre/WS2019/DDM/03_DDM_Hands-on_Akka_Actor_Programming.pdf

In this demo we have a master actor system that holds a master actor.

In this example we do prime number calculation. The master will get a range of integers, it will break down this range
in many tasks and send them to worker actors.

## Construction du projet

```
mvn clean package
```

## Lancement des système maitre et esclave en mode nominal
```
java -jar target/akka-tutorial-0.0.1-SNAPSHOT.jar master -h 192.168.1.17
```
Une fois cette commande exécuté on doit voir apparaître un prompt demandant une plage de recherche.
On saisi 1,1000000

[22:58:35.806  INFO]                                    local| Slf4jLogger started
[22:58:35.856  INFO]                     akka.remote.Remoting| Starting remoting
[22:58:42.937  INFO]                     akka.remote.Remoting| Remoting started; listening on addresses :[akka.tcp://MasterActorSystem@192.168.1.17:7877]
[22:58:42.939  INFO]                     akka.remote.Remoting| Remoting now listens on addresses: [akka.tcp://MasterActorSystem@192.168.1.17:7877]
[22:58:54.955  INFO] ctorSystem@192.168.1.17:7877/user/reaper| Started Actor[akka://MasterActorSystem/user/reaper#1814731464]...
[22:58:54.967  INFO] ctorSystem@192.168.1.17:7877/user/reaper| Started watching Actor[akka://MasterActorSystem/user/listener#1733682590].
[22:58:54.969  INFO] ctorSystem@192.168.1.17:7877/user/master| Started Actor[akka://MasterActorSystem/user/master#202310331]...
[22:58:54.969  INFO] ctorSystem@192.168.1.17:7877/user/reaper| Started watching Actor[akka://MasterActorSystem/user/master#202310331].
[22:58:54.971  INFO] ctorSystem@192.168.1.17:7877/user/reaper| Started watching Actor[akka://MasterActorSystem/user/shepherd#-1674919368].
> Enter ...
  "<min>,<max>" to analyze for primes,
  "all" to log all calculated primes,
  "max" to log the largest calculated prime,
  "exit" for a graceful shutdown,
  "kill" for a hard shutdown:
1,1000000

Par lui-même le master ne sait rien faire, on doit donc lancer un système esclave dans une autre invite de commande:

```
java -jar target/akka-tutorial-0.0.1-SNAPSHOT.jar slave -m 192.168.1.17
```
On passe au système esclave, l'addresse ip du système maitre afin qu'il puisse lui envoyer un message de souscription.
Après l'éxécution de cette commande, on peut voir que le système esclave à bien envoyé ce message :

[23:04:16.073  INFO] orSystem@192.168.1.17:7877/user/shepherd| New subscription: Actor[akka.tcp://SlaveActorSystem@192.168.1.17:7879/user/slave#-1172169471]
[23:04:16.084  INFO] ctorSystem@192.168.1.17:7877/user/master| New worker: Actor[akka.tcp://SlaveActorSystem@192.168.1.17:7879/remote/akka.tcp/MasterActorSystem@192.168.1.17:7877/user/ma
ster/$a#-848643369]

Et les calculs sont bien réalisés par le système esclave:

[23:04:15.071  INFO]                                    local| Slf4jLogger started
[23:04:15.108  INFO]                     akka.remote.Remoting| Starting remoting
[23:04:15.456  INFO]                     akka.remote.Remoting| Remoting started; listening on addresses :[akka.tcp://SlaveActorSystem@192.168.1.17:7879]
[23:04:15.459  INFO]                     akka.remote.Remoting| Remoting now listens on addresses: [akka.tcp://SlaveActorSystem@192.168.1.17:7879]
[23:04:15.615  INFO] ctorSystem@192.168.1.17:7879/user/reaper| Started Actor[akka://SlaveActorSystem/user/reaper#1117036229]...
[23:04:15.622  INFO] ctorSystem@192.168.1.17:7879/user/reaper| Started watching Actor[akka://SlaveActorSystem/user/slave#-1172169471].
[23:04:16.107  INFO] ActorSystem@192.168.1.17:7879/user/slave| Subscription successfully acknowledged by Actor[akka.tcp://MasterActorSystem@192.168.1.17:7877/user/shepherd#-1674919368].
[23:04:16.146  INFO] ctorSystem@192.168.1.17:7879/user/reaper| Started watching Actor[akka://SlaveActorSystem/remote/akka.tcp/MasterActorSystem@192.168.1.17:7877/user/master/$a#-84864336
9].
[23:04:16.158  INFO] rSystem@192.168.1.17:7877/user/master/$a| Started discovering primes in [1,100000] ...
[23:04:16.312  INFO] rSystem@192.168.1.17:7877/user/master/$a| Started discovering primes in [100001,200000] ...
[23:04:16.513  INFO] rSystem@192.168.1.17:7877/user/master/$a| Started discovering primes in [200001,300000] ...
[23:04:16.570  INFO] rSystem@192.168.1.17:7877/user/master/$a| Started discovering primes in [300001,400000] ...
[23:04:16.640  INFO] rSystem@192.168.1.17:7877/user/master/$a| Started discovering primes in [400001,500000] ...
[23:04:16.693  INFO] rSystem@192.168.1.17:7877/user/master/$a| Started discovering primes in [500001,600000] ...
[23:04:16.756  INFO] rSystem@192.168.1.17:7877/user/master/$a| Started discovering primes in [600001,700000] ...
[23:04:16.818  INFO] rSystem@192.168.1.17:7877/user/master/$a| Started discovering primes in [700001,800000] ...
[23:04:16.879  INFO] rSystem@192.168.1.17:7877/user/master/$a| Started discovering primes in [800001,900000] ...
[23:04:16.948  INFO] rSystem@192.168.1.17:7877/user/master/$a| Started discovering primes in [900001,1000000] ...



## Lancement du système maitre avec des acteurs dans le master actor system

Dans une première invite de commande, lancer un master avec 2 workers locaux :
```
java -jar target/akka-tutorial-0.0.1-SNAPSHOT.jar master -s reactive -w 2

java -jar target/akka-tutorial-0.0.1-SNAPSHOT.jar master --scheduler reactive --workers 2
```

D:\DevHome\Akka\akka-tutorial\akka-tutorial>java -jar target/akka-tutorial-0.0.1-SNAPSHOT.jar master --scheduler reactive --workers 2
[23:07:34.383  INFO]                                    local| Slf4jLogger started
[23:07:34.489  INFO]                     akka.remote.Remoting| Starting remoting
[23:07:34.717  INFO]                     akka.remote.Remoting| Remoting started; listening on addresses :[akka.tcp://MasterActorSystem@192.168.43.168:7877]
[23:07:34.719  INFO]                     akka.remote.Remoting| Remoting now listens on addresses: [akka.tcp://MasterActorSystem@192.168.43.168:7877]
[23:07:34.749  INFO] orSystem@192.168.43.168:7877/user/reaper| Started Actor[akka://MasterActorSystem/user/reaper#560366612]...
[23:07:34.750  INFO] orSystem@192.168.43.168:7877/user/reaper| Started watching Actor[akka://MasterActorSystem/user/listener#-530558349].
[23:07:34.791  INFO] orSystem@192.168.43.168:7877/user/reaper| Started watching Actor[akka://MasterActorSystem/user/master/$a#-814153514].
[23:07:34.792  INFO] orSystem@192.168.43.168:7877/user/reaper| Started watching Actor[akka://MasterActorSystem/user/shepherd#1613598013].
[23:07:34.793  INFO] orSystem@192.168.43.168:7877/user/reaper| Started watching Actor[akka://MasterActorSystem/user/master/$b#1811105818].
[23:07:34.793  INFO] orSystem@192.168.43.168:7877/user/reaper| Started watching Actor[akka://MasterActorSystem/user/master#1569304296].
> Enter ...
  "<min>,<max>" to analyze for primes,
  "all" to log all calculated primes,
  "max" to log the largest calculated prime,
  "exit" for a graceful shutdown,
  "kill" for a hard shutdown:


## Lancement du système esclave

Dans une autre invite de commande lancer un esclave en le connectant au master :
```
java -jar target/akka-tutorial-0.0.1-SNAPSHOT.jar slave -m 192.168.43.168:7877

java -jar target/akka-tutorial-0.0.1-SNAPSHOT.jar slave --master 192.168.43.168:7877
```

D:\DevHome\Akka\akka-tutorial\akka-tutorial>java -jar target/akka-tutorial-0.0.1-SNAPSHOT.jar slave -m 192.168.43.168:7877
[23:10:39.800  INFO]                                    local| Slf4jLogger started
[23:10:39.901  INFO]                     akka.remote.Remoting| Starting remoting
[23:10:40.123  INFO]                     akka.remote.Remoting| Remoting started; listening on addresses :[akka.tcp://SlaveActorSystem@192.168.43.168:7879]
[23:10:40.124  INFO]                     akka.remote.Remoting| Remoting now listens on addresses: [akka.tcp://SlaveActorSystem@192.168.43.168:7879]
[23:10:40.168  INFO] orSystem@192.168.43.168:7879/user/reaper| Started Actor[akka://SlaveActorSystem/user/reaper#1716430548]...
[23:10:40.179  INFO] orSystem@192.168.43.168:7879/user/reaper| Started watching Actor[akka://SlaveActorSystem/user/slave#-1860519194].
[23:10:40.625  INFO] torSystem@192.168.43.168:7879/user/slave| Subscription successfully acknowledged by Actor[akka.tcp://MasterActorSystem@192.168.43.168:7877/user/shepherd#1613598013].
[23:10:40.688  INFO] orSystem@192.168.43.168:7879/user/reaper| Started watching Actor[akka://SlaveActorSystem/remote/akka.tcp/MasterActorSystem@192.168.43.168:7877/user/master/$c#-307253192].


## Lancer le calcul

Dans la première invite, saisir ensuite 2 chiffres (ex: 1,10000000) pour lancer le calcul.

Le système esclave est notifié des calculs du système maitre.