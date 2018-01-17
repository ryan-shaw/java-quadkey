import org.junit.Test;
import rshaw.QuadKey;
import rshaw.TileSystem;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

/**
 * @author Ryan Shaw
 */

public class QuadKeyTest {

    @Test
    public void testChildren(){
        QuadKey qk = new QuadKey("031313131221322200013");
        List<QuadKey> list = qk.children();
        List<QuadKey> expected = new ArrayList<>();
        expected.add(new QuadKey("0313131312213222000130"));
        expected.add(new QuadKey("0313131312213222000131"));
        expected.add(new QuadKey("0313131312213222000132"));
        expected.add(new QuadKey("0313131312213222000133"));
        assertEquals(4, list.size());
        for(int i = 0; i < 4; i++){
            assertEquals(expected.get(i).toString(), list.get(i).toString());
        }
    }

    @Test
    public void testParent(){
        QuadKey qk = new QuadKey("031313131221322200013");
        assertEquals("03131313122132220001", qk.parent().toString());
    }

    @Test
    public void testUnwind(){
        QuadKey qk = new QuadKey("03131");
        List<QuadKey> actual = qk.unwind();

        assertEquals("0313", actual.get(0).toString());
        assertEquals("031", actual.get(1).toString());
        assertEquals("03", actual.get(2).toString());
        assertEquals("0", actual.get(3).toString());
    }

    @Test
    public void testToTile(){
        QuadKey qk = new QuadKey("0313");
        TileSystem.TileCoord tc = qk.to_tile();
        assertEquals(7, tc.x);
        assertEquals(5, tc.y);
    }

}
