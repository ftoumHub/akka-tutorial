# DEMO Akka actors

https://hpi.de/fileadmin/user_upload/fachgebiete/naumann/lehre/WS2019/DDM/03_DDM_Hands-on_Akka_Actor_Programming.pdf

## Construction du projet

```
mvn package
```

## Lancement du système maitre

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