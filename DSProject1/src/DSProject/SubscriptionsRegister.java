package DSProject;

import java.util.HashMap;
import java.util.LinkedList;

public class SubscriptionsRegister {
	/* Linked structure: Type -> Originator -> Org -> Clients */
	HashMap<String, HashMap<String, HashMap<String, LinkedList<HostRecord>>>> register;

	public SubscriptionsRegister() {
		initStructure();
	}

	public boolean subscribeClient(Article subscription, HostRecord client) {
		//System.out.println("Subscribing client " + client);

		/* Look for type */
		//System.out.println("Checking type...");
		HashMap<String, HashMap<String, LinkedList<HostRecord>>> originatorRegister = register.get(subscription
				.getType());

		if (originatorRegister == null) { /* Specified type does not exist */
			System.out.println("ERROR Subscribing the client: " + client
					+ ". Invalid subscription type.");
			return false;
		}

		
		/* Look for originator */
		HashMap<String, LinkedList<HostRecord>> orgRegister = originatorRegister.get(subscription
				.getOriginator());
		if (orgRegister == null) { /*
									 * Originator does not exist, create the
									 * entry
									 */
			orgRegister = new HashMap<String, LinkedList<HostRecord>>();
			originatorRegister.put(subscription.getOriginator(), orgRegister);

			System.out.println("Originator does not exists! Creating it... : "
					+ subscription.getOriginator());
		}

		/* Look for Org */
		LinkedList<HostRecord> clientsRegister = orgRegister
				.get(subscription.getOrg());
		if (clientsRegister == null) { /*
										 * Originator does not exist, create the
										 * entry
										 */
			clientsRegister = new LinkedList<HostRecord>();
			orgRegister.put(subscription.getOrg(), clientsRegister);

			System.out.println("Org does not exists! Creating it... : "
					+ subscription.getOrg());
		}

		//System.out.println("Adding client: " + client);

		/* Store client */
		if (!clientsRegister.add(client)) {
			System.out
					.println("ERROR Storing the client in the register. Maybe there is not enough free memory.");
			return false;
		}
		return true;
	}

	public boolean unsubscribeClient(Article subscription, HostRecord client) {
		/* Look for type */
		HashMap<String, HashMap<String, LinkedList<HostRecord>>> originatorRegister = register.get(subscription
				.getType());
		if (originatorRegister == null) { /* Specified type does not exist */
			System.out.println("ERROR Unubscribing the client: " + client
					+ ". Invalid subscription type.");
			return false;
		}

		/* Look for originator */
		HashMap<String, LinkedList<HostRecord>> orgRegister = (HashMap<String, LinkedList<HostRecord>>) originatorRegister.get(subscription
				.getOriginator());
		if (orgRegister == null) {
			System.out.println("ERROR Unsubscribing client!: " + client);
			return false;
		}

		/* Look for Org */
		LinkedList<HostRecord> clientsRegister = (LinkedList<HostRecord>) orgRegister
				.get(subscription.getOrg());
		if (clientsRegister == null) {
			System.out.println("ERROR Unsubscribing client!: " + client);
			return false;
		}

		/* Store client */
		if (!clientsRegister.remove(client)) {
			System.out.println("ERROR Unsubscribing client!: " + client);
			return false;
		}
		return true;
	}

	public LinkedList<HostRecord> getClients(Article article) {
		LinkedList<HostRecord> clients = new LinkedList<HostRecord>();

		/*
		 * The article is always sent to those clients who are subscribed to
		 * everything
		 */
		HashMap<String, HashMap<String, LinkedList<HostRecord>>> originatorRegister = register.get("all");
		if (originatorRegister != null) {
			clients = getClientsFromType(originatorRegister, article);
		}

		if (!article.getType().equals("all")) {
			originatorRegister = register.get(article.getType());
			clients.addAll(getClientsFromType(originatorRegister, article));
		}

		return clients;
	}

	private LinkedList<HostRecord> getClientsFromType(
			HashMap<String, HashMap<String, LinkedList<HostRecord>>> originatorRegister, Article article) {
		LinkedList<HostRecord> clients = new LinkedList<HostRecord>();

		if (originatorRegister == null)
			return clients;

		/*
		 * The article is always sent to those clients who are subscribed to
		 * everything
		 */
		HashMap<String, LinkedList<HostRecord>> orgRegister = (HashMap<String, LinkedList<HostRecord>>) originatorRegister.get("all");
		if (orgRegister != null) {
			clients = getClientsFromOriginator(orgRegister, article);
		}

		if (!article.getOriginator().equals("all")) {
			orgRegister = (HashMap<String, LinkedList<HostRecord>>) originatorRegister.get(article
					.getOriginator());
			clients.addAll(getClientsFromOriginator(orgRegister, article));
		}

		return clients;
	}

	private LinkedList<HostRecord> getClientsFromOriginator(
			HashMap<String, LinkedList<HostRecord>> orgRegister, Article article) {
		LinkedList<HostRecord> clients = new LinkedList<HostRecord>();

		if (orgRegister == null)
			return clients;

		/*
		 * The article is always sent to those clients who are subscribed to
		 * everything
		 */
		LinkedList<HostRecord> orgClients = (LinkedList<HostRecord>) orgRegister
				.get("all");
		if (orgRegister != null) {
			clients.addAll(orgClients);
		}

		if (!article.getOrg().equals("all")) {
			orgClients = (LinkedList<HostRecord>) orgRegister.get(article
					.getOrg());
			if (orgClients != null) {
				clients.addAll(orgClients);
			}
		}

		return clients;
	}

	private void initStructure() {
		register = new HashMap<String, HashMap<String, HashMap<String, LinkedList<HostRecord>>>>();
		/* Init types */
		register.put("sports", new HashMap<String, HashMap<String, LinkedList<HostRecord>>>());
		register.put("lifestyle", new HashMap<String, HashMap<String, LinkedList<HostRecord>>>());
		register.put("entertainment", new HashMap<String, HashMap<String, LinkedList<HostRecord>>>());
		register.put("business", new HashMap<String, HashMap<String, LinkedList<HostRecord>>>());
		register.put("technology", new HashMap<String, HashMap<String, LinkedList<HostRecord>>>());
		register.put("science", new HashMap<String, HashMap<String, LinkedList<HostRecord>>>());
		register.put("politics", new HashMap<String, HashMap<String, LinkedList<HostRecord>>>());
		register.put("health", new HashMap<String, HashMap<String, LinkedList<HostRecord>>>());
		register.put("all", new HashMap<String, HashMap<String, LinkedList<HostRecord>>>());
	}
}
