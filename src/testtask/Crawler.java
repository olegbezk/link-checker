/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testtask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

/**
 * 
 * @author oleg
 */
public class Crawler {
    private List<CrawlerReports> crawlerReportsList = new ArrayList<CrawlerReports>();

    private List<Link> internalUrls = new ArrayList<Link>();
    private Set<String> visited = new HashSet<String>();
    
    private boolean cancel;
    
    private URL base;
    
    public void setBase(URL base) {
        this.base = base;
        internalUrls.add(new Link(base, 0, 0, ""));
    }
    
    private void addLink(Link link) {
        internalUrls.add(link);
    }
    
    public void addCrawlerReports(CrawlerReports crawlerReports) {
        crawlerReportsList.add(crawlerReports);
    } 
    
    // processed URL
    public void processURL(Link link) {
        try {
            URLConnection connection = link.getUrl().openConnection();

            if ((connection.getContentType() != null) && !connection.getContentType().
                    toLowerCase().startsWith("text/")) {
                visited.add(link.getUrl().toString());
                return;
            }
            
            InputStream is = connection.getInputStream();
            Reader r = new InputStreamReader(is);

            HTMLEditorKit.Parser parser = new HTMLParser().getParser();
            parser.parse(r, new Parser(link), true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // completed

        visited.add(link.getUrl().toString());
        
        for (CrawlerReports crawlerReports: crawlerReportsList) {
            crawlerReports.reportURL(link);
        }
    }

    // called to start
    public void start() {
        
        for (int i = 0; i < internalUrls.size(); i++) {
            if (cancel) {
                break;
            }
            
            if (visited.contains(internalUrls.get(i).toString())) {
                continue;
            }
            
            processURL(internalUrls.get(i));
        }
    }

    // Detecting links
    private class Parser extends HTMLEditorKit.ParserCallback {
        private Link parsedURL;

        public Parser(Link link) {
            parsedURL = link;
        }

        @Override
        public void handleSimpleTag(HTML.Tag t, MutableAttributeSet s, int postn) {

            String href = (String) s.getAttribute(HTML.Attribute.HREF);
            
            if ((href == null) && (t == HTML.Tag.FRAME)) {
                href = (String) s.getAttribute(HTML.Attribute.SRC);
            }
            
            if (href == null) {
                return;
            }
            
            
            int i = href.indexOf('#');
            if (i != -1) {
                href = href.substring(0, i);
            }
            
            if (href.toLowerCase().startsWith("mailto:")) {
                return;
            }
            
            if (href.toLowerCase().startsWith("javascript")) {
                return;
            }
            
            handleLink(parsedURL, href);
        }

        @Override
        public void handleStartTag(HTML.Tag t, MutableAttributeSet s, int postn) {
            handleSimpleTag(t, s, postn); // as the same way
        }

        public void handleLink(Link procurl, String s) {
            try {
                URL url = new URL(procurl.getUrl(), s);
                
                if (base.getHost().equalsIgnoreCase(url.getHost())) {
                    if (!visited.contains(url.toString())) {
                        addLink(new Link(url, procurl.getNumOfNest() + 1, 0, ""));
                    }
                } else {
                    procurl.setUrlExtrnl(procurl.getUrlExtrnl() + 1);
                }
                
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    // Iterupped start process before it's done all work
    public void cancel() {
        cancel = true;
    }
    // return to pervious state
    public void clear() {
        internalUrls.clear();
        visited.clear();
        cancel = false;
    }
}

class HTMLParser extends HTMLEditorKit {

    @Override
    public HTMLEditorKit.Parser getParser() {
        return super.getParser();
    }
}
