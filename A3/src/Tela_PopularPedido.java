
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class Tela_PopularPedido extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Tela_PopularPedido.class.getName());

    public static Connection conectarBD() {
    String url = "jdbc:mysql://localhost:3306/lanchonete";
    String usuario = "root";
    String senha = "6gabrielnesi";

    try {
        return DriverManager.getConnection(url, usuario, senha);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao conectar com o banco de dados: " + e.getMessage());
        return null;
        }
    }

    
  
    private int pedidoIdRecebido;
    private int clienteIdRecebido;

    



    public Tela_PopularPedido(int idPedido, int idCliente) {
    initComponents();
    carregarProdutosDoPedido(idPedido);
    this.pedidoIdRecebido = idPedido;
    this.clienteIdRecebido = idCliente;

    System.out.println("ID do pedido recebido: " + pedidoIdRecebido);
   
           // Configura para NÃO fechar automaticamente ao clicar no X
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Adiciona listener para capturar o evento do botão fechar (X)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Abre a outra janela
                new Tela_Pedidos().setVisible(true);
                // Fecha essa janela atual
                dispose();
            }
        });

    }
    @SuppressWarnings("ConvertToTryWithResources")
    private void inserirItemCompletoNoPedido() {
    int idPedido  = this.pedidoIdRecebido;
    int idCliente = this.clienteIdRecebido;

    try {
        // 1) Ler idProduto e quantidade dos campos de texto
        int idProduto  = Integer.parseInt(jTextPane2.getText().trim());
        int quantidade = Integer.parseInt(jTextPane3.getText().trim());

        // 2) Abrir conexão
        Connection conn = Tela_PopularPedido.conectarBD();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Falha ao conectar ao banco.");
            return;
        }

        // 3) Buscar valorUnitario na tabela produtos
        String sqlBuscaProduto = "SELECT precoProduto FROM produtos WHERE idProduto = ?";
        PreparedStatement stmtBusca = conn.prepareStatement(sqlBuscaProduto);
        stmtBusca.setInt(1, idProduto);
        ResultSet rsProd = stmtBusca.executeQuery();

        if (!rsProd.next()) {
            JOptionPane.showMessageDialog(this, "Produto não encontrado (ID " + idProduto + ").");
            rsProd.close();
            stmtBusca.close();
            conn.close();
            return;
        }

        double valorUnitario = rsProd.getDouble("precoProduto");
        rsProd.close();
        stmtBusca.close();

        // 4) Calcular subtotal
        double subtotal = quantidade * valorUnitario;

        // 5) Inserir na tabela itens_pedido
        String sqlInsert = ""
            + "INSERT INTO itens_pedido "
            + "(idPedido, idProduto, quantidade, valorUnitario, subtotal) "
            + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert);
        stmtInsert.setInt(1, idPedido);
        stmtInsert.setInt(2, idProduto);
        stmtInsert.setInt(3, quantidade);
        stmtInsert.setDouble(4, valorUnitario);
        stmtInsert.setDouble(5, subtotal);

        int afetadas = stmtInsert.executeUpdate();
        if (afetadas > 0) {
            JOptionPane.showMessageDialog(this, 
                "Item adicionado ao pedido (Pedido: " + idPedido 
                + ", Cliente: " + idCliente + ").");
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao inserir item no pedido.");
        }

        stmtInsert.close();
        conn.close();

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "ID de produto e quantidade devem ser números inteiros.");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Erro de banco: " + e.getMessage());
        
    }
    carregarProdutosDoPedido(pedidoIdRecebido);
}


 

