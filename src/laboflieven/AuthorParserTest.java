package laboflieven;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Lieven on 15-11-2015.
 */
public class AuthorParserTest
{
    @Test
    public void verify()
    {
        AuthorParser parser = new AuthorParser();
        List<Author> auths = parser.getAuthors("Soly, Hugo (801000325312)");
        assertEquals("Hugo", auths.get(0).firstName);
        assertEquals("Soly", auths.get(0).lastName);
        assertEquals(801000325312L, auths.get(0).uzId.longValue());

    }

    @Test
    public void verifyWithAfil()
    {
        AuthorParser parser = new AuthorParser();
        List<Author> auths = parser.getAuthors("Soly, Hugo (801000325312@LW)");
        assertEquals("Hugo", auths.get(0).firstName);
        assertEquals("Soly", auths.get(0).lastName);
        assertEquals(801000325312L, auths.get(0).uzId.longValue());

    }


    @Test
    public void verifyNoId()
    {
        AuthorParser parser = new AuthorParser();
        List<Author> auths = parser.getAuthors("Soly, Hugo");
        assertEquals("Hugo", auths.get(0).firstName);
        assertEquals("Soly", auths.get(0).lastName);
        assertNull(auths.get(0).uzId);

    }


    @Test
    public void verifyAffiliation()
    {
        AuthorParser parser = new AuthorParser();
        List<Author> auths = parser.getAuthors("Soly, Hugo (801000325312@LW)");
        assertEquals("LW", auths.get(0).affiliation.get(0));
    }
}