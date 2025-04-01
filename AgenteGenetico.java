import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class AgenteGenetico {
    private int[][] matrizDistancias;
    private List<Integer> cidadesEntrada = new ArrayList<Integer>();
    private int TAMANHO_POPULACAO = 500;
    private double PROBABILIDADE_CROSSOVER = 0.8;
    private int QUANTIDADE_REPETICOES = 300;
    private int TAMANHO_TORNEIO = 5;
    private Random random = new Random();

    public AgenteGenetico(int[][] distancias) {
        this.matrizDistancias = distancias;
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
        int tamanhoRota = random.nextInt(matrizDistancias.length - (1 + cidadesSet.size())) + 1;

        // adiciona cidades até atingir o tamanho da rota (sem cidades repetidas)
        while (cidadesSet.size() < tamanhoRota) {
            int cidadeAleatoria = random.nextInt(matrizDistancias.length) + 1;
            cidadesSet.add(cidadeAleatoria);
        }

        List<Integer> rota = new ArrayList<>(cidadesSet);

        if (rota.size() > 2) {
            Collections.shuffle(rota.subList(1, rota.size()));
        }

        rota.add(rota.get(0));
        return new Rota(rota, calcularDistancia(rota));
    }

    public int calcularDistancia(List<Integer> cidades) {
        int cidadeAnterior = cidades.get(0);
        int distanciaTotal = 0;

        for (int i = 1; i < cidades.size(); i++) {
            int cidadeAtual = cidades.get(i);

            if (cidadeAnterior < 0 || cidadeAnterior >= matrizDistancias.length ||
                    cidadeAtual < 0 || cidadeAtual >= matrizDistancias[0].length) {
                return Integer.MAX_VALUE; // Rota inválida
            }

            distanciaTotal += matrizDistancias[cidadeAnterior][cidadeAtual];
            cidadeAnterior = cidadeAtual;
        }

        return distanciaTotal;
    }

    public void resolver(List<Integer> cidades) {
        this.cidadesEntrada = cidades;

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
    public Populacao evoluirPopulacao(Populacao populacao) {
        Rota[] antigosIndividuos = populacao.getIndividuos();
        Rota[] novosIndividuos = new Rota[TAMANHO_POPULACAO];

        // elitismo: manter os 2 melhores de cada geração antes de evoluir
        for (int i = 0; i < 2; i++) {
            novosIndividuos[i] = antigosIndividuos[i];
        }

        for (int i = 2; i < TAMANHO_POPULACAO; i++) {
            Rota pai1 = selecionaPai(antigosIndividuos);
            Rota pai2 = selecionaPai(antigosIndividuos);

            boolean crossover = random.nextDouble() < PROBABILIDADE_CROSSOVER;
            Rota filho = crossover ? cruzaIndividuos(pai1, pai2) : pai1;

            filho = mutaIndividuo(filho);
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
            int indiceCandidato = random.nextInt(individuos.length);

            if (!indices.contains(indiceCandidato)) {
                candidatos[indices.size()] = individuos[indiceCandidato];
                indices.add(indiceCandidato);
            }
        }

        Arrays.sort(candidatos);
        return candidatos[0];
    }

    public Rota cruzaIndividuos(Rota pai1, Rota pai2) {
        List<Integer> cidadesPai1 = new ArrayList<>(pai1.cidades);
        List<Integer> cidadesPai2 = new ArrayList<>(pai2.cidades);
        LinkedHashSet<Integer> cidadesFilhoSet = new LinkedHashSet<>();

        int tamanho = cidadesPai1.size() - 1; // Ignora a última cidade (repetição da primeira)
        int pontoCorte = random.nextInt(tamanho - 1) + 1; // Ponto entre 1 e tamanho - 1

        // 1. Copia a primeira parte do pai1 (até o ponto de corte)
        cidadesFilhoSet.addAll(cidadesPai1.subList(0, pontoCorte));

        // 2. Adiciona cidades do pai2 que não estão no filho ainda
        for (int cidade : cidadesPai2) {
            if (!cidadesFilhoSet.contains(cidade)) {
                cidadesFilhoSet.add(cidade);
            }
        }

        // 3. Adiciona a cidade inicial no final
        List<Integer> cidadesFilho = new ArrayList<>(cidadesFilhoSet);
        cidadesFilho.add(cidadesFilho.get(0));

        return new Rota(cidadesFilho, calcularDistancia(cidadesFilho));
    }

    public Rota mutaIndividuo(Rota individuo) {
        List<Integer> cidadesIndividuo = new ArrayList<>(individuo.cidades);
        int tamanho = cidadesIndividuo.size();
        double probabilidadeMutacao = 1.0 / (tamanho);

        if (tamanho <= 3) {
            return individuo;
        }

        // possível troca aleatória de cidades (exceto a primeira e última)
        for (int i = 1; i < tamanho - 1; i++) {
            if (random.nextDouble() < probabilidadeMutacao) {
                int posicao = 1 + random.nextInt(tamanho - 2);
                Collections.swap(cidadesIndividuo, i, posicao);
            }
        }

        return new Rota(cidadesIndividuo, calcularDistancia(cidadesIndividuo));
    }
}
