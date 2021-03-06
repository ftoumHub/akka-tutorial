package de.hpi.akka_tutorial.remote;

import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.typesafe.config.Config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.PoisonPill;
import de.hpi.akka_tutorial.remote.actors.Listener;
import de.hpi.akka_tutorial.remote.actors.Master;
import de.hpi.akka_tutorial.remote.actors.Reaper;
import de.hpi.akka_tutorial.remote.actors.Shepherd;
import de.hpi.akka_tutorial.remote.actors.Slave;
import de.hpi.akka_tutorial.remote.actors.scheduling.SchedulingStrategy;
import de.hpi.akka_tutorial.remote.messages.ShutdownMessage;
import de.hpi.akka_tutorial.util.AkkaUtils;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import static akka.actor.ActorRef.noSender;

public class Calculator {

	private static final String DEFAULT_MASTER_SYSTEM_NAME = "MasterActorSystem";
	private static final String DEFAULT_SLAVE_SYSTEM_NAME = "SlaveActorSystem";

	public static void runMaster(String host, int port, SchedulingStrategy.Factory schedulingStrategyFactory, int numLocalWorkers) {
		
		// Create the ActorSystem
		final Config config = AkkaUtils.createRemoteAkkaConfig(host, port);
		final ActorSystem masterActorSystem = ActorSystem.create(DEFAULT_MASTER_SYSTEM_NAME, config);

		// Create actors in the master actor system : Reaper, Listener, Master and Shepherd
		masterActorSystem.actorOf(Reaper.props(), Reaper.DEFAULT_NAME);
		final ActorRef listener = masterActorSystem.actorOf(Listener.props(), Listener.DEFAULT_NAME);
		final ActorRef master = masterActorSystem.actorOf(Master.props(listener, schedulingStrategyFactory, numLocalWorkers),
				Master.DEFAULT_NAME);
		final ActorRef shepherd = masterActorSystem.actorOf(Shepherd.props(master), Shepherd.DEFAULT_NAME);

		// Enter interactive loop
		enterInteractiveLoop(listener, master, shepherd);
		
		System.out.println("Stopping...");

		// Await termination: The termination should be issued by the reaper
		awaitTermination(masterActorSystem);
	}
	
	private static void enterInteractiveLoop(final ActorRef listener, final ActorRef master, final ActorRef shepherd) {
		
		// Read ranges from the console and process them
		final Scanner scanner = new Scanner(System.in);
		while (true) {
			// Sleep to reduce mixing of log messages with the regular stdout messages.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			
			// Read input
			System.out.println("> Enter ...\n"
					+ "  \"<min>,<max>\" to analyze for primes,\n"
					+ "  \"all\" to log all calculated primes,\n"
					+ "  \"max\" to log the largest calculated prime,\n"
					+ "  \"exit\" for a graceful shutdown,\n"
					+ "  \"kill\" for a hard shutdown:");
			String line = scanner.nextLine();

			switch (line) {
				case "all": 
					listener.tell(new Listener.LogPrimesMessage(), noSender());
					break;
				case "max": 
					listener.tell(new Listener.LogMaxMessage(), noSender());
					break;
				case "exit":
					Calculator.shutdown(shepherd, master);
					scanner.close();
					return;
				case "kill":
					Calculator.kill(listener, master, shepherd);
					scanner.close();
					return;
				default:
					Calculator.processRange(line, master);
			}
		}
	}
	
	private static void shutdown(final ActorRef shepherd, final ActorRef master) {
		// Tell the master that we will not send any further requests and want to shutdown
		// the system after all current jobs finished
		master.tell(new ShutdownMessage(), noSender());
		
		// Do not accept any new subscriptions
		shepherd.tell(new ShutdownMessage(), noSender());
	}
	
	private static void kill(final ActorRef listener, final ActorRef master, final ActorRef shepherd) {
		listener.tell(PoisonPill.getInstance(), noSender()); // End the listener
		master.tell(PoisonPill.getInstance(), noSender()); // End the master
		shepherd.tell(PoisonPill.getInstance(), noSender()); // End the shepherd
	}
	
	private static void processRange(final String line, final ActorRef master) {
		// Check for correct range message
		String[] lineSplit = line.split(",");
		if (lineSplit.length != 2) {
			System.out.println("Invalid range format: " + line);
			return;
		}
		
		try {
			// Extract start- and endNumber
			long startNumber = Long.valueOf(lineSplit[0]);
			long endNumber = Long.valueOf(lineSplit[1]);
			
			// Start the calculation
			master.tell(new Master.RangeMessage(startNumber, endNumber), noSender());
		} catch (NumberFormatException e) {
			System.out.println("Invalid number format for range: " + line);
		}
	}
	
	public static void awaitTermination(final ActorSystem actorSystem) {
		try {
			Await.ready(actorSystem.whenTerminated(), Duration.Inf());
		} catch (TimeoutException | InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("ActorSystem terminated!");
	}

	public static void runSlave(String host, int port, String masterHost, int masterPort) {

		// Create the local ActorSystem
		final Config config = AkkaUtils.createRemoteAkkaConfig(host, port);
		final ActorSystem actorSystem = ActorSystem.create(DEFAULT_SLAVE_SYSTEM_NAME, config);
		
		// Create the reaper.
		actorSystem.actorOf(Reaper.props(), Reaper.DEFAULT_NAME);

		// Create a Slave
		final ActorRef slave = actorSystem.actorOf(Slave.props(), Slave.DEFAULT_NAME);

		// Tell the Slave to register the local ActorSystem
		slave.tell(new Slave.AddressMessage(new Address("akka.tcp", DEFAULT_MASTER_SYSTEM_NAME, masterHost, masterPort)), noSender());
		
		// Await termination: The termination should be issued by the reaper
		Calculator.awaitTermination(actorSystem);
	}

}

