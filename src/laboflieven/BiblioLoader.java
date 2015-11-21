package laboflieven;

import java.io.*;
import java.sql.*;
import java.util.List;

import com.mysql.jdbc.Driver;


import org.apache.commons.csv.CSVParser;
/**
 * Created by Lieven on 15-11-2015.
 */
public class BiblioLoader
{
    Connection conn = null;

    public void loadData(final File csvFile) throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn =
                DriverManager.getConnection("jdbc:mysql://localhost/inventarisatie?" +
                        "user=root&password=password");
        conn.setAutoCommit(true);
        Reader reader = new BufferedReader(new FileReader(csvFile));
        CSVParser parser = new CSVParser(reader);
        String[] header = parser.getLine();
        int idIndex = getIndexFor("id", header);
        int ugentAuthorIndex = getIndexFor("ugent_author", header);
        int titleIndex = getIndexFor("title", header);
        int keywordIndex = getIndexFor("keyword", header);
        int typeIndex = getIndexFor("type", header);
        conn.prepareStatement("DELETE FROM article").execute();
        conn.prepareStatement("DELETE FROM ugent_contact").execute();
        AuthorParser authorParser = new AuthorParser();
        String[] record = parser.getLine();
        PreparedStatement insertArticle = conn.prepareStatement("INSERT INTO article(idarticle, title, type) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
        PreparedStatement insertAuthor = conn.prepareStatement("INSERT INTO ugent_contact(firstname, lastname, ugent_id) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
        PreparedStatement insertAuthorArticleLink = conn.prepareStatement("INSERT INTO contact_article(contact_id, article_id) VALUES(?,?)");
        while (record != null)
        {
            Integer id = Integer.parseInt(record[idIndex]);
            String ugentAuthors = record[ugentAuthorIndex];

            String title = record[titleIndex];
            String type = record[typeIndex];


            insertArticle.setInt(1, id);
            insertArticle.setString(2, title);
            insertArticle.setString(3, type);
            insertArticle.executeUpdate();
            ResultSet articleSet = insertArticle.getGeneratedKeys();
            articleSet.next();
            int articleId = articleSet.getInt(1);

            List<Author> authors = authorParser.getAuthors(ugentAuthors);
            for (Author author : authors)
            {
                if (author.uzId != null) {
                    insertAuthor.setString(1, author.firstName);
                    insertAuthor.setString(2, author.lastName);
                    insertAuthor.setLong(3, author.uzId);
                    insertAuthor.execute();
                    ResultSet set = insertAuthor.getGeneratedKeys();
                    set.next();
                    int contactId = set.getInt(1);

                    insertAuthorArticleLink.setInt(1, contactId);
                    insertAuthorArticleLink.setInt(2, articleId);
                    insertAuthorArticleLink.execute();
                }
            }
            record = parser.getLine();
        }
        conn.close();
        reader.close();
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
