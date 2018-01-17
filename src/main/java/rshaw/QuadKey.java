package rshaw;

import java.util.ArrayList;
import java.util.List;

import rshaw.TileSystem.PixelCoord;
import rshaw.TileSystem.TileCoord;

/**
 * @author Ryan Shaw
 */

public class QuadKey {

    private String key;
    private int level;

    /**
     * A quadkey must be between 1 and 23 digits and only contain digit[0-3]
     */
    public QuadKey(String key){
        if(!TileSystem.valid_key(key)){
            throw new TileSystem.KeyNotValid();
        }
        this.key = key;
        this.level = key.length();
    }

    public List<QuadKey> children(){
        List<QuadKey> list = new ArrayList<>();
        if(this.level >= 23){
            return list;
        }

        for(int i = 0; i < 4; i++){
            list.add(new QuadKey(this.key + i));
        }
        return list;
    }

    public QuadKey parent(){
        return new QuadKey(this.key.substring(0, this.key.length() - 1));
    }

    public String[] nearby(){
        throw new RuntimeException("Not Implemented");
    }

    public Integer is_ancestor(QuadKey node){
        if(this.level <= node.level || !this.key.substring(0, node.key.length()).equals(node.key)){
            return null;
        }
        return this.level - node.level;
    }

    public Integer is_descendent(QuadKey node){
        return node.is_ancestor(this);
    }

    public double area(){
        long size = TileSystem.map_size(this.level);
        int LAT = 0;
        double res = TileSystem.ground_resolution(LAT, this.level);
        double side = (size / 2) * res;
        return side * side;
    }

    public double xdifference(QuadKey to){
        throw new RuntimeException("Not Implemented");
    }

    public double difference(QuadKey to){
        throw new RuntimeException("Not Implemented");
    }

    public List<QuadKey> unwind(){
        List<QuadKey> keys = new ArrayList<>();
        for(int i = this.key.length() - 1 ; i > 0; i--){
            keys.add(new QuadKey(this.key.substring(0, i)));
        }
        return keys;
    }

    public TileCoord to_tile(){
        return TileSystem.quadkey_to_tile(this.key);
    }

    public double[] to_geo(){
        return to_geo(false);   
    }

    public double[] to_geo(boolean centered){
        TileCoord tc = TileSystem.quadkey_to_tile(this.key);
        PixelCoord pixel = TileSystem.tile_to_pixel(tc);
        return TileSystem.pixel_to_geo(pixel, tc.level);

    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof QuadKey)){
            return false;
        }
        return obj.toString().equals(this.toString());
    }

    public static QuadKey from_geo(double[] coords, int level){
        PixelCoord pixel = TileSystem.geo_to_pixel(coords[0], coords[1], level);
        TileCoord tile = TileSystem.pixel_to_tile(pixel);
        String key = TileSystem.tile_to_quadkey(tile, level);
        return new QuadKey(key);
    }

    public static QuadKey from_str(String qk_str){
        return new QuadKey(qk_str);
    }
}
