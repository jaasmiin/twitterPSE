package categorizer;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * some simple tests for the normalize methods
 * 
 * @author Paul
 */
public class CategorizerTest {
    @Test
    public void normalizeUrlTest() {
        String normalized = Categorizer.normalizeUrl("https://google.com/");
        assertEquals("google.com", normalized);
        
        normalized = Categorizer.normalizeUrl("https://www.google.com/");
        assertEquals("google.com", normalized);
        
        normalized = Categorizer.normalizeUrl("http://google.com/");
        assertEquals("google.com", normalized);
        
        normalized = Categorizer.normalizeUrl("http://www.google.com/");
        assertEquals("google.com", normalized);
    }
    
    @Test
    public void normalizeNameTest() {
        String normalized = Categorizer.normalizeName("Ab CD_e");
        assertEquals("ab%cd%e", normalized);
    }
}
