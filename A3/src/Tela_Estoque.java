import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Tela_Estoque extends javax.swing.JFrame {
      
    public Tela_Estoque() {
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        carregarDados();
    }

    private Connection conectar() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/lanchonete";
        String user = "root";
        String password = "6gabrielnesi";
        return DriverManager.getConnection(url, user, password);
    }

    // Carregar dados na tabela
    private void carregarDados() {
        try (Connection conn = conectar()) {
            String sql = "SELECT * FROM produtos";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
            modelo.setRowCount(0);

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("idProduto"),
                    rs.getString("nomeProduto"),
                    rs.getString("catProduto"),
                    rs.getDouble("precoProduto"),
                    rs.getInt("qntdProduto")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    private void buscarProdutos(String termo) {
    try (Connection conn = conectar()) {
        String sql = "SELECT * FROM produtos WHERE nomeProduto LIKE ? OR catProduto LIKE ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "%" + termo + "%");
        stmt.setString(2, "%" + termo + "%");
        ResultSet rs = stmt.executeQuery();

        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getInt("idProduto"),
                rs.getString("nomeProduto"),
                rs.getString("catProduto"),
                rs.getDouble("precoProduto"),
                rs.getInt("qntdProduto")
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao buscar: " + e.getMessage());
    }
}

    @SuppressWarnings({"unchecked", "Convert2Lambda"})
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        JLabel jLabelBusca = new JLabel("Buscar:");
        JTextField jTextFieldBusca = new JTextField();
        jTextFieldBusca.setColumns(20);
        JButton jButtonBuscar = new JButton("Buscar");

        jButton1.addActionListener((java.awt.event.ActionEvent evt) -> {
            jButton1ActionPerformed(evt);
        });

jButton2.addActionListener(new java.awt.event.ActionListener() {
    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton2ActionPerformed(evt);
    }
});

jButton3.addActionListener((java.awt.event.ActionEvent evt) -> {
    jButton3ActionPerformed(evt);
        });

        jButtonBuscar.addActionListener(e -> {
    String termo = jTextFieldBusca.getText();
    buscarProdutos(termo);
});

        setTitle("Tela de Estoque");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "ID_Produto", "Nome_Produto", "Categoria_Produto", "Preço_Produto", "qntd_Produto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class
            };
            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setText("Nome:");
        jLabel3.setText("Categoria:");
        jLabel4.setText("Preço:");
        jLabel5.setText("Quantidade:");

        jButton1.setText("Cadastrar Produto");
        jButton2.setText("Atualizar Produto");
        jButton3.setText("Apagar Produto");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelBusca)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldBusca, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonBuscar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBusca)
                    .addComponent(jTextFieldBusca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBuscar))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }
    
    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
         int linha = jTable1.getSelectedRow();
        if (linha >= 0) {
            jTextField1.setText(jTable1.getValueAt(linha, 1).toString());
            jTextField4.setText(jTable1.getValueAt(linha, 2).toString());
            jTextField2.setText(jTable1.getValueAt(linha, 3).toString());
            jTextField6.setText(jTable1.getValueAt(linha, 4).toString());
        }
    }
        //cadastro dos produtos
     private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String nome = jTextField1.getText();
        String categoria = jTextField4.getText();
        double preco = Double.parseDouble(jTextField2.getText());
        int quantidade = Integer.parseInt(jTextField6.getText());

        try (Connection conn = conectar()) {
            String sql = "INSERT INTO produtos (nomeProduto, catProduto, precoProduto, qntdProduto) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setString(2, categoria);
            stmt.setDouble(3, preco);
            stmt.setInt(4, quantidade);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Produto cadastrado com sucesso!");
            carregarDados();
   Relatorio_Entrada.getInstancia().registrarEntrada("Um produto foi registrado, nome: " + nome + "  Quantidade: " + quantidade);


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar: " + e.getMessage());
        }
    }

    // Atualizar Produto
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        int linha = jTable1.getSelectedRow();
        if (linha >= 0) {
            int id = (int) jTable1.getValueAt(linha, 0);
            String nome = jTextField1.getText();
            String categoria = jTextField4.getText();
            double preco = Double.parseDouble(jTextField2.getText());
            int quantidade = Integer.parseInt(jTextField6.getText());

            int quantidadeAnterior = -1;
            String nomeAnterior = "";
            // Buscar quantidade e nome anterior no banco
            try (Connection conn = conectar()) {
                String sqlBusca = "SELECT nomeProduto, qntdProduto FROM produtos WHERE idProduto = ?";
                PreparedStatement stmtBusca = conn.prepareStatement(sqlBusca);
                stmtBusca.setInt(1, id);
                ResultSet rs = stmtBusca.executeQuery();
                if (rs.next()) {
                    nomeAnterior = rs.getString("nomeProduto");
                    quantidadeAnterior = rs.getInt("qntdProduto");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao buscar quantidade anterior: " + e.getMessage());
            }

            try (Connection conn = conectar()) {
                String sql = "UPDATE produtos SET nomeProduto = ?, catProduto = ?, precoProduto = ?, qntdProduto = ? WHERE idProduto = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nome);
                stmt.setString(2, categoria);
                stmt.setDouble(3, preco);
                stmt.setInt(4, quantidade);
                stmt.setInt(5, id);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Produto atualizado com sucesso!");
                carregarDados();

                // Só registra se a quantidade aumentou
                if (quantidade > quantidadeAnterior) {
                    Relatorio_Entrada.getInstancia().registrarEntrada(
                        "Produto atualizado: Nome anterior: " + nomeAnterior +
                        " | Nome novo: " + nome +
                        " | Quantidade anterior: " + quantidadeAnterior +
                        " | Nova quantidade: " + quantidade
                    );
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao atualizar: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um produto para atualizar.");
        }
    }

    // Apagar Produto
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        int linha = jTable1.getSelectedRow();
        if (linha >= 0) {
            int id = (int) jTable1.getValueAt(linha, 0);

            int confirm = JOptionPane.showConfirmDialog(null, "Deseja realmente apagar esse produto?", "Confirmação", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = conectar()) {
                    String sql = "DELETE FROM produtos WHERE idProduto = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Produto apagado com sucesso!");
                    carregarDados();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao apagar: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um produto para apagar.");
        }
    }
     public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Tela_Estoque().setVisible(true));
    }


    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration                   
}
