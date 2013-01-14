package testtask;


/**
*
* @author oleg
*/
public class Main {
	public static void main(String[] args) throws Exception {
	    Crawler crawler = new Crawler();
	    
		LinkChecker linkChecker = new LinkChecker(crawler);
		DBCrawlerReports crawlerReports = new DBCrawlerReports();
		
		crawler.addCrawlerReports(linkChecker);
                crawler.addCrawlerReports(crawlerReports);
		
		linkChecker.setVisible(true);
	}
}
