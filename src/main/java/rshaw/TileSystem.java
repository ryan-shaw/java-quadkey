package rshaw;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ryan Shaw
 */

public class TileSystem {

    public static class LevelNotValid extends RuntimeException{

    }

    public static class KeyNotValid extends RuntimeException{

    }

    private static final int EARTH_RADIUS = 6378137;

    private static final double[] LATITUDE_RANGE = new double[]{
        -85.05112878, 85.05112878
    };

    private static final double[] LONGITUDE_RANGE = new double[]{
        -180., 180.
    };

    public static boolean valid_key(String key){
        Pattern p = Pattern.compile("^[0-3]+$");
        Matcher m = p.matcher(key);
        return m.matches();
    }

    public static double clip(double n, double[] minMax){
        if(!(minMax[0] <= minMax[1])){
            //TODO: Fix?
        }
        return Math.min(Math.max(n, minMax[0]), minMax[1]);
    }

    public static long map_size(long level) {
        if(!(1 <= level && level <= 23)){
            throw new LevelNotValid();
        }
        return 256L << level;
    }

    public static double ground_resolution(double lat, int level){
        if(!(1 <= level && level <= 23)){
            throw new LevelNotValid();
        }
        lat = clip(lat, TileSystem.LATITUDE_RANGE);
        return Math.cos(lat * Math.PI / 180) * 2 * Math.PI * TileSystem.EARTH_RADIUS / TileSystem.map_size(level);
    }

    public static int[] geo_to_pixel(double lat, double lon, int level) {
        if(!(1 <= level && level <= 23)){
            throw new LevelNotValid();
        }
        lat = clip(lat, LATITUDE_RANGE);
        lon = clip(lon, LONGITUDE_RANGE);

        double x = (lon + 180) / 360;
        double sin_lat = Math.sin(lat * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sin_lat) / (1 - sin_lat)) / (4 * Math.PI);
        long map_size = map_size(level);

        int pixel_x = (int) clip(x * map_size + 0.5, new double[]{0, map_size - 1});
        int pixel_y = (int) clip(y * map_size + 0.5, new double[]{0, map_size - 1});

        return new int[]{pixel_x, pixel_y};
    }

    public static double[] pixel_to_geo(int[] pixelCoord, int level){
        if(!(1 <= level && level <= 23)){
            throw new LevelNotValid();
        }
        int pixel_x = pixelCoord[0];
        int pixel_y = pixelCoord[1];
        float map_size = (float) map_size(level);
        double x = (clip(pixel_x, new double[]{0, map_size - 1}) / map_size) - 0.5;
        double y = 0.5 - (clip(pixel_y, new double[]{0, map_size - 1}) / map_size);

        double lat = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
        double lon = 360 * x;

        return new double[]{
                Math.round(lat * 1000000.0) / 1000000.0, Math.round(lon * 1000000.0) / 1000000.0
        };
    }

    /**
     * Transform pixel to tile coordinates
     */
    public static int[] pixel_to_tile(int[] pixelCoord){
        return new int[]{
            pixelCoord[0] / 256,
            pixelCoord[1] / 256,
        };
    }

    /**
     * Transform tile coordinates to pixel coordinates
     */
    public static int[] tile_to_pixel(int[] tileCoord){
        return tile_to_pixel(tileCoord);
    }

    /**
     * Transform tile coordinates to pixel coordinates
     */
    public static int[] tile_to_pixel(int[] tileCoord, boolean centered){
        int x = tileCoord[0] * 256;
        int y = tileCoord[1] * 256;
        if(centered){
            x += 128;
            y += 128;
        }
        return new int[]{x, y};
    }

    /**
     * Transform tile coordinates to quadkey
     */
    public static String tile_to_quadkey(int[] tileCoord, int level){
        if(!(1 <= level && level <= 23)){
            throw new LevelNotValid();
        }
        String quadkey = "";
        for(int i = 0; i < level; i++){
            int bit = level - i;
            int digit = (int) '0';
            int mask = 1 << (bit - 1);
            if((tileCoord[0] & mask) != 0){
                digit += 1;
            }
            if((tileCoord[1] & mask) != 0){
                digit += 2;
            }
            quadkey += (char) digit;
        }
        return quadkey;
    }

    /**
     * Transform quadkey to tile coordinates
     */
    public static int[] quadkey_to_tile(String quadkey){
        if (!valid_key(quadkey)) {
            throw new KeyNotValid();
        }
        int x = 0;
        int y = 0;
        int level = quadkey.length();
        for(int i = 0; i < level; i++){
            int bit = level -i;
            int mask = 1 << (bit - 1);
            if(quadkey.charAt(level - bit) == '1'){
                x |= mask;
            }
            if(quadkey.charAt(level - bit) == '2'){
                y |= mask;
            }
            if(quadkey.charAt(level - bit) == '3'){
                x |= mask;
                y |=  mask;
            }
        }
        return new int[]{x, y, level};
    }
}
