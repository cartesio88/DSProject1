import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SubscriptionsRegister {
	/* Linked structure: Type -> Originator -> Org -> Clients */
	HashMap register;

	public SubscriptionsRegister() {
		initStructure();
	}

	public void subscribeClient(Article subscription, ClientRecord client) {
		/* Look for type */
		HashMap originatorRegister = (HashMap) register.get(subscription
				.getType());
		if (originatorRegister == null) { /* Specified type does not exist */
			System.out.println("ERROR Subscribing the client: " + client
					+ ". Invalid subscription type.");
		}

		/* Look for originator */
		HashMap orgRegister = (HashMap) originatorRegister.get(subscription
				.getOriginator());
		if (orgRegister == null) { /*
									 * Originator does not exist, create the
									 * entry
									 */
			orgRegister = new HashMap();
			originatorRegister.put(subscription.getOriginator(), orgRegister);
		}

		/* Look for Org */
		LinkedList<ClientRecord> clientsRegister = (LinkedList<ClientRecord>) orgRegister.get(subscription.getOrg());
		if (clientsRegister == null) { /*
										 * Originator does not exist, create the
										 * entry
										 */
			clientsRegister = new LinkedList();
			orgRegister.put(subscription.getOrg(), orgRegister);
		}

		/* Store client */
		if (!clientsRegister.add(client)) {
			System.out
					.println("ERROR Storing the client in the register. Maybe there is not enough free memory.");
		}
	}

	public void unsubscribeClient(Article subscription, ClientRecord client) {
		/* Look for type */
		HashMap originatorRegister = (HashMap) register.get(subscription
				.getType());
		if (originatorRegister == null) { /* Specified type does not exist */
			System.out.println("ERROR Unubscribing the client: " + client
					+ ". Invalid subscription type.");
		}

		/* Look for originator */
		HashMap orgRegister = (HashMap) originatorRegister.get(subscription
				.getOriginator());
		if (orgRegister == null) { 
			System.out.println("ERROR Unsubscribing client 1!: "+client);
		}

		/* Look for Org */
		@SuppressWarnings("unchecked")
		LinkedList<ClientRecord> clientsRegister = (LinkedList<ClientRecord>) orgRegister.get(subscription.getOrg());
		if (clientsRegister == null) { 
			System.out.println("ERROR Unsubscribing client 2!: "+client);
		}

		/* Store client */
		if (!clientsRegister.remove(client)) {
			System.out.println("ERROR Unsubscribing client 3!: "+client);
		}
	}
	
	public LinkedList<ClientRecord> getClients(Article article) {
		LinkedList<ClientRecord> clients = new LinkedList<ClientRecord>();

		/* The article is always sent to those clients who are subscribed to everything */
		HashMap originatorRegister = (HashMap) register.get("all");
		if(originatorRegister != null){
			clients = getClientsFromType(originatorRegister, article);	
		}		
		
		if(!article.getType().equals("all")){
			originatorRegister = (HashMap) register.get(article.getType());
			clients.addAll(getClientsFromType(originatorRegister, article));
		}
		
		return clients;
	}

	private LinkedList<ClientRecord> getClientsFromType(HashMap originatorRegister, Article article) {
		LinkedList<ClientRecord> clients = new LinkedList<ClientRecord>();

		/* The article is always sent to those clients who are subscribed to everything */
		HashMap orgRegister = (HashMap) originatorRegister.get("all");
		if(orgRegister != null){
			clients = getClientsFromType(orgRegister, article);	
		}		
		
		if(!article.getOriginator().equals("all")){
			orgRegister = (HashMap) originatorRegister.get(article.getOriginator());
			clients.addAll(getClientsFromOriginator(originatorRegister, article));
		}

		return clients;
	}

	private LinkedList<ClientRecord> getClientsFromOriginator(HashMap orgRegister, Article article) {
		LinkedList<ClientRecord> clients = new LinkedList<ClientRecord>();

		/* The article is always sent to those clients who are subscribed to everything */
		LinkedList<ClientRecord>  orgClients = (LinkedList<ClientRecord>) orgRegister.get("all");
		if(orgRegister != null){
			clients.addAll(orgClients);
		}
		
		
		if(!article.getOrg().equals("all")){
			orgClients = (LinkedList<ClientRecord>) orgRegister.get(article.getOrg());
			clients.addAll(orgClients);
		}

		return clients;
	}

	
	private void initStructure() {
		/* Init types */
		register.put("sports", new HashMap());
		register.put("lifestyle", new HashMap());
		register.put("entertainment", new HashMap());
		register.put("business", new HashMap());
		register.put("technology", new HashMap());
		register.put("science", new HashMap());
		register.put("politics", new HashMap());
		register.put("health", new HashMap());
		register.put("all", new HashMap());
	}
}
