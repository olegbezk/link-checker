/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testtask;

import java.awt.Dimension;
import java.awt.Insets;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * 
 * @author oleg
 */
public class LinkChecker extends javax.swing.JFrame implements Runnable, CrawlerReports {
    public LinkChecker(Crawler crawler) {
        this.crawler = crawler;
        
        setTitle("Find External URL");
        getContentPane().setLayout(null);
        setSize(700, 400);
        setVisible(false);

        label.setText("Enter URL:");
        getContentPane().add(label);
        label.setBounds(12, 12, 84, 12);

        start.setText("Start");
        start.setActionCommand("Start");
        getContentPane().add(start);
        start.setBounds(12, 36, 84, 24);

        getContentPane().add(url);
        url.setBounds(108, 36, 450, 24);

        resultScroll.setAutoscrolls(true);
        resultScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resultScroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        resultScroll.setOpaque(true);
        getContentPane().add(resultScroll);
        resultScroll.setBounds(12, 130, 670, 200);

        result.setEditable(false);
        resultScroll.getViewport().add(result);
        // result.setBounds(0, 0, 366, 138);

        processLabel.setText("Currently Processing: ");
        getContentPane().add(processLabel);
        processLabel.setBounds(12, 76, 584, 15);

        // register Action listeners
        Action action = new Action();
        start.addActionListener(action);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Add Notification :
    @Override
    public void addNotify() { // Record the size of the window
        Dimension size = getSize();
        super.addNotify();
        if (frameSizeAdjust) {
            return;
        }
        frameSizeAdjust = true;
        // Adjust size of frame
        Insets insets = getInsets();
        javax.swing.JMenuBar menuBar = getRootPane().getJMenuBar();
        int menuBarHeight = 0;
        if (menuBar != null) {
            menuBarHeight = menuBar.getPreferredSize().height;
        }
        setSize(insets.left + insets.right + size.width, insets.top + insets.bottom + size.height + menuBarHeight);
    }

    // addNotify val
    boolean frameSizeAdjust = false;
    // declare controls
    javax.swing.JLabel label = new javax.swing.JLabel();
    javax.swing.JButton start = new javax.swing.JButton();
    javax.swing.JTextField url = new javax.swing.JTextField();
    javax.swing.JButton show = new javax.swing.JButton();
    // scroll the results
    javax.swing.JScrollPane resultScroll = new javax.swing.JScrollPane();
    // declare output
    javax.swing.JTextArea result = new javax.swing.JTextArea();
    javax.swing.JLabel processLabel = new javax.swing.JLabel();
    javax.swing.JLabel numOfNest = new javax.swing.JLabel();
    javax.swing.JLabel numOfExtrnlURL = new javax.swing.JLabel();
    // declare menu's events
    private Thread backgroundThread;
    private Crawler crawler;
    private URL urlproc;

    // dispatch events
    class Action implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent event) {
            Object object = event.getSource();
            if (object == start) {
                begin_actionPerformed(event);
            }
        }
    }

    // Called when button is clicked
    void begin_actionPerformed(java.awt.event.ActionEvent event) {
        if (backgroundThread == null) {
            start.setText("Cancel");
            backgroundThread = new Thread(this);
            backgroundThread.start();
        } else {
            crawler.cancel();
        }
    }

    // starts background thread
    @Override
    public void run() {
        try {
            result.setText("");
            String text = url.getText();

            if (text.charAt(text.length() - 1) != '/') {
                text += "/";
            }
            
            if (!text.startsWith("http://")) {
                text = "http://" + text; 
            }

            urlproc = new URL(text);
            
            crawler.clear();
            
            crawler.setBase(urlproc);
            crawler.start();

            Runnable doLater = new Runnable() {
                @Override
                public void run() {
                    start.setText("Start");
                }
            };

            SwingUtilities.invokeLater(doLater);
            backgroundThread = null;

        } catch (MalformedURLException e) {
            UpdateResult err = new UpdateResult();
            err.msg = "Bad address.";
            SwingUtilities.invokeLater(err);

        }catch(StringIndexOutOfBoundsException e) {
            UpdateResult err = new UpdateResult();
            err.msg = "Fill The Adress Field";
            SwingUtilities.invokeLater(err);
        }
    }

    //
    @Override
    public void reportURL(Link link) {
        UpdateCurrentState cs = new UpdateCurrentState();
        cs.msg = link.getUrl().toString();
        SwingUtilities.invokeLater(cs);
        result.append(link.toString() + "\n");
    }

    protected boolean checkLinks(URL url) {
        try {
            URLConnection connection = url.openConnection();
            connection.connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    class UpdateResult implements Runnable {
        public String msg;

        @Override
        public void run() {
            result.append(msg);
        }
    }

    class UpdateCurrentState implements Runnable {
        public String msg;

        @Override
        public void run() {
            processLabel.setText("Currently Processing: " + msg);
        }
    }
}
