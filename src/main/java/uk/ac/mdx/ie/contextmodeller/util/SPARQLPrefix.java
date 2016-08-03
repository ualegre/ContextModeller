package uk.ac.mdx.ie.contextmodeller.util;

public class SPARQLPrefix {

	private String mName;
	private String mURL;


	public SPARQLPrefix(String str) {
		separateNameAndURL(str);
	}

	public SPARQLPrefix() {
	}

	private void separateNameAndURL(String str) {
		String[] strs = str.split(":<");

		mName = strs[0];
		mURL = strs[1].substring(0, strs[1].length() -1 );

	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;

	}

	public String getURL() {
		return mURL;
	}

	public void setURL(String url) {
		mURL = url;
	}

	@Override
	public String toString() {
		return mName + ":<" + mURL + ">";
	}



}
