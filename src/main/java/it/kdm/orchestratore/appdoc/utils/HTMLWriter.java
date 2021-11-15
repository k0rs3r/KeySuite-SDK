package it.kdm.orchestratore.appdoc.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.fit.pdfdom.PDFDomTree;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.*;

public class HTMLWriter extends PDFDomTree {

    public HTMLWriter() throws IOException, ParserConfigurationException {
        super();
    }

    private String pageHref = null;
    private int pagerMaxPages = 20;

    private static Map<String,PDDocument>  docs = new HashMap<>() ;
    private static Map<String,Timer>  timers = new HashMap<>() ;
    static long expiration = Long.parseLong(System.getProperty("pdfexpiration","600")); // -1 per expirare sempre

    @Override
    protected Element createPageElement()
    {
        Element el = super.createPageElement();
        el.setAttribute("title",""+getCurrentPageNo()+"/"+document.getNumberOfPages());
        return el;
    }

    public void setPageHref(String pageHref)
    {
        this.pageHref = pageHref;
    }

    public String getPageHref()
    {
        return this.pageHref;
    }

    /*public static void decompressGzip(String infile, String outfile) throws IOException {

        File input = new File(infile);
        File output = new File(outfile);

        try (GZIPInputStream in = new GZIPInputStream(new FileInputStream(input))){
            try (FileOutputStream out = new FileOutputStream(output)){
                byte[] buffer = new byte[1024];
                int len;
                while((len = in.read(buffer)) != -1){
                    out.write(buffer, 0, len);
                }
            }
        }
    }*/

    public static boolean writeHTML(String infile, String outfile) throws IOException
    {
        return writeHTML(infile,outfile,0,Integer.MAX_VALUE,null,20);
    }

    public static boolean writeHTML(String infile, String outfile, int page, String pageHref, int pagerMaxPages) throws IOException
    {
        return writeHTML(infile,outfile,page,page,pageHref,pagerMaxPages);
    }

    private static boolean writeHTML(final String infile, String outfile, int startPage, int endPage, String pageHref, int pagerMaxPages) throws IOException
    {
        HTMLWriter pdfDomWriter;
        try {
            pdfDomWriter = new HTMLWriter();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        pdfDomWriter.setStartPage(startPage);
        pdfDomWriter.setEndPage(endPage);
        pdfDomWriter.pagerMaxPages = pagerMaxPages;
        pdfDomWriter.setPageHref(pageHref);

        PDDocument document = docs.get(infile);
        Timer timer = timers.get(infile);

        if (timer!=null)
        {
            timer.cancel();
            timers.remove(infile);
            System.out.println("timer "+infile+" removed");
        }

        if (document==null)
        {
            document = PDDocument.load(new File(infile));
            System.out.println(infile+" opened");
        }

        if (expiration>0)
        {
            if (!docs.containsKey(infile))
            {
                docs.put(infile,document);
                System.out.println("doc "+infile+" added");
            }

            final PDDocument temp2 = document;

            timer = new Timer();
            timers.put(infile,timer);

            timer.schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            try {
                                docs.remove(infile);
                                timers.remove(infile);
                                temp2.close();
                                System.out.println("doc and timer "+infile+" removed");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    expiration*1000
            );
            System.out.println("timer "+infile+" added");
        }

        try
        {
            Writer output = new PrintWriter(outfile, "utf-8");

            pdfDomWriter.writeText(document, output);
            output.close();
        }
        finally
        {
            if( document != null && expiration<=0 || ((endPage+1) == document.getNumberOfPages())  )
            {
                if (timer!=null)
                {
                    timer.cancel();
                    timer.purge();
                    timers.remove(infile);
                    System.out.println("timer "+infile+" removed");
                }

                docs.remove(infile);
                document.close();
                System.out.println("doc "+infile+" removed");
            }
        }

        return (endPage+1) < document.getNumberOfPages();
    }

    protected Element createPagerElement()
    {
        String pstyle = "";
        PDRectangle layout = getCurrentMediaBox();
        if (layout != null)
        {
            float w;
            final int rot = pdpage.getRotation();
            if (rot == 90 || rot == 270)
                w = layout.getHeight();
            else
                w = layout.getWidth();

            pstyle = "width:" + w + UNIT + ";";
            pstyle += "overflow:hidden;";
        }

        pstyle += "text-align:center;";

        Element pager = doc.createElement("div");
        pager.setAttribute("id", "footer" );
        pager.setAttribute("class", "page");
        pager.setAttribute("style", pstyle);

        int currentPage = getStartPage();

        if (document.getNumberOfPages()<= pagerMaxPages)
        {
            for ( int i=0; i< document.getNumberOfPages(); i++ )
                addPageLink(pager,i);
        }
        else
        {
            int param = Math.max(pagerMaxPages/3,1);
            //int p2 = 2;

            Collection<Integer> list = new HashSet<>();

            for ( int i=0; i<param; i++ )
                list.add(i);

            for ( int i=document.getNumberOfPages()-param; i<document.getNumberOfPages(); i++ )
                list.add(i);

            for ( int i=currentPage-param/2; i<=currentPage+param/2; i++ )
                list.add(i);

            boolean flag = false;
            for ( int i=0; i<document.getNumberOfPages(); i++ )
            {
                if (list.contains(i) || (list.contains(i+1) && list.contains(i-1) ))
                {
                    flag = false;
                    addPageLink(pager,i);
                }
                else if (!flag)
                {
                    pager.appendChild(doc.createTextNode(".."));
                    flag = true;
                }
            }
        }

        return pager;
    }

    protected void addPageLink(Element pager,int page)
    {
        boolean disabled = (page==getStartPage());

        Element link;
        if (!disabled)
        {
            link = doc.createElement("a");
            link.setAttribute("href",String.format(pageHref,(page+1)) );
        }
        else
        {
            link = doc.createElement("span");
        }
        link.setTextContent(""+(page+1));

        pager.appendChild(link);
        pager.appendChild(doc.createTextNode(" "));
    }

    @Override
    protected void endDocument(PDDocument document) throws IOException {
        super.endDocument(document);

        if (pagerMaxPages>0 && pdpage != null && pageHref != null && getStartPage() == getEndPage() && document.getNumberOfPages() > 1) {
            Element pager = createPagerElement();
            body.appendChild(pager);
        }

        if (pdpage != null && pageHref != null && getStartPage() == getEndPage() && getStartPage() < (document.getNumberOfPages()-1) ) {

            Element link = doc.createElement("a");
            link.setAttribute("id","next"+getStartPage());
            link.setAttribute("class","nextlink");
            link.setAttribute("href",String.format(pageHref,(getStartPage()+1) ));
            link.setAttribute("data-pages",""+document.getNumberOfPages() );
            link.setAttribute("data-page",""+getStartPage() );

            body.appendChild(link);
        }

    }


    @Override
    protected void processPages(PDPageTree pages) throws IOException {

        try {
            Field field = PDFTextStripper.class.getDeclaredField("currentPageNo");
            field.setAccessible(true);

            for (PDPage page : pages) {

                Integer currentPageNo = (Integer) field.get(this);
                field.set(this, currentPageNo + 1);

                if (page.hasContents() && currentPageNo >= startPage && currentPageNo <= endPage ) {
                    processPage(page);
                }
            }
        }
        catch (Exception e) {
            throw new IOException("error");
        }
    }


}