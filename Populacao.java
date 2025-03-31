import java.util.Arrays;

public class Populacao {
    public Rota[] individuos;

    public Populacao(Rota[] individuos) {
        this.individuos = individuos;
        Arrays.sort(individuos);
    }

    public Rota getMelhorIndividuo() {
        return this.individuos[0];
    }

    public Rota[] getIndividuos() {
        return this.individuos;
    }

    public void setIndividuo(int index, Rota individuo) {
        individuos[index] = individuo;
        Arrays.sort(individuos);
    }
}
