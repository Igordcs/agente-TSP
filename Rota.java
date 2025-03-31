// cada indivíduo vai ser representado como uma rota

import java.util.ArrayList;
import java.util.List;

public class Rota implements Comparable<Rota> {
    public List<Integer> cidades = new ArrayList<>();
    public int distancia;

    public Rota(List<Integer> rota, int distancia) {
        this.cidades = rota;
        this.distancia = distancia;
    }

    @Override
    public int compareTo(Rota other) {
        return Integer.compare(this.distancia, other.distancia);
    }
}
