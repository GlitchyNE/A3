import javax.swing.JFrame;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class Tela_Principal extends JFrame {

    private JPanel jPanel1;
    private JLabel jLabel1;
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;

    public Tela_Principal() {
        initComponents();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    @SuppressWarnings("Convert2Lambda")
    private void initComponents() {
        setSize(520, 480);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jPanel1 = new JPanel();
        jPanel1.setBackground(new Color(250, 247, 237));

        jLabel1 = new JLabel(new ImageIcon("C:\\Users\\Gabriel\\Downloads\\1630541240498.jpg"));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        jButton1 = criarCardButton("Gerenciamento de pedidos", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new Tela_Pedidos().setVisible(true);
            }
        });

        jButton2 = criarCardButton("Gerenciamento do estoque", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new Tela_Estoque().setVisible(true);
            }
        });

        jButton3 = criarCardButton("Gerenciamento dos clientes", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new Tela_Clientes().setVisible(true);
            }
        });

        jButton4 = criarCardButton("Relatórios", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                actionRelatorios();
            }
        });

        GroupLayout layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        int btnW = 220;
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(jLabel1)
                .addGroup(layout.createSequentialGroup()
                    .addGap(40)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jButton1, btnW, btnW, btnW)
                        .addComponent(jButton3, btnW, btnW, btnW))
                    .addGap(20)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(jButton2, btnW, btnW, btnW)
                        .addComponent(jButton4, btnW, btnW, btnW))
                    .addGap(40)
                )
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(10)
                .addComponent(jLabel1, 120, 120, 120)
                .addGap(10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, 60, 60, 60)
                    .addComponent(jButton2, 60, 60, 60))
                .addGap(15)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, 60, 60, 60)
                    .addComponent(jButton4, 60, 60, 60))
                .addContainerGap(20, Short.MAX_VALUE)
        );

        setContentPane(jPanel1);
        pack();
    }

    private JButton criarCardButton(String text, ActionListener al) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Roboto", Font.PLAIN, 14));
        btn.setBackground(new Color(255, 87, 34));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        btn.addActionListener(al);
        return btn;
    }

    private void actionRelatorios() {
        String[] op = {"Relatório de Entrada", "Relatório de Saída"};
        int esc = JOptionPane.showOptionDialog(
            this, "Qual tipo de relatório deseja acessar?", "Selecionar Relatório",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, op, op[0]
        );
        if (esc == 0) Relatorio_Entrada.getInstancia().setVisible(true);
        else if (esc == 1) Relatorio_Saida.getInstancia().setVisible(true);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            System.err.println("Erro ao definir o LookAndFeel: " + e.getMessage());
        }

        EventQueue.invokeLater(() -> new Tela_Principal());
    }
}
