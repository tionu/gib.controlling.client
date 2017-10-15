package gib.controlling.client.setup;

public enum Params {
	ZOHO_AUTH_TOKEN(""),;

	private final String text;

	/**
	 * @param text
	 */
	private Params(final String text) {
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return text;
	}

}
