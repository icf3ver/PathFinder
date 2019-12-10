package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class IntPair{
    public int x = 0;
    public int y = 0;

    IntPair(int x, int y){
        this.x = x;
        this.y = y;
    }
}

public class Map {
    public static int mapWidth = 10;
    public static int mapHeight = 10;
    public static int availableSpots = 0;
    public static int startX = 0;
    public static int startY = 0;
    public static int endX = mapWidth-1;
    public static int endY = mapHeight-1;
    private static int[][] map = new int[mapWidth][mapHeight];
    private static List<IntPair> OpenList = new ArrayList<IntPair>();
    private static List<IntPair> StaticClosedList = new ArrayList<IntPair>();
    private static List<IntPair> ClosedList = new ArrayList<IntPair>();
    private static int[][] direction = new int[mapWidth][mapHeight];
    private static int[][] F = new int[mapWidth][mapHeight];

    public static void toggleWall(int x, int y){
        availableSpots = (mapWidth * mapHeight) - StaticClosedList.size();
        if(map[x][y] == 0 || map[x][y] == 4){
            map[x][y] = 1;
            StaticClosedList.add(new IntPair(x,y));
        }else if(map[x][y] == 1){
            map[x][y] = 0;
            StaticClosedList.removeIf(n -> ( ( n.x == x ) && ( n.y == y ) ));
        }

    }

    public static void reset(){
        StaticClosedList.clear();
        setup();
    }

