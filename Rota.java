// cada indivíduo vai ser representado como uma rota

import java.util.ArrayList;
import java.util.List;

public class Rota implements Comparable<Rota> {
    public List<Integer> cidades = new ArrayList<>();
    public long distancia;

    public Rota(List<Integer> rota, long distancia) {
        this.cidades = rota;
        this.distancia = distancia;
    }

    @Override
    public int compareTo(Rota other) {
        return Long.compare(this.distancia, other.distancia);
    }
}
