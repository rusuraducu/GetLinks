import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class GetLinks {

    private static Set<String> allTheLinks = new LinkedHashSet<>();

    public static void main(String[] args) {
        GetLinks GetLinks = new GetLinks();
        Set<String> urlList = new HashSet<>();
        urlList.add("https://en.wikipedia.org/wiki/FGE");
        GetLinks.writeLinksFeedBack(GetLinks, urlList);

    }

    private void writeLinksFeedBack(GetLinks GetLinks, Set<String> urlList) {
        if (!isValidLink(urlList)) {
            System.out.println("The url is not valid.");
        }
        else
            {
            int noLinks = GetLinks.writeAllTheLinksInTheTextFile(urlList);
            System.out.println(noLinks + " links have been written in the text file.");
        }
    }

    private int writeAllTheLinksInTheTextFile(Set<String> urlList) {
        getLinksUpToTheThirdGeneration(urlList, 3); // recursion "3" means to get the links up to the third generation
        int i = 0;
        if (theFileExists()) {
            clearFile();
        }
        for (String g : allTheLinks) {
            i++;
            String url = i + ". " + formatURL(g);
            writeLink(url);
        }
        return i;
    }

    private boolean getLinksUpToTheThirdGeneration(Set<String> urlList, int recursion) {
        Set<String> temp = new HashSet<>();
        for (String url : urlList) {
            temp.addAll(getAllTheLinksFromPage(getContent(formatURL(url))));
        }
        addTheLinksInTheGlobalSetWithLinks(temp);
        recursion = recursion - 1;
        if (recursion > 0) {
            getLinksUpToTheThirdGeneration(allTheLinks, recursion);
        }
        return true;
    }


    private Set<String> getAllTheLinksFromPage(String content) {
        Set<String> getLinks = new HashSet<>();
        int i = 0;
        while (true) {
            int found = content.indexOf("href=\"/wiki", i);
            if (found == -1) {
                break;
            }
            int start = found + 6;
            int end = content.indexOf("\"", start);
            String urlLink = content.substring(start, end);
            getLinks.add(urlLink + "\n");
            i = end + 1;
        }
        return getLinks;
    }

    private static boolean isValidLink(Set<String> urls) {
        for (String u : urls) {
            if (u.startsWith("https://en.wikipedia.org/") == true || u.startsWith("/wiki") == true) {
                return true;
            }
        }
        return false;
    }

    private String getContent(String url) {

        StringBuilder Builder = new StringBuilder();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(connection(url).getInputStream()))) {
            String Line = new String();
            while ((Line = bf.readLine()) != null) {
                if (isArticle(Line)) {
                    Builder.append(Line + "\n");
                }
            }
        } catch (Exception ex) {

        }
        return Builder.toString();
    }

    private HttpURLConnection connection(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
        } catch (IOException ex) {
            ex.getStackTrace();
        }
        return conn;
    }

    //Am observat ca un link care duce catre un articol contine "wiki/" dupa domeniu/ si nu contine " : ".
    //Am eliminat link-ul care duce catre pagina principala a website-ului. Acesta are o structura asemanatoare
    //cu link-urile care duc catre articole.

    private boolean isArticle(String url) {
        if (url.contains("/wiki/Main_Page")) {
            return false;
        }
        for (char c : url.toCharArray()) {
            if (c == ':') {
                return false;
            }
        }
        return true;
    }

    private void addTheLinksInTheGlobalSetWithLinks(Set<String> temp) {
        allTheLinks.addAll(temp);
        temp.clear();
    }

    private String writeLink(String url) {
        try (BufferedWriter bf = new BufferedWriter(new FileWriter("D:\\allTheLinks.txt", true))) {
            bf.write(url);
            bf.newLine();
        } catch (IOException ex) {
            ex.getStackTrace();
        }
        return url;
    }

    private String formatURL(String url) {
        if (url.startsWith("https://en.wikipedia.org") != true) {
            url = "https://en.wikipedia.org" + url;
        }
        return url;
    }

    private boolean theFileExists() {
        BufferedReader bfr = null;
        try {
            bfr = new BufferedReader(new FileReader("D:\\allTheLinks.txt"));
            bfr.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private void clearFile() {
        try {
            FileWriter fw = new FileWriter("D:\\allTheLinks.txt");
            fw.write("");
            fw.close();
        } catch (IOException io) {
            return;
        }
    }
}
