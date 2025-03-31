import java.util.Arrays;

public class Populacao {
    public Rota[] individuos;

    public Populacao(Rota[] individuos) {
        this.individuos = individuos;
        Arrays.sort(individuos);
    }

    // retorna os 2 melhores indivíduos
    public Rota[] getPais() {
        return null;
    }

    public Rota getMelhorIndividuo() {
        return individuos[0];
    }

    public Rota[] getIndividuos() {
        return this.individuos;
    }

    public void setIndividuo(int index, Rota individuo) {
        individuos[index] = individuo;
        Arrays.sort(individuos);
    }
}
