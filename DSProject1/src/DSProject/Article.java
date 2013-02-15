package DSProject;

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

		/* Split the fields (and checks the format! */
		getFields(rawArticle);

		/* Is the type valid? */
		if (legalArticle)
			checkType();

		/*
		 * Is it an article? a subscription? is it legal at all?
		 */
		if (legalArticle)
			classifyArticle();

		/* if some fields are empty, write "all" on them */
		normalizeArticle();
	}

	public Article(String type, String originator, String org, String contents) {
		this.type = type;
		this.originator = originator;
		this.org = org;
		this.contents = contents;

		raw = encode();

		legalArticle = true;

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
		type = tempArticle.substring(0, pos).toLowerCase();
		tempArticle = tempArticle.substring(pos + 1);

		/* Get originator */
		pos = tempArticle.indexOf(';');
		if (pos == -1) {
			System.out.println("ERROR: Illegal article: " + rawArticle);
			legalArticle = false;
			return;
		}
		originator = tempArticle.substring(0, pos).toLowerCase();
		tempArticle = tempArticle.substring(pos + 1);

		/* Get org */
		pos = tempArticle.indexOf(';');
		if (pos == -1) {
			System.out.println("ERROR: Illegal article: " + rawArticle);
			legalArticle = false;
			return;
		}
		org = tempArticle.substring(0, pos).toLowerCase();
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

	private void normalizeArticle() {
		if (type.equals(""))
			type = "all";
		if (originator.equals(""))
			originator = "all";
		if (org.equals(""))
			org = "all";

	}

	private void checkType() {
		if (!type.equals("") && !type.equalsIgnoreCase("sports")
				&& !type.equals("lifestyle") && !type.equals("entertainment")
				&& !type.equals("business") && !type.equals("technology")
				&& !type.equals("science") && !type.equals("politics")
				&& !type.equals("health")) {
			System.out.println("ERROR: Illegal type: " + type);
			legalArticle = false;
		}

	}

	public boolean isValidArticle() {
		return validArticle;
	}

	public String getType() {
		return type;
	}

	public String getOriginator() {
		return originator;
	}

	public String getOrg() {
		return org;
	}

	public String getContents() {
		return contents;
	}

	public boolean isValidSubscription() {
		return validSubscription;
	}

	public String encode() {
		String string = "";

		string += type + ";" + originator + ";" + org + ";" + contents;

		return string;
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

	public boolean equals(Object o) {
		Article a = (Article) o;
		
		return a.type.equals(this.type) && a.originator.equals(this.originator)
				&& a.org.equals(this.org) && a.contents.equals(this.contents);
	}
}
