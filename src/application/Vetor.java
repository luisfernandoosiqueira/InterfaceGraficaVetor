package application;

public class Vetor {
    private Double x;
    private Double y;
    private Double z;

    public Vetor(Double x, Double y) {
        this.x = x;
        this.y = y;
        this.z = 0.0; // Trata como 2D se Z não for fornecido
    }

    public Vetor(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    public double calcularMagnitude() {
        // Calcula a magnitude do vetor
        return Math.sqrt(x * x + y * y + (z != null ? z * z : 0));
    }

    public static double calcularProdutoEscalar(Vetor v1, Vetor v2) {
        // Calcula o produto escalar entre dois vetores
        return v1.x * v2.x + v1.y * v2.y + (v1.z != null && v2.z != null ? v1.z * v2.z : 0);
    }

    public static double calcularAngulo(Vetor v1, Vetor v2) {
        // Calcula o ângulo entre dois vetores
        double produtoEscalar = calcularProdutoEscalar(v1, v2);
        double magnitudes = v1.calcularMagnitude() * v2.calcularMagnitude();
        return Math.toDegrees(Math.acos(produtoEscalar / magnitudes));
    }

    public static Vetor calcularProdutoVetorial(Vetor v1, Vetor v2) {
        // Calcula o produto vetorial entre dois vetores
        if (v1.z == null) {
            v1.z = 0.0; 
        }
        if (v2.z == null) {
            v2.z = 0.0; 
        }
        double x = v1.y * v2.z - v1.z * v2.y;
        double y = v1.z * v2.x - v1.x * v2.z;
        double z = v1.x * v2.y - v1.y * v2.x;
        return new Vetor(x, y, z);
    }

    @Override
    public String toString() {
        // Formatação da string de saída para vetores
        if (z == null) {
            return "(" + x + ", " + y + ")";
        } else {
            return "(" + x + ", " + y + ", " + z + ")";
        }
    }
}