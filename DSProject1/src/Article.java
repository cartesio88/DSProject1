/* Encapsulates an article/subscription */
public class Article {
	final private int MAXSTRING = 120;

	private String type = "";
	private String originator = "";
	private String org = "";
	private String contents = "";

	private boolean validArticle = false;
	private boolean validSubscription = false;
	private boolean legalArticle = true;
	private String raw;

	public Article(String rawArticle) {
		raw = rawArticle;

		getFields(rawArticle);

		/* Is the type valid? */
		if (legalArticle)
			checkType();

		/*
		 * Is it an article? a subscription? is it legal at all?
		 */
		if (legalArticle)
			classifyArticle();
	}

	private void getFields(String rawArticle) {
		int pos = 0;
		String tempArticle = rawArticle;
		/* Get type */
		pos = tempArticle.indexOf(';');
		if (pos == -1) {
			System.out.println("ERROR: Illegal article: " + rawArticle);
			legalArticle = false;
			return;
		}
		type = tempArticle.substring(0, pos);
		tempArticle = tempArticle.substring(pos + 1);

		/* Get originator */
		pos = tempArticle.indexOf(';');
		if (pos == -1) {
			System.out.println("ERROR: Illegal article: " + rawArticle);
			legalArticle = false;
			return;
		}
		originator = tempArticle.substring(0, pos);
		tempArticle = tempArticle.substring(pos + 1);

		/* Get org */
		pos = tempArticle.indexOf(';');
		if (pos == -1) {
			System.out.println("ERROR: Illegal article: " + rawArticle);
			legalArticle = false;
			return;
		}
		org = tempArticle.substring(0, pos);
		tempArticle = tempArticle.substring(pos + 1);

		/* Get contents */
		pos = tempArticle.indexOf(';');
		contents = tempArticle;

		if (contents.length() >= MAXSTRING) {
			System.out
					.println("ERROR: Content's length exceed the maximum allowed: "
							+ contents);
			legalArticle = false;
		}
	}

	private void classifyArticle() {

		if (!contents.equals("")) { // Maybe an article
			validArticle = true;
		} else { // Maybe a suscription
			if (!type.equals("") || !originator.equals("") || !org.equals("")) {
				validSubscription = true;
			}
		}

		if (!validArticle && !validSubscription)
			legalArticle = false;
	}

	private void checkType() {
		if (!type.equals("") && !type.equalsIgnoreCase("sports")
				&& !type.equalsIgnoreCase("lifestyle")
				&& !type.equalsIgnoreCase("entertainment")
				&& !type.equalsIgnoreCase("business")
				&& !type.equalsIgnoreCase("technology")
				&& !type.equalsIgnoreCase("science")
				&& !type.equalsIgnoreCase("politics")
				&& !type.equalsIgnoreCase("health")) {
			System.out.println("ERROR: Illegal type: " + type);
			legalArticle = false;
		}

	}

	public boolean isValidArticle() {
		return validArticle;
	}

	public boolean isValidSubscription() {
		return validSubscription;
	}

	public String toString() {
		String string = "";

		if (legalArticle) {
			if (validArticle)
				string += "== ARTICLE ==\n";
			else if (validSubscription)
				string += "== SUBSCRIPTION ==\n";
			else
				string += "ERROR: Undetermined type of \"legal\" article!\n";

			string += "Type: " + type + "\n";
			string += "Originator: " + originator + "\n";
			string += "Org: " + org + "\n";
			string += "Contents: " + contents + "\n";
		} else {
			string += "This article is Illegal!\n";
		}

		string += raw + "\n";

		return string;
	}
}
