package juego;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class juego extends JFrame {

    int FILAS = 10;
    int COLS = 10;
    int MINAS = 15;

    boolean[][] tieneMina;
    boolean[][] revelada;
    boolean[][] marcada;
    int[][] contador;
    boolean gameOver = false;
    boolean victoria = false;
    boolean primerClic = true;
    int celdasReveladas = 0;

    JButton[][] botones;
    JPanel panelTablero;
    JPanel panelLobby;
    JPanel panelPrincipal;
    CardLayout cardLayout;

    JLabel lblMinas;
    JLabel lblTimer;
    JButton btnReiniciar;
    Timer temporizador;
    int segundos = 0;

    static final Color COLOR_FONDO = new Color(30, 31, 34);
    static final Color COLOR_PANEL_SUPERIOR = new Color(24, 25, 28);
    static final Color COLOR_BOTON_OCULTO = new Color(88, 101, 242);
    static final Color COLOR_BOTON_REVELADO = new Color(30, 31, 34);
    static final Color COLOR_MARCADO = new Color(237, 66, 69);
    static final Color COLOR_TEXTO = new Color(220, 221, 222);
    static final Color COLOR_BORDE = new Color(18, 19, 22);
    static final Color COLOR_LOBBY_BG = new Color(22, 23, 26);

    Color[] coloresNumeros = {
        new Color(88, 101, 242), new Color(87, 181, 87), new Color(237, 66, 69),
        new Color(127, 90, 210), new Color(180, 80, 60), new Color(60, 180, 180),
        Color.LIGHT_GRAY, Color.GRAY
    };

    public juego() {
        setTitle("Buscaminas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);
        panelPrincipal.setBackground(COLOR_LOBBY_BG);

        crearLobby();
        crearPanelJuego();

        add(panelPrincipal);
        cardLayout.show(panelPrincipal, "lobby");

        pack();
        setLocationRelativeTo(null);
    }

    void crearLobby() {
        panelLobby = new JPanel(new GridBagLayout());
        panelLobby.setBackground(COLOR_LOBBY_BG);
        panelLobby.setPreferredSize(new Dimension(480, 520));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);

        JLabel titulo = new JLabel("\uD83D\uDCA3 BUSCAMINAS \uD83D\uDCA3", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titulo.setForeground(new Color(88, 101, 242));

        JLabel subtitulo = new JLabel("Selecciona una dificultad", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitulo.setForeground(new Color(120, 130, 150));

        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setBackground(COLOR_LOBBY_BG);

        BotonDificultad btnEasy = new BotonDificultad(
            "\uD83D\uDE0A F\u00e1cil", "9\u00d79  |  10 minas", new Color(87, 181, 87),
            () -> iniciarJuego(5, 5, 10)
        );
        BotonDificultad btnMedium = new BotonDificultad(
            "\uD83E\uDD14 Medio", "10\u00d710  |  15 minas", new Color(88, 101, 242),
            () -> iniciarJuego(15, 15, 15)
        );
        BotonDificultad btnHard = new BotonDificultad(
            "\uD83D\uDE35 Dif\u00edcil", "12\u00d712  |  25 minas", new Color(237, 66, 69),
            () -> iniciarJuego(25, 25, 90)
        );

        btnMedium.setSelected(true);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.insets = new Insets(6, 0, 6, 0);

        panelBotones.add(btnEasy, gbc2);
        panelBotones.add(btnMedium, gbc2);
        panelBotones.add(btnHard, gbc2);

        gbc.gridy = 0;
        gbc.insets = new Insets(20, 40, 5, 40);
        panelLobby.add(titulo, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 40, 30, 40);
        panelLobby.add(subtitulo, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 40, 10, 40);
        panelLobby.add(panelBotones, gbc);

        panelPrincipal.add(panelLobby, "lobby");
    }

    void iniciarJuego(int filas, int cols, int minas) {
        FILAS = filas;
        COLS = cols;
        MINAS = minas;

        panelPrincipal.remove(panelTablero.getParent() != panelPrincipal ? panelTablero : panelTablero);

        remove(panelTablero);
        crearPanelJuego();
        reiniciarJuego();

        pack();
        setLocationRelativeTo(null);
        cardLayout.show(panelPrincipal, "juego");
    }

    void crearPanelJuego() {
        JPanel panelJuego = new JPanel(new BorderLayout());
        panelJuego.setBackground(COLOR_FONDO);

        crearPanelSuperior(panelJuego);
        crearTablero();
        panelJuego.add(panelTablero, BorderLayout.CENTER);
        crearPanelInferior(panelJuego);

        panelPrincipal.add(panelJuego, "juego");
    }

    void crearMenu(JPanel panelJuego) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(COLOR_PANEL_SUPERIOR);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));

        JMenu mnJuego = new JMenu("Juego");
        mnJuego.setForeground(COLOR_TEXTO);
        mnJuego.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JMenuItem itemNuevo = new JMenuItem("Nuevo juego");
        itemNuevo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemNuevo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemNuevo.addActionListener(e -> reiniciarJuego());
        mnJuego.add(itemNuevo);

        mnJuego.addSeparator();

        JMenuItem itemMenuPpal = new JMenuItem("Men\u00fa principal");
        itemMenuPpal.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        itemMenuPpal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemMenuPpal.addActionListener(e -> {
            if (temporizador != null) temporizador.stop();
            cardLayout.show(panelPrincipal, "lobby");
            pack();
            setLocationRelativeTo(null);
        });
        mnJuego.add(itemMenuPpal);

        mnJuego.addSeparator();

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        itemSalir.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemSalir.addActionListener(e -> System.exit(0));
        mnJuego.add(itemSalir);

        menuBar.add(mnJuego);
        setJMenuBar(menuBar);
    }

    void crearPanelSuperior(JPanel panelJuego) {
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(COLOR_PANEL_SUPERIOR);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        lblMinas = new JLabel("Minas: " + MINAS);
        lblMinas.setForeground(new Color(237, 66, 69));
        lblMinas.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centro.setBackground(COLOR_PANEL_SUPERIOR);

        btnReiniciar = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(60, 70, 160));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(100, 115, 255));
                } else {
                    g2.setColor(COLOR_BOTON_OCULTO);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnReiniciar.setText("\u25B6");
        btnReiniciar.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
        btnReiniciar.setForeground(Color.WHITE);
        btnReiniciar.setPreferredSize(new Dimension(40, 40));
        btnReiniciar.setContentAreaFilled(false);
        btnReiniciar.setBorderPainted(false);
        btnReiniciar.setFocusPainted(false);
        btnReiniciar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReiniciar.addActionListener(e -> reiniciarJuego());

        centro.add(btnReiniciar);

        lblTimer = new JLabel("00:00");
        lblTimer.setForeground(new Color(88, 101, 242));
        lblTimer.setFont(new Font("Segoe UI", Font.BOLD, 16));

        panelSuperior.add(lblMinas, BorderLayout.WEST);
        panelSuperior.add(centro, BorderLayout.CENTER);
        panelSuperior.add(lblTimer, BorderLayout.EAST);

        panelJuego.add(panelSuperior, BorderLayout.NORTH);
    }

    void crearTablero() {
        botones = new JButton[FILAS][COLS];
        tieneMina = new boolean[FILAS][COLS];
        revelada = new boolean[FILAS][COLS];
        marcada = new boolean[FILAS][COLS];
        contador = new int[FILAS][COLS];

        int bordeGrosor = FILAS <= 8 ? 4 : (FILAS <= 10 ? 3 : 2);
        panelTablero = new JPanel(new GridLayout(FILAS, COLS, bordeGrosor, bordeGrosor));
        panelTablero.setBackground(COLOR_BORDE);
        panelTablero.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, bordeGrosor));

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLS; j++) {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        if (!isEnabled()) {
                            g2.setColor(COLOR_BOTON_REVELADO);
                        } else {
                            g2.setColor(COLOR_BOTON_OCULTO);
                        }
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 3, 3);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
                btn.setForeground(Color.WHITE);
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setMargin(new Insets(0, 0, 0, 0));

                final int fila = i;
                final int col = j;

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver || victoria) return;
                        if (SwingUtilities.isRightMouseButton(e)) {
                            marcarCelda(fila, col);
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            revelarCelda(fila, col);
                        }
                    }
                });

                botones[i][j] = btn;
                panelTablero.add(btn);
            }
        }
    }

    void crearPanelInferior(JPanel panelJuego) {
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(COLOR_PANEL_SUPERIOR);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        JLabel lblNombre = new JLabel("Jowell Acosta - 24-1268");
        lblNombre.setForeground(new Color(120, 130, 150));
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblInfo = new JLabel("Click izquierdo: revelar | Click derecho: marcar");
        lblInfo.setForeground(new Color(120, 130, 150));
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);

        panelInferior.add(lblNombre, BorderLayout.WEST);
        panelInferior.add(lblInfo, BorderLayout.CENTER);
        panelJuego.add(panelInferior, BorderLayout.SOUTH);
    }

    void reiniciarJuego() {
        gameOver = false;
        victoria = false;
        celdasReveladas = 0;
        primerClic = true;
        segundos = 0;

        if (temporizador != null) {
            temporizador.stop();
        }
        lblTimer.setText("00:00");

        int tamCelda;
        if (FILAS <= 8) tamCelda = 44;
        else if (FILAS <= 10) tamCelda = 40;
        else tamCelda = 36;

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLS; j++) {
                tieneMina[i][j] = false;
                revelada[i][j] = false;
                marcada[i][j] = false;
                contador[i][j] = 0;
                botones[i][j].setText("");
                botones[i][j].setEnabled(true);
                botones[i][j].setPreferredSize(new Dimension(tamCelda, tamCelda));
                botones[i][j].setFont(new Font("Segoe UI", Font.BOLD, tamCelda >= 40 ? 16 : 14));
                botones[i][j].repaint();
            }
        }

        lblMinas.setText("Minas: " + MINAS);

        ponerBanderaCentro();

        pack();

        crearMenu((JPanel) panelTablero.getParent());
        setJMenuBar(getJMenuBar());
    }

    boolean esCentro(int f, int c) {
        return f == FILAS / 2 && c == COLS / 2;
    }

    void ponerBanderaCentro() {
        int cf = FILAS / 2;
        int cc = COLS / 2;
        botones[cf][cc].setText("\uD83D\uDEA9");
        botones[cf][cc].setForeground(Color.WHITE);
        botones[cf][cc].setFont(new Font("Segoe UI Symbol", Font.PLAIN, botones[cf][cc].getFont().getSize() + 2));
        botones[cf][cc].setBackground(new Color(255, 180, 0));
        botones[cf][cc].setOpaque(true);
        botones[cf][cc].setEnabled(false);
        botones[cf][cc].setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        botones[cf][cc].repaint();
    }

    void colocarMinas(int filaSegura, int colSegura) {
        Random rand = new Random();
        int minasColocadas = 0;
        while (minasColocadas < MINAS) {
            int i = rand.nextInt(FILAS);
            int j = rand.nextInt(COLS);
            if (!tieneMina[i][j] && !(i == filaSegura && j == colSegura) && !esCentro(i, j)) {
                tieneMina[i][j] = true;
                minasColocadas++;
            }
        }

        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (!tieneMina[i][j]) {
                    contador[i][j] = contarMinasAlrededor(i, j);
                }
            }
        }
    }

    int contarMinasAlrededor(int fila, int col) {
        int suma = 0;
        for (int i = fila - 1; i <= fila + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < FILAS && j >= 0 && j < COLS) {
                    if (tieneMina[i][j]) suma++;
                }
            }
        }
        return suma;
    }

    void iniciarTemporizador() {
        temporizador = new Timer(1000, e -> {
            segundos++;
            int mins = segundos / 60;
            int secs = segundos % 60;
            lblTimer.setText(String.format("%02d:%02d", mins, secs));
        });
        temporizador.start();
    }

    void revelarCelda(int fila, int col) {
        if (fila < 0 || fila >= FILAS || col < 0 || col >= COLS) return;
        if (revelada[fila][col] || marcada[fila][col]) return;
        if (esCentro(fila, col)) return;

        if (primerClic) {
            primerClic = false;
            colocarMinas(fila, col);
            iniciarTemporizador();
            if (contador[fila][col] == 0 && !tieneMina[fila][col]) {
                revelarCelda(fila, col);
                return;
            }
        }

        if (tieneMina[fila][col]) {
            gameOver = true;
            if (temporizador != null) temporizador.stop();
            mostrarTodasLasMinas();
            int opcion = JOptionPane.showOptionDialog(this,
                "\u00a1Boom! Pisaste una mina.\n\u00bfQuieres intentar de nuevo?\n\nJowell Acosta - 24-1268",
                "Game Over", JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE, null,
                new String[]{"Reintentar", "Men\u00fa"}, "Reintentar");
            if (opcion == JOptionPane.YES_OPTION) {
                reiniciarJuego();
            } else {
                if (temporizador != null) temporizador.stop();
                cardLayout.show(panelPrincipal, "lobby");
                pack();
                setLocationRelativeTo(null);
            }
            return;
        }

        revelada[fila][col] = true;
        celdasReveladas++;
        botones[fila][col].setEnabled(false);
        botones[fila][col].repaint();

        if (contador[fila][col] > 0) {
            botones[fila][col].setText(String.valueOf(contador[fila][col]));
            botones[fila][col].setForeground(coloresNumeros[contador[fila][col] - 1]);
        } else {
            for (int i = fila - 1; i <= fila + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    revelarCelda(i, j);
                }
            }
        }

        verificarVictoria();
    }

    void marcarCelda(int fila, int col) {
        if (revelada[fila][col]) return;
        if (esCentro(fila, col)) return;

        if (marcada[fila][col]) {
            marcada[fila][col] = false;
            botones[fila][col].setText("");
            botones[fila][col].repaint();
        } else {
            marcada[fila][col] = true;
            botones[fila][col].setText("\u2691");
            botones[fila][col].setForeground(new Color(237, 66, 69));
            botones[fila][col].repaint();
        }

        int marcadas = 0;
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (marcada[i][j]) marcadas++;
            }
        }
        lblMinas.setText("Minas: " + (MINAS - marcadas));
    }

    void mostrarTodasLasMinas() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (!revelada[i][j]) {
                    if (tieneMina[i][j]) {
                        botones[i][j].setText("\u26A0");
                        botones[i][j].setForeground(Color.WHITE);
                    }
                    if (marcada[i][j] && !tieneMina[i][j]) {
                        botones[i][j].setText("X");
                        botones[i][j].setForeground(Color.LIGHT_GRAY);
                    }
                }
                botones[i][j].setEnabled(false);
                botones[i][j].repaint();
            }
        }
    }

    void verificarVictoria() {
        int totalCeldasSinMina = FILAS * COLS - MINAS - 1;
        if (celdasReveladas == totalCeldasSinMina) {
            victoria = true;
            if (temporizador != null) temporizador.stop();
            int mins = segundos / 60;
            int secs = segundos % 60;
            int opcion = JOptionPane.showOptionDialog(this,
                "\u00a1Felicidades! Ganaste en " + mins + ":" + (secs < 10 ? "0" : "") + secs + "\n\u00bfQuieres jugar de nuevo?",
                "Victoria", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null,
                new String[]{"Reintentar", "Men\u00fa"}, "Reintentar");
            if (opcion == JOptionPane.YES_OPTION) {
                reiniciarJuego();
            } else {
                if (temporizador != null) temporizador.stop();
                cardLayout.show(panelPrincipal, "lobby");
                pack();
                setLocationRelativeTo(null);
            }
        }
    }

    class BotonDificultad extends JPanel {
        boolean selected = false;
        Runnable accion;
        Color color;

        BotonDificultad(String titulo, String desc, Color color, Runnable accion) {
            this.color = color;
            this.accion = accion;
            setLayout(new BorderLayout(12, 0));
            setBackground(new Color(35, 36, 40));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 52, 58), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
            ));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel iconoLbl = new JLabel("\u2691", SwingConstants.CENTER);
            iconoLbl.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
            iconoLbl.setForeground(color);
            iconoLbl.setPreferredSize(new Dimension(40, 40));

            JPanel textoPanel = new JPanel(new GridLayout(2, 1));
            textoPanel.setBackground(new Color(35, 36, 40));
            JLabel tituloLbl = new JLabel(titulo);
            tituloLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            tituloLbl.setForeground(Color.WHITE);
            JLabel descLbl = new JLabel(desc);
            descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            descLbl.setForeground(new Color(120, 130, 150));
            textoPanel.add(tituloLbl);
            textoPanel.add(descLbl);

            add(iconoLbl, BorderLayout.WEST);
            add(textoPanel, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    accion.run();
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(45, 46, 50));
                    textoPanel.setBackground(new Color(45, 46, 50));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(new Color(35, 36, 40));
                    textoPanel.setBackground(new Color(35, 36, 40));
                }
            });
        }

        void setSelected(boolean s) {
            selected = s;
            if (s) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 2),
                    BorderFactory.createEmptyBorder(13, 15, 13, 15)
                ));
            } else {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(50, 52, 58), 1),
                    BorderFactory.createEmptyBorder(14, 16, 14, 16)
                ));
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new juego().setVisible(true));
    }
}
