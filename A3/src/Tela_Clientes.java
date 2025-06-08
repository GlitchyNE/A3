import java.awt.HeadlessException;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Tela_Clientes extends JFrame {

    Connection conn;
    PreparedStatement pst;
    ResultSet rs;

    public Tela_Clientes() {
        initComponents();
        connect();
        listarClientes();
    }

  
    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/lanchonete", "root", "6gabrielnesi");
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar no banco: " + e.getMessage());
        }
    }

   
    private void listarClientes() {
        String sql = "SELECT * FROM clientes";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("idCliente"),
                        rs.getString("nomeCliente"),
                        rs.getString("cpfCliente"),
                        rs.getString("tefCliente")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
        }
    }

   
    public void buscarClientes(String busca) {
        String sql = "SELECT * FROM clientes WHERE nomeCliente LIKE ? OR cpfCliente LIKE ? OR tefCliente LIKE ?";
        try {
            pst = conn.prepareStatement(sql);
            String buscaLike = "%" + busca + "%";
            pst.setString(1, buscaLike);
            pst.setString(2, buscaLike);
            pst.setString(3, buscaLike);
            rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("idCliente"),
                        rs.getString("nomeCliente"),
                        rs.getString("cpfCliente"),
                        rs.getString("tefCliente")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
        }
    }

 
    public void cadastrarCliente() {
        String nomeCliente = jTextPaneNome.getText();
        String cpfCliente = jTextPaneCPF.getText();
        String tefCliente = jTextPaneTelefone.getText();

        if (nomeCliente.isEmpty() || cpfCliente.isEmpty() || tefCliente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            return;
        }

        try {
            pst = conn.prepareStatement("INSERT INTO clientes (nomeCliente, cpfCliente, tefCliente) VALUES (?, ?, ?)");
            pst.setString(1, nomeCliente);
            pst.setString(2, cpfCliente);
            pst.setString(3, tefCliente);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
            limparCampos();
            listarClientes();
        } catch (HeadlessException | SQLException e) {
        }
    }


    public void atualizarCliente() {
        int selected = jTable1.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para atualizar.");
            return;
        }

        int idCliente = (int) jTable1.getValueAt(selected, 0);
        String nomeCliente = jTextPaneNome.getText();
        String cpfCliente = jTextPaneCPF.getText();
        String tefCliente = jTextPaneTelefone.getText();

        try {
            pst = conn.prepareStatement("UPDATE clientes SET nomeCliente=?, cpfCliente=?, tefCliente=? WHERE idCliente=?");
            pst.setString(1, nomeCliente);
            pst.setString(2, cpfCliente);
            pst.setString(3, tefCliente);
            pst.setInt(4, idCliente);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");
            limparCampos();
            listarClientes();
        } catch (HeadlessException | SQLException e) {
        }
    }


    public void deletarCliente() {
        int selected = jTable1.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para deletar.");
            return;
        }

        int idCliente = (int) jTable1.getValueAt(selected, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente deletar este cliente?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                pst = conn.prepareStatement("DELETE FROM clientes WHERE idCliente=?");
                pst.setInt(1, idCliente);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Cliente deletado com sucesso!");
                limparCampos();
                listarClientes();
            } catch (HeadlessException | SQLException e) {
            }
        }
    }

    public void limparCampos() {
        jTextPaneNome.setText("");
        jTextPaneCPF.setText("");
        jTextPaneTelefone.setText("");
    }

    // -------------------- Componentes e Interface --------------------
    private void initComponents() {

        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        jScrollPaneBusca = new JScrollPane();
        jTextPaneBusca = new JTextPane();
        jLabel1 = new JLabel("Nome:");
        jLabel2 = new JLabel("Telefone:");
        jLabel3 = new JLabel("CPF:");
        jScrollPaneNome = new JScrollPane();
        jTextPaneNome = new JTextPane();
        jScrollPaneCPF = new JScrollPane();
        jTextPaneCPF = new JTextPane();
        jScrollPaneTelefone = new JScrollPane();
        jTextPaneTelefone = new JTextPane();
        jButtonCadastrar = new JButton("Cadastrar Cliente");
        jButtonAtualizar = new JButton("Atualizar Cliente");
        jButtonDeletar = new JButton("Deletar Cliente");
        jButtonPedidos = new JButton("Ir para pedidos");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

    
        jTable1.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID_Cliente", "Nome", "CPF", "Telefone"}
        ) {
            Class[] types = new Class[]{
                    Integer.class, String.class, String.class, String.class
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        jTable1.setRowHeight(28);
        jTable1.setFont(new java.awt.Font("Segoe UI", 0, 16));
        jScrollPane1.setViewportView(jTable1);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(900, 250)); // tabela maior

        jTextPaneBusca.setText("Buscar por CPF/Nome ou telefone...");
        jTextPaneBusca.setFont(new java.awt.Font("Segoe UI", 0, 16));
        jScrollPaneBusca.setViewportView(jTextPaneBusca);

        jScrollPaneNome.setViewportView(jTextPaneNome);
        jScrollPaneNome.setPreferredSize(new java.awt.Dimension(400, 30)); // campo nome maior
        jTextPaneNome.setFont(new java.awt.Font("Segoe UI", 0, 16));

        jScrollPaneCPF.setViewportView(jTextPaneCPF);
        jTextPaneCPF.setFont(new java.awt.Font("Segoe UI", 0, 16));
        jScrollPaneTelefone.setViewportView(jTextPaneTelefone);
        jTextPaneTelefone.setFont(new java.awt.Font("Segoe UI", 0, 16));

        
        java.awt.Dimension btnSize = new java.awt.Dimension(220, 45);
        jButtonCadastrar.setPreferredSize(btnSize);
        jButtonAtualizar.setPreferredSize(btnSize);
        jButtonDeletar.setPreferredSize(btnSize);
        jButtonPedidos.setPreferredSize(btnSize);
        jButtonCadastrar.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jButtonAtualizar.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jButtonDeletar.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jButtonPedidos.setFont(new java.awt.Font("Segoe UI", 1, 16));

        
        jButtonCadastrar.addActionListener(e -> cadastrarCliente());
        jButtonAtualizar.addActionListener(e -> atualizarCliente());
        jButtonDeletar.addActionListener(e -> deletarCliente());
        jButtonPedidos.addActionListener(e -> {
            int selected = jTable1.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um cliente para abrir os pedidos.");
                return;
            }
            int idCliente = (int) jTable1.getValueAt(selected, 0);
            new Tela_Pedidos(idCliente, true).setVisible(true);
            this.dispose();
        });

      
        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int selected = jTable1.getSelectedRow();
                jTextPaneNome.setText(jTable1.getValueAt(selected, 1).toString());
                jTextPaneCPF.setText(jTable1.getValueAt(selected, 2).toString());
                jTextPaneTelefone.setText(jTable1.getValueAt(selected, 3).toString());
            }
        });

     
        jTextPaneBusca.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                String busca = jTextPaneBusca.getText();
                buscarClientes(busca);
            }
        });

    
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

       layout.setHorizontalGroup(
    layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 900, GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPaneBusca, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPaneNome, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPaneTelefone, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPaneCPF, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)))))
                )
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jButtonCadastrar, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                .addComponent(jButtonAtualizar, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                .addComponent(jButtonDeletar, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                .addComponent(jButtonPedidos, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
        )
        .addContainerGap(1, Short.MAX_VALUE)
);

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    // Campos à esquerda
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPaneBusca, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPaneNome, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPaneTelefone, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPaneCPF, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                    )
                   
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonCadastrar, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonAtualizar, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonDeletar, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonPedidos, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                    )
                )
                .addContainerGap(100, Short.MAX_VALUE)
        );
        

        pack();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Tela_Clientes().setVisible(true));
    }

   
    private JButton jButtonCadastrar;
    private JButton jButtonAtualizar;
    private JButton jButtonDeletar;
    private JButton jButtonPedidos;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPaneBusca;
    private JScrollPane jScrollPaneCPF;
    private JScrollPane jScrollPaneNome;
    private JScrollPane jScrollPaneTelefone;
    private JTable jTable1;
    private JTextPane jTextPaneBusca;
    private JTextPane jTextPaneCPF;
    private JTextPane jTextPaneNome;
    private JTextPane jTextPaneTelefone;
}
