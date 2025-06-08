import java.awt.Color;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.*;

public class Tela_Login extends javax.swing.JFrame {

    public Tela_Login() {
        initComponents();
        setSize(450, 450);  
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabelLogin = new javax.swing.JLabel();
        jTextPane1 = new javax.swing.JTextPane();
        jLabelSenha = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        jPanel3.setBackground(new Color(250, 247, 237));

        
        jLabel2.setIcon(new ImageIcon("C:\\Users\\Gabriel\\Downloads\\1630541240498.jpg"));

       
        jLabel3.setFont(new java.awt.Font("Roboto", java.awt.Font.BOLD, 18));
        jLabel3.setText("Acesso ao sistema");
        jLabel3.setHorizontalAlignment(SwingConstants.CENTER);

        
        jLabelLogin.setFont(new java.awt.Font("Roboto", java.awt.Font.PLAIN, 14));
        jLabelLogin.setText("Login:");

      
        jTextPane1.setFont(new java.awt.Font("Roboto", java.awt.Font.PLAIN, 14));
        jTextPane1.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

      
        jLabelSenha.setFont(new java.awt.Font("Roboto", java.awt.Font.PLAIN, 14));
        jLabelSenha.setText("Senha:");

       
        jPasswordField1.setFont(new java.awt.Font("Roboto", java.awt.Font.PLAIN, 14));
        jPasswordField1.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        jPasswordField1.addActionListener(evt -> jPasswordField1ActionPerformed());

      
        jButton1.setText("Acessar Sistema");
        jButton1.setFont(new java.awt.Font("Roboto", java.awt.Font.BOLD, 14));
        jButton1.setBackground(new Color(255, 87, 34));
        jButton1.setForeground(new Color(255, 255, 255));
        jButton1.setFocusPainted(false);
        jButton1.setBorder(new LineBorder(new Color(255, 87, 34), 1, true));
        int fieldWidth = 300;
        jButton1.setPreferredSize(new java.awt.Dimension(fieldWidth, 45));
        jButton1.addActionListener(evt -> jButton1ActionPerformed(evt));

        
        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setAutoCreateGaps(true);
        jPanel3Layout.setAutoCreateContainerGaps(true);
        int sideGap = (450 - fieldWidth) / 2;
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createSequentialGroup()
                .addGap(sideGap)
                .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, fieldWidth, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelLogin)
                    .addComponent(jTextPane1, GroupLayout.PREFERRED_SIZE, fieldWidth, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSenha)
                    .addComponent(jPasswordField1, GroupLayout.PREFERRED_SIZE, fieldWidth, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, fieldWidth, GroupLayout.PREFERRED_SIZE)
                )
                .addGap(sideGap)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createSequentialGroup()
                .addGap(10)
                .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                .addGap(10)
                .addComponent(jLabel3)
                .addGap(20)
                .addComponent(jLabelLogin)
                .addComponent(jTextPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(15)
                .addComponent(jLabelSenha)
                .addComponent(jPasswordField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(25)
                .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE)
        );

     
        GroupLayout mainLayout = new GroupLayout(getContentPane());
        getContentPane().setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainLayout.setVerticalGroup(
            mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void jPasswordField1ActionPerformed() {
       
    }

    @SuppressWarnings("ConvertToTryWithResources")
    private void jButton1ActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {
        String url = "jdbc:mysql://localhost:3306/lanchonete";
        String user = "root";
        String password = "6gabrielnesi";

        String login = jTextPane1.getText();
        String senha = new String(jPasswordField1.getPassword());

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            String sql = "SELECT * FROM funcionarios WHERE loginFuncionario = ? AND senhaFuncionario = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Login realizado com sucesso!");
                new Tela_Principal().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Login ou senha inválidos!");
            }
            rs.close(); stmt.close(); conn.close();
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar: " + e.getMessage());
        }
    }

    // Variáveis de componente
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelLogin;
    private javax.swing.JLabel jLabelSenha;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JTextPane jTextPane1;

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Tela_Login().setVisible(true));
    }
}
