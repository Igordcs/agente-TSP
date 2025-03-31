import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        int[][] matrizDistancias = carregarMatrizDistancias("cities.txt");
        int[] rota = { 0, 1, 2, 3, 0 };
        AgenteGenetico agente = new AgenteGenetico(matrizDistancias, rota);
        System.out.println(agente.resolver());
        agente.imprimirMatriz();
    }

    private static int[][] carregarMatrizDistancias(String caminhoArquivo) {
        List<int[]> linhas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linhaArquivo;
            while ((linhaArquivo = br.readLine()) != null) {
                String[] valores = linhaArquivo.trim().split("\\s+");

                int[] distancias = new int[valores.length];
                for (int i = 0; i < valores.length; i++) {
                    distancias[i] = Integer.parseInt(valores[i]);
                }
                linhas.add(distancias);
            }

            int numCidades = linhas.size();
            for (int[] linha : linhas) {
                if (linha.length != numCidades) {
                    System.err.println("Erro: A matriz de distâncias não é quadrada!");
                    return new int[0][0];
                }
            }

            int[][] matriz = new int[numCidades][numCidades];
            for (int i = 0; i < numCidades; i++) {
                System.arraycopy(linhas.get(i), 0, matriz[i], 0, numCidades);
            }

            return matriz;
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao carregar matriz: " + e.getMessage());
            return new int[0][0];
        }
    }
}