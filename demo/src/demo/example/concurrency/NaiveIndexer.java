package demo.example.concurrency;

import java.net.MalformedURLException;

import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * For N web links, this approach creates 2 * N threads
 * Benefit: Better coordination of control flow between threads. Relinquishes lock on wait()
 * 
 * 
 * Note: htmlPage is not declared volatile in weblink as unlock on a monitor 'happends before' every subsequent lock on that same monitor.
 * Limitations:
 * solves task cooperation in low level fashion. it's like programming in concurrency assembly language
 * synchronized blocks are needed.
 * @author apandey
 *
 */

public class NaiveIndexer {
	private Deque<Weblink> queue = new ArrayDeque<>();
	
	
	
	private static class Weblink {
		
		private long id;
		private String title;
		private String url;
		private String host;
		
		
		//shared resource, weblink
		private volatile String htmlPage;
		
		
		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public String getHtmlPage() {
			return htmlPage;
		}

		public void setHtmlPage(String htmlPage) {
			this.htmlPage = htmlPage;
		}

		
	}
	
	private static class Downloader implements Runnable {
		private Weblink weblink;
		public Downloader(Weblink weblink) {
			this.weblink = weblink;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				synchronized (weblink) {
					String htmlPage = HttpConnect.download(weblink.getUrl());
					weblink.setHtmlPage(htmlPage);
					
					weblink.notify();
				}
				//lock is released here
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}	
	}
	
	private static class Indexer implements Runnable {
		private Weblink weblink;
		private Indexer(Weblink weblink) {
			this.weblink = weblink;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
//			while(true) {
//				String htmlPage = weblink.getHtmlPage();
//				//remember that htmlPage is a volatile variable
//				if(htmlPage != null) {
//					index(htmlPage);
//					break;
//				} else {
//					System.out.println(weblink.getId() + " not yet downloaded!");
//				}
//			}
			String htmlPage = weblink.getHtmlPage();
			
			synchronized(weblink) {
				
				//standard idiom is that wait() should be used with while loop and not with if block: look for spurious wakeup
				while(htmlPage == null) {
					try {
						System.out.println(weblink.getId() + " not yet downloaded!");
						weblink.wait();
						//wait() releases the lock, while sleep() and yield() don't.
						System.out.println(weblink.getId() + " awakened");
						htmlPage = weblink.getHtmlPage();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					index(htmlPage);
				}
			}
			
		}
		private void index(String text) {
			if (text != null) {
				System.out.println("\nIndexed: " + weblink.getId() + "\n");
			}
		}
		
	}
	
	public void go() {
		while(queue.size() > 0) {
			Weblink weblink = queue.remove();
			Thread downloaderThread = new Thread(new Downloader(weblink));
			Thread indexerThread = new Thread(new Indexer(weblink));
			
			downloaderThread.start();
			indexerThread.start();
		}
	}
	
	public void add(Weblink weblink) {
		queue.add(weblink);
	}
	
	public Weblink createWeblink(long id, String title, String url, String host) {
		Weblink weblink = new Weblink();
		weblink.setId(id);
		weblink.setTitle(title);
		weblink.setUrl(url);
		weblink.setHost(host);
		return weblink;
		
	}
	
	public static void main(String[] args) {
		NaiveIndexer naiveIndexer = new NaiveIndexer();
		naiveIndexer.add(naiveIndexer.createWeblink(2000, "Taming Tiger, Part 2", "https://www.javaworld.com/article/2072759/core-java/taming-tiger--part-2.html", "http://www.javaworld.com"));
		naiveIndexer.add(naiveIndexer.createWeblink(2001, "How do I import a pre-existing Java project into Eclipse and get up and running?", "https://stackoverflow.com/questions/142863/how-do-i-import-a-pre-existing-java-project-into-eclipse-and-get-up-and-running", "http://www.stackoverflow.com"));
		naiveIndexer.add(naiveIndexer.createWeblink(2002, "Interface vs Abstract Class", "http://mindprod.com/jgloss/interfacevsabstract.html", "http://mindprod.com"));
		naiveIndexer.add(naiveIndexer.createWeblink(2004, "Virtual Hosting and Tomcat", "http://tomcat.apache.org/tomcat-6.0-doc/virtual-hosting-howto.html", "http://tomcat.apache.org"));
	    naiveIndexer.go();
	}
	
}
