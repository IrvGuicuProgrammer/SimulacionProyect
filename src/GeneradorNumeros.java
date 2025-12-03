import java.util.ArrayList;
import java.util.List;

/**
 * Clase singleton para compartir los números generados entre diferentes ventanas
 */
public class GeneradorNumeros {
    private static GeneradorNumeros instance;
    private List<Double> numeros;
    private int n, x0, a, c, m;
    
    private GeneradorNumeros() {
        numeros = new ArrayList<>();
    }
    
    public static GeneradorNumeros getInstance() {
        if (instance == null) {
            instance = new GeneradorNumeros();
        }
        return instance;
    }
    
    public List<Double> getNumeros() {
        return numeros;
    }
    
    public void setNumeros(List<Double> numeros) {
        this.numeros = numeros;
    }
    
    public void clearNumeros() {
        numeros.clear();
    }
    
    public int getN() { return n; }
    public int getX0() { return x0; }
    public int getA() { return a; }
    public int getC() { return c; }
    public int getM() { return m; }
    
    public void setParametros(int n, int x0, int a, int c, int m) {
        this.n = n;
        this.x0 = x0;
        this.a = a;
        this.c = c;
        this.m = m;
    }
    
    public boolean hayNumeros() {
        return !numeros.isEmpty();
    }
}