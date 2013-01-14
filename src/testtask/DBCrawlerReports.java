package testtask;

import java.sql.SQLException;

public class DBCrawlerReports implements CrawlerReports {
    
    private Database database;
    
    public DBCrawlerReports() throws Exception {
        this.database = new Database();
        database.create();
    }
    
    public void reportURL(Link link) {
        try {

            database.insert(
                    link.getUrl().toString(), link.getNumOfNest(),
                    link.getUrlExtrnl(), link.getDescription().toString());
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }
}
