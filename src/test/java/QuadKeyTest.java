import org.junit.Test;
import rshaw.QuadKey;
import rshaw.TileSystem;

import java.awt.font.NumericShaper.Range;
import java.nio.channels.AcceptPendingException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

/**
 * @author Ryan Shaw
 */

public class QuadKeyTest {

    @Test
    public void testInit0(){
        QuadKey qk = QuadKey.from_str("0321201120");
    }

    @Test(expected = TileSystem.KeyNotValid.class)
    public void testInit1(){
        QuadKey qk = QuadKey.from_str("");
    }

    @Test(expected = TileSystem.KeyNotValid.class)
    public void testInit2(){
        QuadKey qk = QuadKey.from_str("0156510012");
    }

    @Test
    public void testFromGeo(){
        double[] geo = new double[]{40, -105};
        int level = 7;
        QuadKey qk = QuadKey.from_str("0231010");
        assertEquals(qk, QuadKey.from_geo(geo, level));
    }

    @Test
    public void testEquality(){
        QuadKey one = QuadKey.from_str("00");
        QuadKey two = QuadKey.from_str("00");
        assertEquals(one, two);
        QuadKey three = QuadKey.from_str("0");
        assertNotEquals(one, three);
    }

    @Test
    public void testChildren(){
        QuadKey qk = QuadKey.from_str("0");
        List<QuadKey> expected = new ArrayList<>();
        expected.add(new QuadKey("00"));
        expected.add(new QuadKey("01"));
        expected.add(new QuadKey("02"));
        expected.add(new QuadKey("03"));
        List<QuadKey> actual = qk.children();
        for(int i = 0; i < actual.size(); i++){
            assertEquals(expected.get(i), actual.get(i));
        }
        qk = QuadKey.from_str("00000000000000000000000");
        assertEquals(new ArrayList<QuadKey>(), qk.children());
    }

    @Test
    public void testAncestry(){
        QuadKey one = QuadKey.from_str("0");
        QuadKey two = QuadKey.from_str("0101");
        assertEquals((Integer) 3, one.is_descendent(two));
        assertNull(two.is_descendent(one));
        assertEquals((Integer) 3, two.is_ancestor(one));
        QuadKey three = QuadKey.from_str("1");
        assertNull(three.is_ancestor(one));
    }

    @Test(expected = RuntimeException.class)
    public void testNearby(){
        QuadKey qk = QuadKey.from_str("0");
        String[] expected = new String[]{"1", "2", "3"};
        assertArrayEquals(expected, qk.nearby());
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
        int[] tc = qk.to_tile();
        assertEquals(7, tc[0]);
        assertEquals(5, tc[1]);
        assertEquals(4, tc[2]);
    }

}