private void carregarProdutosDoPedido(int idPedido) {
    // Obtém o modelo da tabela e limpa o conteúdo anterior
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.setRowCount(0);

    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
        conn = Tela_PopularPedido.conectarBD();
        if (conn == null) {
            JOptionPane.showMessageDialog(this,
                "Não foi possível conectar ao banco para carregar os itens.",
                "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            return;
        }


        String sql = ""
            + "SELECT ip.idItem, ip.idProduto, p.nomeProduto, ip.quantidade, "
            + "       ip.valorUnitario, ip.subtotal "
            + "FROM itens_pedido ip "
            + "  JOIN produtos p ON ip.idProduto = p.idProduto "
            + "WHERE ip.idPedido = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, idPedido);
        rs = stmt.executeQuery();

      
        while (rs.next()) {
            Object[] row = new Object[] {
                rs.getInt("idItem"),         // ID_Item
                rs.getInt("idProduto"),      // ID_Produto
                rs.getString("nomeProduto"), // Nome_Produto
                rs.getInt("quantidade"),     // Quantidade
                rs.getDouble("valorUnitario"), // Preço Unitário
                rs.getDouble("subtotal")     // Subtotal
            };
            model.addRow(row);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Erro ao carregar produtos do pedido: " + e.getMessage(),
            "Erro de Banco", JOptionPane.ERROR_MESSAGE);
    } finally {
        try { if (rs    != null) rs.close();    } catch (SQLException ex) {}
        try { if (stmt  != null) stmt.close();  } catch (SQLException ex) {}
        try { if (conn  != null) conn.close();  } catch (SQLException ex) {}
    }
}

private void deletarItemSelecionado() {
    int linhaSelecionada = jTable1.getSelectedRow();
    if (linhaSelecionada == -1) {
        JOptionPane.showMessageDialog(this,
            "Selecione um item para excluir.",
            "Nenhuma linha selecionada",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Confirmação antes de excluir
    int confirmacao = JOptionPane.showConfirmDialog(this,
        "Tem certeza que deseja excluir este item?",
        "Confirmar exclusão",
        JOptionPane.YES_NO_OPTION);

    if (confirmacao != JOptionPane.YES_OPTION) {
        return; // Cancela a exclusão se o usuário não confirmar
    }

    // Supondo que a coluna 0 da jTable1 é idItem
    int idItem = (int) jTable1.getValueAt(linhaSelecionada, 0);

    String sqlDelete = "DELETE FROM itens_pedido WHERE idItem = ?";
    try (Connection conn = conectarBD();
         PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {

        stmt.setInt(1, idItem);
        int afetadas = stmt.executeUpdate();
        if (afetadas > 0) {
            JOptionPane.showMessageDialog(this,
                "Item removido com sucesso.",
                "Exclusão realizada",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Falha ao excluir o item.",
                "Erro de Banco",
                JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Erro ao excluir item: " + e.getMessage(),
            "Erro de Banco", JOptionPane.ERROR_MESSAGE);
    }

 
    carregarProdutosDoPedido(pedidoIdRecebido);
}


    public Tela_PopularPedido() {
         initComponents();
         
 
       
    }


    @SuppressWarnings("unchecked")
  
    private void initComponents() {

        ProdutoPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextPane3 = new javax.swing.JTextPane();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ProdutoPanel.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID_Item", "ID_Produto", "Nome_Produto", "Quantidade", "Preço Unitário", "Subtotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setText("ID do Produto");

        jScrollPane3.setViewportView(jTextPane2);

        jButton2.setText("Voltar");
        jButton2.addActionListener((java.awt.event.ActionEvent evt) -> {
            jButton2ActionPerformed(evt);
        });

        jButton3.setText("Deletar Produto");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jButton5.setText("Incluir Produto");
        jButton5.addActionListener((java.awt.event.ActionEvent evt) -> {
            jButton5ActionPerformed(evt);
        });

        jScrollPane4.setViewportView(jTextPane3);

        jLabel3.setText("Quantidade");

        javax.swing.GroupLayout ProdutoPanelLayout = new javax.swing.GroupLayout(ProdutoPanel);
        ProdutoPanel.setLayout(ProdutoPanelLayout);
        ProdutoPanelLayout.setHorizontalGroup(
            ProdutoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(ProdutoPanelLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(ProdutoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(ProdutoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45))
        );
        ProdutoPanelLayout.setVerticalGroup(
            ProdutoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProdutoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ProdutoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ProdutoPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ProdutoPanelLayout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ProdutoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ProdutoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void jButton3ActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
      deletarItemSelecionado();
    }

    private void jButton5ActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
    inserirItemCompletoNoPedido(); 
    }


    private void jButton2ActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        new Tela_Pedidos(this.pedidoIdRecebido).setVisible(true);
        this.dispose();
    }
    
   
    public static void main(String args[]) {
    
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Tela_PopularPedido().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ProdutoPanel;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextPane jTextPane2;
    private javax.swing.JTextPane jTextPane3;
   
}
