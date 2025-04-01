import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AgenteGenetico {
    private int[][] matrizDistancias;
    private List<Integer> cidadesEntrada = new ArrayList<Integer>();
    private int TAMANHO_POPULACAO = 500; // rever essas constantes
    private double PROBABILIDADE_MUTACAO = 0.15; // rever essas constantes
    private int QUANTIDADE_REPETICOES = 300;
    private int TAMANHO_TORNEIO = 5;
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

    // Gera uma rota com as cidades de entrada + cidades aleatórias
    public Rota geraRotaAleatoria() {
        Set<Integer> cidadesSet = new LinkedHashSet<>(this.cidadesEntrada);

        int cidadeInicial = this.cidadesEntrada.get(0);
        int tamanhoRota = aleatorio.nextInt(matrizDistancias.length - (1 + cidadesSet.size())) + 1;

        while (cidadesSet.size() < tamanhoRota) {
            int cidadeAleatoria = aleatorio.nextInt(tamanhoRota) + 1;
            cidadesSet.add(cidadeAleatoria);
        }

        List<Integer> rota = new ArrayList<>(cidadesSet);

        // embaralha as cidades intermediárias
        if (rota.size() > 2) {
            Collections.shuffle(rota.subList(1, rota.size()));
        }

        // garante que termina e começa com a mesma cidade
        rota.add(cidadeInicial);

        return new Rota(rota, calcularDistancia(rota));
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
        int geracao = 0;

        // evolui a população até X gerações convergirem pra um mesmo valor
        while (geracao < QUANTIDADE_REPETICOES) {
            populacao = evoluirPopulacao(populacao);
            melhorIndividuo = populacao.getMelhorIndividuo();
            geracao++;
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
    public Populacao evoluirPopulacao(Populacao _populacao) {
        // elitismo (vamo manter os 2 melhores de cada geração antes de evoluir)
        Rota[] antigosIndividuos = _populacao.getIndividuos();
        Rota[] novosIndividuos = new Rota[TAMANHO_POPULACAO];
        novosIndividuos[0] = _populacao.individuos[0];
        novosIndividuos[1] = _populacao.individuos[1];

        for (int i = 1; i < TAMANHO_POPULACAO; i++) {
            Rota pai = selecionaPai(antigosIndividuos);
            Rota pai2 = selecionaPai(antigosIndividuos);

            Rota filho = cruzaIndividuos(pai, pai2);

            if (aleatorio.nextDouble() < PROBABILIDADE_MUTACAO) {
                filho = mutaIndividuo(filho);
            }

            filho.distancia = calcularDistancia(filho.cidades);
            novosIndividuos[i] = filho;
        }

        return new Populacao(novosIndividuos);
    }

    public Rota selecionaPai(Rota[] individuos) {
        // seleção por torneio: escolher o mais apto entre K indivíduos aleatórios
        Set<Integer> indices = new HashSet<>();
        Rota[] candidatos = new Rota[TAMANHO_TORNEIO];

        // seleciona candidatos distintos
        while (indices.size() < TAMANHO_TORNEIO && indices.size() < individuos.length) {
            int indiceCandidato = aleatorio.nextInt(individuos.length);

            if (!indices.contains(indiceCandidato)) {
                candidatos[indices.size()] = individuos[indiceCandidato];
                indices.add(indiceCandidato);
            }
        }

        Arrays.sort(candidatos);
        return candidatos[0];
    }

    public Rota cruzaIndividuos(Rota pai1, Rota pai2) {
        List<Integer> cidadesFilho = new ArrayList<>();
        List<Integer> cidadesPai1 = new ArrayList<>(pai1.cidades);
        List<Integer> cidadesPai2 = new ArrayList<>(pai2.cidades);

        int tamanho = cidadesPai1.size() - 1; // Ignora a última cidade (repetição da primeira)
        int pontoCorte = aleatorio.nextInt(tamanho - 1) + 1; // Ponto entre 1 e tamanho-1

        // 1. Copia a primeira parte do pai1 (até o ponto de corte)
        for (int i = 0; i < pontoCorte; i++) {
            cidadesFilho.add(cidadesPai1.get(i));
        }

        // 2. Completa com as cidades do pai2 que não estão no filho ainda
        for (int i = 0; i < cidadesPai2.size(); i++) {
            int cidade = cidadesPai2.get(i);
            if (!cidadesFilho.contains(cidade)) {
                cidadesFilho.add(cidade);
            }
        }

        // 3. Garante que termina na cidade inicial
        cidadesFilho.add(cidadesFilho.get(0));

        return new Rota(cidadesFilho, calcularDistancia(cidadesFilho));
    }

    public Rota mutaIndividuo(Rota individuo) {
        // gera 2 números aleatórios entre o início e fim e troca as cidades de posição
        List<Integer> cidadesIndividuo = new ArrayList<>(individuo.cidades);
        int tamanho = cidadesIndividuo.size() - 1;
        if (tamanho > 1) {
            int aleatorio1 = aleatorio.nextInt(tamanho - 1) + 1;
            int aleatorio2 = aleatorio.nextInt(tamanho - 1) + 1;

            // evitar que seja a mesma posição
            while (aleatorio1 == aleatorio2) {
                aleatorio2 = aleatorio.nextInt(tamanho - 1) + 1;
            }

            Collections.swap(cidadesIndividuo, aleatorio1, aleatorio2);
            cidadesIndividuo.set(cidadesIndividuo.size() - 1, cidadesIndividuo.get(0));
        }
        return new Rota(cidadesIndividuo, calcularDistancia(cidadesIndividuo));
    }
}