    public static void setup(){
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                map[y][x] = 0;
            }
        }
        for(int i = 0; i < StaticClosedList.size(); i++){
            map[StaticClosedList.get(i).x][StaticClosedList.get(i).y] = 1;
        }
        OpenList.clear();
        ClosedList.clear();
        ClosedList.addAll(StaticClosedList);
        for (int y = 0; y < F.length; y++){
            for (int x = 0; x < F[0].length; x++){
                F[y][x] = 0;
            }
        }


        //start and end
        map[startX][startY] = 2;
        map[endX][endY] = 3;

        //Walls

        //calculate F for positions
        if (analise()) {
            //generate path
            IntPair tmp = generatePath(endX, endY);
            while (tmp != null) {
                tmp = generatePath(tmp.x, tmp.y);
            }
            render();
        }
    }

    private static boolean analise(){
        choose(0,0);
        int available = 100;
        while (!contains(ClosedList, new IntPair(endX,endY)) && available > 0){
            available -= 1;
            int tmp = OpenList.size();
            for (int i = 0; i < tmp; i++) {
                choose(OpenList.get(i).x, OpenList.get(i).y);
            }
        }
        if (!contains(ClosedList, new IntPair(endX,endY))){
            reset();
            return false;
        }

        return true;
    }

    private static void choose(int posX, int posY){
        IntPair pos = new IntPair(posX, posY);
        OpenList.remove(pos);
        ClosedList.add(pos);

        if (posX > 0) {
            IntPair a = new IntPair(posX - 1, posY);
            if (!contains(OpenList, a) && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 1;
                F[a.x][a.y] = F[pos.x][pos.y] + 10;
            } else if (F[pos.x][pos.y] + 10 < F[a.x][a.y] && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 1;
                F[a.x][a.y] = F[pos.x][pos.y] + 10;
            }
        }
        if (posX > 0 && posY + 1 < mapHeight){
            IntPair a = new IntPair(posX - 1, posY + 1);
            if(!contains(OpenList, a) && !contains(ClosedList, a)){
                OpenList.add(a);
                direction[a.x][a.y] = 2;
                F[a.x][a.y] = F[pos.x][pos.y] + 13;
            }else if(F[pos.x][pos.y] + 13 < F[a.x][a.y] && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 2;
                F[a.x][a.y] = F[pos.x][pos.y] + 13;
            }
        }
        if (posY + 1 < mapHeight){
            IntPair a = new IntPair(posX, posY + 1);
            if(!contains(OpenList, a) && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 3;
                F[a.x][a.y] = F[pos.x][pos.y] + 10;
            }else if(F[pos.x][pos.y] + 10 < F[a.x][a.y] && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 3;
                F[a.x][a.y] = F[pos.x][pos.y] + 10;
            }
        }
        if (posY + 1 < mapHeight && posX + 1 < mapWidth){
            IntPair a = new IntPair(posX + 1, posY + 1);
            if(!contains(OpenList, a) && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 4;
                F[a.x][a.y] = F[pos.x][pos.y] + 13;
            }else if(F[pos.x][pos.y] + 13 < F[a.x][a.y] && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 4;
                F[a.x][a.y] = F[pos.x][pos.y] + 13;
            }
        }
        if (posX + 1 < mapWidth){
            IntPair a = new IntPair(posX + 1, posY);
            if(!contains(OpenList, a) && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 5;
                F[a.x][a.y] = F[pos.x][pos.y] + 10;
            }else if(F[pos.x][pos.y] + 10 < F[a.x][a.y] && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 5;
                F[a.x][a.y] = F[pos.x][pos.y] + 10;
            }
        }
        if (posX + 1 < mapWidth && posY > 0){
            IntPair a = new IntPair(posX + 1, posY - 1);
            if(!contains(OpenList, a) && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 6;
                F[a.x][a.y] = F[pos.x][pos.y] + 13;
            }else if(F[pos.x][pos.y] + 13 < F[a.x][a.y] && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 6;
                F[a.x][a.y] = F[pos.x][pos.y] + 13;
            }
        }
        if (posY > 0){
            IntPair a = new IntPair(posX, posY - 1);
            if(!contains(OpenList, a) && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 7;
                F[a.x][a.y] = F[pos.x][pos.y] + 10;
            }else if(F[pos.x][pos.y] + 10 < F[a.x][a.y] && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 7;
                F[a.x][a.y] = F[pos.x][pos.y] + 10;
            }
        }
        if (posY > 0 && posX > 0){
            IntPair a = new IntPair(posX - 1, posY - 1);
            if(!contains(OpenList, a) && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 8;
                F[a.x][a.y] = F[pos.x][pos.y] + 13;
            }else if(F[pos.x][pos.y] + 13 < F[a.x][a.y] && !contains(ClosedList, a)) {
                OpenList.add(a);
                direction[a.x][a.y] = 8;
                F[a.x][a.y] = F[pos.x][pos.y] + 13;
            }
        }
    }

    private static IntPair generatePath(int x, int y){
        int sX = x;
        int sY = y;
        if (x > 0) {
            if (F[x - 1][y] < F[sX][sY] && map[x - 1][y] != 1){
                sX = x - 1;
                sY = y;
            }
        }
        if (x > 0 && y + 1 < mapHeight){
            if (F[x - 1][y + 1] < F[sX][sY] && map[x - 1][y + 1] != 1){
                sX = x - 1;
                sY = y + 1;
            }
        }
        if (y + 1 < mapHeight){
            if (F[x][y + 1] < F[sX][sY] && map[x][y + 1] != 1){
                sX = x;
                sY = y + 1;
            }
        }
        if (y + 1 < mapHeight && x + 1 < mapWidth){
            if (F[x + 1][y + 1] < F[sX][sY] && map[x + 1][y + 1] != 1){
                sX = x + 1;
                sY = y + 1;
            }
        }
        if (x + 1 < mapWidth){
            if (F[x + 1][y] < F[sX][sY] && map[x + 1][y] != 1){
                sX = x + 1;
                sY = y;
            }
        }
        if (x + 1 < mapWidth && y > 0){
            if (F[x + 1][y - 1] < F[sX][sY] && map[x + 1][y - 1] != 1){
                sX = x + 1;
                sY = y - 1;
            }
        }
        if (y > 0){
            if (F[x][y - 1] < F[sX][sY] && map[x][y - 1] != 1){
                sX = x;
                sY = y - 1;
            }
        }
        if (y > 0 && x > 0){
            if (F[x - 1][y - 1] < F[sX][sY] && map[x - 1][y - 1] != 1){
                sX = x - 1;
                sY = y - 1;
            }
        }

        if (map[sX][sY] != 2 && map[sX][sY] != 1){
            map[sX][sY] = 4;
            return new IntPair(sX, sY);
        }else{
            return null;
        }
    }

    public static void render(){
        Window.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                Window.shapeRenderer.setColor(getColor(map[x][y]));
                Window.shapeRenderer.rect((x*30) + 10, (y*30) + 10,25,25);
            }
        }
        Window.shapeRenderer.end();
    }

    private static Color getColor(int number){
        switch (number){
            case 0:
                return(new Color(1,1,1,1));
            case 1:
                return(new Color(0,0,0,1));
            case 2:
                return(new Color(1,0,0,1));
            case 3:
                return(new Color(0,1,0,1));
            default :
                return(new Color(0,0,1,1));
        }
    }
    
    private static boolean contains(List<IntPair> list, IntPair a){
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).x == a.x && list.get(i).y == a.y){
                return true;
            }
        }
        return false;
    }
}
