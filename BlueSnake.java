import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class BlueSnake extends JFrame {
    private static final int TAMANHO_CELULA = 30;
    private static final int NUMERO_COLUNAS = 30;
    private static final int NUMERO_LINHAS = 20;
    private int direcaoAtual = KeyEvent.VK_RIGHT;
    private int xCabeca = 10;
    private int yCabeca = 10;
    private int[] corpoX = new int[100];
    private int[] corpoY = new int[100];
    private int tamanhoCorpo = 3;
    private int xFruta;
    private int yFruta;
    private int placar = 0;

    // Foto do corpo
    private ImageIcon corpoCobraIcon = new ImageIcon("C:\\Users\\Luciano\\Desktop\\Blue Snake\\media\\cabeca.png");
    private Image corpoCobraImagem = corpoCobraIcon.getImage();

    // Foto da Fruta
    private ImageIcon frutaIcon = new ImageIcon("C:\\Users\\Luciano\\Desktop\\Blue Snake\\media\\fruta.png");
    private Image frutaImagem = frutaIcon.getImage();

    // Audio ao comer fruta
    private String somFruta = "C:\\Users\\Luciano\\Desktop\\Blue Snake\\media\\canetaAzul.wav";

    public BlueSnake() {
        setTitle("Blue Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(NUMERO_COLUNAS * TAMANHO_CELULA, NUMERO_LINHAS * TAMANHO_CELULA);
        setLocationRelativeTo(null);

        add(new TelaJogo(this));

        addKeyListener(new KeyAdapter());
        setFocusable(true);

        reposicionarFruta();

        Timer timer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                atualizarJogo();
            }
        });
        timer.start();
    }
    private class TelaJogo extends JPanel {
        private final BlueSnake jogo;
        public TelaJogo(BlueSnake jogo) {
            this.jogo = jogo;
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            for (int i = 0; i < tamanhoCorpo; i++) {
                g.drawImage(corpoCobraImagem, corpoX[i] * TAMANHO_CELULA, corpoY[i] * TAMANHO_CELULA, TAMANHO_CELULA, TAMANHO_CELULA, this);
            }
            g.drawImage(frutaImagem, xFruta * TAMANHO_CELULA, yFruta * TAMANHO_CELULA, TAMANHO_CELULA, TAMANHO_CELULA, this);
            g.setColor(Color.blue);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("canetas azuis: " + placar, 10, 20);
        }
    }
    private class KeyAdapter implements KeyListener {
        public void keyPressed(KeyEvent e) {
            int novaDirecao = e.getKeyCode();
            if ((novaDirecao == KeyEvent.VK_LEFT && direcaoAtual != KeyEvent.VK_RIGHT) ||
                    (novaDirecao == KeyEvent.VK_RIGHT && direcaoAtual != KeyEvent.VK_LEFT) ||
                    (novaDirecao == KeyEvent.VK_UP && direcaoAtual != KeyEvent.VK_DOWN) ||
                    (novaDirecao == KeyEvent.VK_DOWN && direcaoAtual != KeyEvent.VK_UP)) {
                direcaoAtual = novaDirecao;
            }
        }
        public void keyTyped(KeyEvent e) {
        }
        public void keyReleased(KeyEvent e) {
        }
    }
    private void atualizarJogo() {
        moverCabeca();
        if (verificarColisao()) {
            reiniciarJogo();
        }
        if (cabeçaComeuFruta()) {
            reproduzirSom(somFruta);
            crescerCobra();
            reposicionarFruta();
        }
        moverCorpo();
        repaint();
    }
    private void reproduzirSom(String caminhoSom) {
        try {
            File somArquivo = new File(caminhoSom);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(somArquivo);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void moverCabeca() {
        switch (direcaoAtual) {
            case KeyEvent.VK_UP:
                yCabeca--;
                break;
            case KeyEvent.VK_DOWN:
                yCabeca++;
                break;
            case KeyEvent.VK_LEFT:
                xCabeca--;
                break;
            case KeyEvent.VK_RIGHT:
                xCabeca++;
                break;
        }
    }
    private boolean verificarColisao() {
        return xCabeca < 0 || xCabeca >= NUMERO_COLUNAS ||
                yCabeca < 0 || yCabeca >= NUMERO_LINHAS ||
                colisaoCorpo();
    }
    private boolean colisaoCorpo() {
        for (int i = 0; i < tamanhoCorpo; i++) {
            if (corpoX[i] == xCabeca && corpoY[i] == yCabeca) {
                return true;
            }
        }
        return false;
    }
    private void crescerCobra() {
        corpoX[tamanhoCorpo] = corpoX[tamanhoCorpo - 1];
        corpoY[tamanhoCorpo] = corpoY[tamanhoCorpo - 1];
        tamanhoCorpo++;
        placar++;
    }
    private void reposicionarFruta() {
        Random rand = new Random();
        xFruta = rand.nextInt(NUMERO_COLUNAS);
        yFruta = rand.nextInt(NUMERO_LINHAS);
    }
    private boolean cabeçaComeuFruta() {
        return xCabeca == xFruta && yCabeca == yFruta;
    }
    private void moverCorpo() {
        for (int i = tamanhoCorpo - 1; i > 0; i--) {
            corpoX[i] = corpoX[i - 1];
            corpoY[i] = corpoY[i - 1];
        }
        corpoX[0] = xCabeca;
        corpoY[0] = yCabeca;
    }
    private void reiniciarJogo() {
        xCabeca = 10;
        yCabeca = 10;
        tamanhoCorpo = 3;
        placar = 0; 
        reposicionarFruta();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BlueSnake jogo = new BlueSnake();
            jogo.setVisible(true);
        });
    }
}