package laboflieven;

import java.io.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.InputStream;


import com.mysql.jdbc.*;
import org.apache.commons.csv.CSVParser;


/**
 * Created by Lieven on 15-11-2015.
 */
public class BiblioLoader
{
    Connection conn = null;
    private PreparedStatement insertArticle;
    private PreparedStatement insertKeyword;
    private PreparedStatement insertAuthor;
    private PreparedStatement insertAffiliation;
    private PreparedStatement insertAffiliationLink;
    private PreparedStatement insertKeywordArticleLink;
    private PreparedStatement insertAuthorArticleLink;

    private Map<String, Integer> affiliationMap  = new HashMap<>();
    private Map<String, Integer> keywordMap  = new HashMap<>();
    private Map<String, Integer> authorIdMap  = new HashMap<>();
    // Contact id | affiliationId
    private Set<String> contactAffiliationSet  = new TreeSet<>();

    public void loadData(final File csvFile) throws Exception {
        Properties properties = new Properties();

        InputStream stream = new FileInputStream("db.properties");
        properties.load(stream);

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn =
                DriverManager.getConnection("jdbc:mysql://localhost/"+properties.get("dbname")+"?" +
                        "user="+properties.get("dbuser")+"&password="+properties.get("dbpass"));
        conn.setAutoCommit(true);
        Reader reader = new BufferedReader(new FileReader(csvFile));
        CSVParser parser = new CSVParser(reader);
        String[] header = parser.getLine();
        int idIndex = getIndexFor("id", header);
        int ugentAuthorIndex = getIndexFor("ugent_author", header);
        int titleIndex = getIndexFor("title", header);
        int keywordIndex = getIndexFor("keyword", header);
        int typeIndex = getIndexFor("type", header);
        int dateIndex = getIndexFor("date_submitted", header);

        clearTables();

        AuthorParser authorParser = new AuthorParser();
        String[] record = parser.getLine();
        insertArticle = conn.prepareStatement("INSERT INTO article(id, title, type, date) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        insertKeyword = conn.prepareStatement("INSERT INTO keywordterm(name) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
        insertAuthor = conn.prepareStatement("INSERT INTO contact(firstname, lastname, ugent_id) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
        insertAffiliation = conn.prepareStatement("INSERT INTO affiliation(title) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
        insertAffiliationLink = conn.prepareStatement("INSERT INTO contact_affiliation(contactid, affiliationid) VALUES(?,?)");
        insertKeywordArticleLink = conn.prepareStatement("INSERT INTO article_keyword(article_id, keyword_id) VALUES(?,?)");
        insertAuthorArticleLink = conn.prepareStatement("INSERT INTO article_contact(contact_id, article_id) VALUES(?,?)");

        while (record != null)
        {
            Integer id = Integer.parseInt(record[idIndex]);
            String ugentAuthors = record[ugentAuthorIndex];

            String title = record[titleIndex];
            String type = record[typeIndex];
            String dateAsString = record[dateIndex];
            //2010-06-28 09:01:24
            DateFormat format = new SimpleDateFormat("y-M-d H:m:s");

            insertArticle.setInt(1, id);
            insertArticle.setString(2, title);
            insertArticle.setString(3, type);
            Date date = new Date(2020,12,12);
            if (dateAsString.length() > 0){
                date = new Date(format.parse(dateAsString).getTime());
            }
            insertArticle.setDate(4, date);
            insertArticle.executeUpdate();
            int articleId = getNextId(insertArticle);

            processContact(authorIdMap, authorParser, insertAuthor, insertAuthorArticleLink, ugentAuthors, articleId);
            String keywords = record[keywordIndex];
            processKeywords(keywordMap, insertKeyword, insertKeywordArticleLink, articleId, keywords);
            record = parser.getLine();
        }
        conn.close();
        reader.close();
    }

    private void clearTables() throws SQLException {
        conn.prepareStatement("DELETE FROM article_keyword").execute();
        conn.prepareStatement("DELETE FROM article_contact").execute();
        conn.prepareStatement("DELETE FROM contact_affiliation").execute();
        conn.prepareStatement("DELETE FROM article").execute();
        conn.prepareStatement("DELETE FROM contact").execute();
        conn.prepareStatement("DELETE FROM keywordterm").execute();
        conn.prepareStatement("DELETE FROM affiliation").execute();
    }

    private void processContact(Map<String, Integer> authorIdMap, AuthorParser authorParser, PreparedStatement insertAuthor, PreparedStatement insertAuthorArticleLink, String ugentAuthors, int articleId) throws SQLException {
        List<Author> authors = authorParser.getAuthors(ugentAuthors);
        for (Author author : authors)
        {
            int contactId;
            String authorFullName = author.firstName + "|" + author.lastName;
            if (authorIdMap.containsKey(authorFullName)) {
                contactId = authorIdMap.get(authorFullName);
            } else {
                insertAuthor.setString(1, author.firstName);
                insertAuthor.setString(2, author.lastName);
                insertAuthor.setLong(3, author.uzId == null ? 0 : author.uzId);
                insertAuthor.execute();
                contactId =  getNextId(insertAuthor);
                authorIdMap.put(authorFullName, contactId);
            }
            for (String affiliation : author.affiliation)
            {
                int affilionId;
                if (affiliationMap.containsKey(affiliation))
                {
                    affilionId = affiliationMap.get(affiliation);
                } else {
                    insertAffiliation.setString(1, affiliation);
                    insertAffiliation.execute();
                    affilionId =  getNextId(insertAffiliation);
                    affiliationMap.put(affiliation, affilionId);
                }
                if (contactAffiliationSet.contains(contactId+"|"+affilionId))  {
                    // NOP.
                } else {
                    insertAffiliationLink.setInt(1, contactId);
                    insertAffiliationLink.setInt(2, affilionId);
                    insertAffiliationLink.execute();
                    contactAffiliationSet.add(contactId + "|" + affilionId);
                }
            }

            insertAuthorArticleLink.setInt(1, contactId);
            insertAuthorArticleLink.setInt(2, articleId);
            insertAuthorArticleLink.execute();

        }
    }

    private void processKeywords(Map<String, Integer> keywordMap, PreparedStatement insertKeyword, PreparedStatement insertKeywordArticleLink, int articleId, String keywords) throws SQLException {
        if (keywords.trim().length() != 0) {
            Set<String> keyWordSet = new TreeSet<>(Arrays.asList(keywords.split(";")));

            for (String keyword : keyWordSet)
            {
                if (keyword.length() > 512)
                {
                    System.out.println(keyword);
                }
                else {
                    int termId;
                    if (keywordMap.containsKey(keyword)) {
                        termId = keywordMap.get(keyword);
                    } else {
                        insertKeyword.setString(1, keyword.trim());
                        insertKeyword.execute();
                        termId = getNextId(insertKeyword);
                        keywordMap.put(keyword, termId);
                    }
                    insertKeywordArticleLink.setInt(1, articleId);
                    insertKeywordArticleLink.setInt(2, termId);
                    insertKeywordArticleLink.execute();
                }
            }
        }
    }


    private static int getNextId(PreparedStatement statement) throws SQLException {
        ResultSet set = statement.getGeneratedKeys();
        set.next();
        return set.getInt(1);
    }
    private int getIndexFor(String id, String[] headers) throws Exception {
        int headerIndex = 0;
        for (String header : headers)
        {
            if (header.equals(id)) return headerIndex;
            headerIndex++;
        }
        throw new Exception("Header not found  " + id);
    }

    public static void main(String[] args) throws Exception {
        BiblioLoader loader = new BiblioLoader();
        loader.loadData(new File("publications.csv"));

    }
}
