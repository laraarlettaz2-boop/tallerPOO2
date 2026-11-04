package elnino;

import elnino.servicio.DatosDePrueba;
import elnino.servicio.Sistema;
import elnino.ui.MenuPrincipal;

public class Main {
    public static void main(String[] args) {
        Sistema sistema = new Sistema();
        DatosDePrueba.cargar(sistema);
        new MenuPrincipal(sistema).iniciar();
    }
}
