import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class AgenteGenetico {
    private int[][] matrizDistancias;
    private List<Integer> cidadesEntrada = new ArrayList<Integer>();
    private int TAMANHO_POPULACAO = 500; // rever essas constantes
    private double PROBABILIDADE_MUTACAO = 0.7; // rever essas constantes
    private int QUANTIDADE_REPETICOES = 25;
    private Stack<Integer> pilhaDistancia = new Stack<Integer>();
    private Random aleatorio = new Random();

    public AgenteGenetico(int[][] distancias, List<Integer> cidades) {
        this.matrizDistancias = distancias;
        this.cidadesEntrada = cidades;
    }

    // inicializa a população com rotas aleatórias
    public Populacao inicializaPopulacao() {
        Rota[] individuos = new Rota[TAMANHO_POPULACAO];
        for (int i = 0; i < TAMANHO_POPULACAO; i++) {
            individuos[i] = geraRotaAleatoria();
        }
        return new Populacao(individuos);
    }

    public Rota geraRotaAleatoria() {
        List<Integer> rota = new ArrayList<>();
        int cidadeInicial = this.cidadesEntrada.get(0);
        for (int cidade : this.cidadesEntrada) {
            rota.add(cidade);
        }

        int tamanhoRota = aleatorio.nextInt(matrizDistancias.length - (1 + rota.size())) + 1;
        for (int i = 0; i < tamanhoRota; i++) {
            int cidadeAleatoria = aleatorio.nextInt(tamanhoRota) + 1;
            rota.add(cidadeAleatoria);
        }

        // aplica um shuffle nas cidades intermediárias
        if (rota.size() > 2) {
            Collections.shuffle(rota.subList(1, rota.size()));
        }

        // garante que termina e começa com a mesma cidade
        rota.add(cidadeInicial);
        int distancia = calcularDistancia(rota);
        return new Rota(rota, distancia);
    }

    public int calcularDistancia(List<Integer> cidades) {
        Map<Integer, Integer> contadorCidades = new HashMap<>();
        int cidadeInicial = this.cidadesEntrada.get(0);
        int distanciaTotal = 0;

        // contar quantas vezes aparece cada cidade
        for (int cidade : cidades) {
            contadorCidades.put(cidade, contadorCidades.getOrDefault(cidade, 0) + 1);
        }

        for (int i = 0; i < cidades.size() - 1; i++) {
            int cidadeAtual = cidades.get(i);
            int proximaCidade = cidades.get(i + 1);

            if (cidadeAtual >= 0 && cidadeAtual < matrizDistancias.length &&
                    proximaCidade >= 0 && proximaCidade < matrizDistancias[0].length) {
                int qtdApareceu = contadorCidades.get(cidadeAtual);
                distanciaTotal += matrizDistancias[cidadeAtual][proximaCidade];

                // verifica se a cidadeInicial se repetiu no meio da rota
                if (cidadeAtual == cidadeInicial && qtdApareceu > 2) {
                    int multiplicador = qtdApareceu - 2;
                    distanciaTotal += 10000 * multiplicador;
                }

                // verifica se a cidadeAtual se repetiu
                if (cidadeAtual != cidadeInicial && qtdApareceu > 1) {
                    distanciaTotal += 10000 * qtdApareceu;
                }
            } else {
                // Punição para cidades inválidas (MAIOR QUE A DE CIDADE REPETIDA)
                distanciaTotal += 100000;
            }
        }
        return distanciaTotal;
    }

    public void resolver() {
        // inicializa a população
        Populacao populacao = inicializaPopulacao();
        Rota melhorIndividuo = null;

        // evolui a população até X gerações convergirem pra um mesmo valor
        while (pilhaDistancia.size() < QUANTIDADE_REPETICOES) {
            // populacao = evoluirPopulacao();
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
        if (pilhaDistancia.empty() || pilhaDistancia.peek() != distancia) {
            pilhaDistancia.clear();
        }
        pilhaDistancia.add(distancia);
    }
}
