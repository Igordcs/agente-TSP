import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AgenteGenetico {
    private int[][] matrizDistancias;
    private List<Integer> cidadesEntrada = new ArrayList<Integer>();
    private int TAMANHO_POPULACAO = 500; // rever essas constantes
    private double PROBABILIDADE_MUTACAO = 0.7; // rever essas constantes
    private int QUANTIDADE_REPETICOES = 25;
    private Stack<Integer> pilhaDistancia = new Stack<Integer>();

    public AgenteGenetico(int[][] distancias, List<Integer> cidades) {
        this.matrizDistancias = distancias;
        this.cidadesEntrada = cidades;
    }

    // inicializa a população com rotas aleatórias
    public Populacao inicializaPopulacao() {
        return null;
    }

    public void resolver() {
        // inicializa a população
        Populacao populacao = inicializaPopulacao();
        Rota melhorIndividuo = null;

        // evolui a população até X gerações convergirem pra um mesmo valor
        while (pilhaDistancia.size() < QUANTIDADE_REPETICOES) {
            populacao = evoluirPopulacao();
            melhorIndividuo = populacao.getMelhorIndividuo();
            adicionarDistancia(melhorIndividuo.distancia);
        }

        if (melhorIndividuo == null) {
            System.out.println("Não foi possível gerar a melhor rota contendo essas cidades");
            return;
        }

        for (int cidade : melhorIndividuo.cidades) {
            System.out.printf("%d; ", cidade);
        }
        System.out.println("Menor distância: " + melhorIndividuo.distancia);
    }

    // seleciona os pais e gera uma nova população de acordo com esses 2 pais
    public Populacao evoluirPopulacao() {
        return null;
    }

    public void adicionarDistancia(int distancia) {
        // distancia for diferente, limpa a pilha pois a geração não está convergindo
        if (pilhaDistancia.empty() && pilhaDistancia.peek() != distancia) {
            pilhaDistancia.empty();
        }
        pilhaDistancia.add(distancia);
    }
}
