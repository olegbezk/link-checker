package testtask;

import java.net.URL;

/**
*
* @author oleg
*/
public class Link {
	private URL url;
	private int numOfNest;
	private int urlExtrnl;
	private String description;

	public Link() {
	}
	
	public Link(URL url, int numOfNest, int urlExtrnl, String description) {
		this.url = url;
		this.numOfNest = numOfNest;
		this.urlExtrnl = urlExtrnl;
		this.description = description;
	}

	public URL getUrl() {
		return url;
	}

    public void setUrl(URL url) {
		this.url = url;
	}

	public int getNumOfNest() {
		return numOfNest;
	}

	public void setNumOfNest(int numOfNest) {
		this.numOfNest = numOfNest;
	}

	public int getUrlExtrnl() {
		return urlExtrnl;
	}

	public void setUrlExtrnl(int urlExtrnl) {
		this.urlExtrnl = urlExtrnl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
    public String toString() {
        return "Link [url=" + url + ", numOfNest=" + numOfNest + ", urlExtrnl=" + urlExtrnl + ", description="
                + description + "]";
    }
}
