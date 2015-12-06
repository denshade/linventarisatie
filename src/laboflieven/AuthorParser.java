package laboflieven;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lieven on 15-11-2015.
 */
public class AuthorParser {
    public List<Author> getAuthors(String ugentAuthors)
    {
        Set<Author> authors = new TreeSet<Author>();
        String[] authorInput = ugentAuthors.split(";");
        for (String authorCoded : authorInput)
        {
            Author author = new Author();
            String[] splitAuthors = authorCoded.split(",");
            String lastname = splitAuthors[0];
            Long code = null;
            if (splitAuthors.length > 1)
            {
                String firstnameWithCode = splitAuthors[1];
                Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(authorCoded);
                while(m.find()) {
                    String codeWithAffil = m.group(1);
                    String codeNoAfil = codeWithAffil;
                    if (codeWithAffil.indexOf('@') > -1) {
                        codeNoAfil = codeWithAffil.substring(0, codeWithAffil.indexOf('@'));
                        author.affiliation.add(codeWithAffil.substring(codeWithAffil.indexOf('@') + 1));
                    }
                    try {
                        code = Long.parseLong(codeNoAfil.trim());
                    } catch(NumberFormatException e)
                    {
                        System.out.println(ugentAuthors + e.getMessage());
                    }
                }
                if (firstnameWithCode.indexOf('(') > -1) {
                    author.firstName = firstnameWithCode.substring(0, firstnameWithCode.indexOf('(')).trim();
                } else {
                    author.firstName = firstnameWithCode.trim();
                }
            }

            author.lastName = lastname;
            author.uzId = code;
            authors.add(author);
        }
        return new ArrayList(authors);
    }

}
