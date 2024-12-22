package uvigo.tfgalmacen.almacenUtilities;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class MisElementoGraficos {

    static Box paralelepipedo;

    public static Box CreaParalelepipedo(int ancho, int alto, int profundo, int Posx, int Posy, int Posz, Color color) {
        //EL origen esta en la esquina inferior izquierda

        paralelepipedo = new Box(ancho, alto, profundo);

        paralelepipedo.setMaterial(new PhongMaterial(color));
        paralelepipedo.setTranslateX(Posy + (double) -ancho / 2);
        paralelepipedo.setTranslateY(Posz + (double) alto / 2);
        paralelepipedo.setTranslateZ(Posx + (double) -profundo / 2);


        return paralelepipedo;
    }


}
