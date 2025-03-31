public class AgenteGenetico {
    private int[][] matrizDistancias;
    private int[] cidadesEntrada;

    public AgenteGenetico(int[][] distancias, int[] cidades) {
        this.matrizDistancias = distancias;
        this.cidadesEntrada = cidades.clone();
    }

    public int resolver() {
        return 0;
    }

    public void imprimirMatriz() {
        if (this.matrizDistancias == null) {
            System.out.println("Matriz vazia");
            return;
        }

        System.out.println("Matriz de distâncias (" + this.matrizDistancias.length +
                "x" + this.matrizDistancias[0].length + "):");

        for (int i = 0; i < this.matrizDistancias.length; i++) {
            for (int j = 0; j < this.matrizDistancias[i].length; j++) {
                System.out.printf("%6d ", this.matrizDistancias[i][j]);
            }
            System.out.println();
        }
    }
}
