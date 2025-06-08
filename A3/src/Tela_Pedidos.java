import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Tela_Pedidos extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Tela_Pedidos.class.getName());
    
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

    private String obterNomeCliente(int idCliente) {
    String sql = "SELECT nomeCliente FROM clientes WHERE idCliente = ?";

    try (Connection conn = conectarBD();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, idCliente);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getString("nomeCliente");
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Erro ao buscar nome do cliente: " + e.getMessage(),
            "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
    }

    return null;
    }
    
    private void carregarPedidosNaTabela() {
    String sql = "SELECT idPedido, idCliente, valorPedido, statusPedido FROM pedidos";

    DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
    modelo.setRowCount(0);

    try (Connection conn = conectarBD();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            int idPedido   = rs.getInt("idPedido");
            int idCliente  = rs.getInt("idCliente");
            
            // Atualiza o valor total ANTES de mostrar na tabela
            atualizarValorTotalDoPedido(idPedido);

            // Agora busca o valor atualizado
            double valor   = obterValorPedido(idPedido, conn);
            String status  = rs.getString("statusPedido");

            // Buscar o nome do cliente com base no ID
            String nomeCliente = obterNomeCliente(idCliente);
            if (nomeCliente == null) {
                nomeCliente = ""; 
            }

            modelo.addRow(new Object[] {
                idPedido,
                nomeCliente,
                valor,
                status
            });
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Erro ao carregar pedidos: " + e.getMessage(),
            "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
     }
}

    @SuppressWarnings("ConvertToTryWithResources")
   private void atualizarStatusPedidoSelecionado() {
    int linhaSelecionada = jTable1.getSelectedRow();

    if (linhaSelecionada == -1) {
        JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela.");
        return;
    }

    int idPedido = (int) jTable1.getValueAt(linhaSelecionada, 0);
    String novoStatus = (String) jComboBox1.getSelectedItem();

    if (novoStatus == null || novoStatus.trim().isEmpty() || novoStatus.equals("Selecione o Status")) {
        JOptionPane.showMessageDialog(this, "Por favor, selecione um status válido.");
        return;
    }

    try (Connection conn = Tela_PopularPedido.conectarBD()) {
        String sql = "UPDATE pedidos SET statusPedido = ? WHERE idPedido = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, novoStatus);
        stmt.setInt(2, idPedido);

        int linhasAfetadas = stmt.executeUpdate();
        stmt.close();

        if (linhasAfetadas > 0) {
            JOptionPane.showMessageDialog(this, "Status do pedido atualizado para: " + novoStatus);
            carregarPedidosNaTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar o status do pedido.");
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Erro no banco de dados: " + e.getMessage());
    }
}

    private int criarPedidoComValorZero(int idCliente) {
    String sql = 
        "INSERT INTO pedidos (idCliente, valorPedido, statusPedido) " +
        "VALUES (?, 0.0, 'Pendente')";

    try (Connection conn = Tela_PopularPedido.conectarBD();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setInt(1, idCliente);
        int afetadas = stmt.executeUpdate();
        if (afetadas == 0) {
            // Não inseriu nada → retorna erro
            return -1;
        }

        // Recupera o idPedido gerado pelo AUTO_INCREMENT
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -1;
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Erro ao criar pedido: " + e.getMessage(),
            "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        return -1;
    }
}

    private int obterIdClientePorPedido(int idPedido) {    
    String sql = "SELECT idCliente FROM pedidos WHERE idPedido = ?";
    try (Connection conn = Tela_PopularPedido.conectarBD();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, idPedido);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("idCliente");
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Erro ao buscar cliente do pedido: " + e.getMessage(),
            "Erro de Banco", JOptionPane.ERROR_MESSAGE);
    }
    return -1;
}
   
    @SuppressWarnings("ConvertToTryWithResources")
    public void atualizarValorTotalDoPedido(int idPedido) {
    String sqlSoma = "SELECT SUM(subtotal) AS total FROM itens_pedido WHERE idPedido = ?";
    String sqlUpdate = "UPDATE pedidos SET valorPedido = ? WHERE idPedido = ?";

    try (Connection conn = conectarBD();
         PreparedStatement stmtSoma = conn.prepareStatement(sqlSoma)) {

        stmtSoma.setInt(1, idPedido);
        ResultSet rs = stmtSoma.executeQuery();

        double total = 0.0;
        if (rs.next()) {
            total = rs.getDouble("total");
        }
        rs.close();

        try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
            stmtUpdate.setDouble(1, total);
            stmtUpdate.setInt(2, idPedido);
            stmtUpdate.executeUpdate();
          
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null,
            "Erro ao atualizar valor do pedido: " + e.getMessage(),
            "Erro de Banco", JOptionPane.ERROR_MESSAGE);
    }
   
}

    @SuppressWarnings("ConvertToTryWithResources")
    private void excluirPedidoSelecionado() {
    int linhaSelecionada = jTable1.getSelectedRow();

    if (linhaSelecionada == -1) {
        JOptionPane.showMessageDialog(this, "Selecione um pedido para excluir.");
        return;
    }

    int idPedido = (int) jTable1.getValueAt(linhaSelecionada, 0); // coluna 0 = idPedido

    int confirmacao = JOptionPane.showConfirmDialog(
        this,
        "Tem certeza que deseja excluir o pedido #" + idPedido + "?",
        "Confirmação",
        JOptionPane.YES_NO_OPTION
    );

    if (confirmacao == JOptionPane.YES_OPTION) {
        try (Connection conn = Tela_PopularPedido.conectarBD()) {
            // Primeiro, exclui os itens associados ao pedido (evita violação de integridade referencial)
            String sqlItens = "DELETE FROM itens_pedido WHERE idPedido = ?";
            PreparedStatement stmtItens = conn.prepareStatement(sqlItens);
            stmtItens.setInt(1, idPedido);
            stmtItens.executeUpdate();
            stmtItens.close();

            // Agora exclui o pedido em si
            String sqlPedido = "DELETE FROM pedidos WHERE idPedido = ?";
            PreparedStatement stmtPedido = conn.prepareStatement(sqlPedido);
            stmtPedido.setInt(1, idPedido);
            int linhasAfetadas = stmtPedido.executeUpdate();
            stmtPedido.close();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(this, "Pedido excluído com sucesso!");
                carregarPedidosNaTabela(); // Atualiza a tabela na tela
            } else {
                JOptionPane.showMessageDialog(this, "Erro: o pedido não pôde ser excluído.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + e.getMessage());
        }
    }
    carregarPedidosNaTabela();
}

    @SuppressWarnings("unused")
    private int idPedido = -1;
    public Tela_Pedidos() {
        // chama a versão que recebe parâmetro, passando -1 para “nenhuma seleção”
        this(-1);
    }

    /**
     * Construtor que opcionalmente seleciona um pedido na tabela.
     * Se idPedidoForSelecionado for -1, nenhuma linha é marcada.
     */
     public Tela_Pedidos(int idPedidoForSelecionado) {
        initComponents();
        carregarPedidosNaTabela();
        this.idPedido = idPedidoForSelecionado;

        // Se receber um idPedido válido (>= 0), tenta selecionar essa linha
        if (idPedidoForSelecionado >= 0) {
            selecionarLinhaPorId(idPedidoForSelecionado);
        }
        
    }
     
     public Tela_Pedidos(int idClienteSelecionado, boolean preencherCampoCliente) {
    initComponents();
    carregarPedidosNaTabela();
    if (preencherCampoCliente) {
        jTextPane2.setText(String.valueOf(idClienteSelecionado));
    }
}

    /**
     * Percorre as linhas de jTable1 procurando, na coluna 0 (ID_Pedido),
     * o valor igual a idPedido. Se encontrar, marca e torna visível.
     */
    private void selecionarLinhaPorId(int idPedido) {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((int) model.getValueAt(i, 0) == idPedido) {
                jTable1.setRowSelectionInterval(i, i);
                jTable1.scrollRectToVisible(jTable1.getCellRect(i, 0, true));
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")

    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPane2 = new javax.swing.JTextPane();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabelBusca = new javax.swing.JLabel("Buscar:");
        jTextFieldBusca = new javax.swing.JTextField(15);
        jLabelFiltro = new javax.swing.JLabel("Status:");
        jComboBoxFiltro = new javax.swing.JComboBox<>(new String[] {
            "Todos", "Pendente", "Concluido", "Cancelado"
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID_Pedido", "Nome_Cliente", "Valor_Total", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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

        jLabel2.setText("ID do cliente");

        jLabel4.setText("Status:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecione o Status", "Cancelado", "Pendente", "Concluido" }));
        jComboBox1.addActionListener((java.awt.event.ActionEvent evt) -> {
            jComboBox1ActionPerformed(evt);
        });

        jScrollPane3.setViewportView(jTextPane2);

        jButton2.setText("Atualizar Pedido");
        jButton2.addActionListener((java.awt.event.ActionEvent evt) -> {
            jButton2ActionPerformed(evt);
        });

        jButton3.setText("Deletar Pedido");
        jButton3.addActionListener((java.awt.event.ActionEvent evt) -> {
            jButton3ActionPerformed(evt);
        });

        jButton5.setText("Incluir Produtos");
        jButton5.addActionListener(this::jButton5ActionPerformed);

        jLabelBusca.setText("Buscar Pedido:");

        jTextFieldBusca.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarPedidosNaTabela(jTextFieldBusca.getText(), (String) jComboBoxFiltro.getSelectedItem());
            }
        });

        jLabelFiltro.setText("Filtrar por:");

        jComboBoxFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Pendente", "Concluido", "Cancelado" }));
        jComboBoxFiltro.addActionListener((java.awt.event.ActionEvent evt) -> {
            buscarPedidosNaTabela(jTextFieldBusca.getText(), (String) jComboBoxFiltro.getSelectedItem());
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20)
                .addComponent(jLabelBusca)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldBusca, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30)
                .addComponent(jLabelFiltro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelBusca)
                    .addComponent(jTextFieldBusca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelFiltro)
                    .addComponent(jComboBoxFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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

    jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mousePressed(java.awt.event.MouseEvent evt) {
            jTable1.clearSelection();
        }
    });

    pack();
    }

    private void jComboBox1ActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {
      
    }

    private void jButton2ActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {
        atualizarStatusPedidoSelecionado();
    }

    private void jButton3ActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {
        excluirPedidoSelecionado();
    }

    private void jButton5ActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt) {
    int linhaSelecionada = jTable1.getSelectedRow();
    int idPedido;
    int idCliente;

    if (linhaSelecionada == -1) {
        //  Nenhuma linha selecionada → criar pedido novo a partir de idCliente digitado
        try {
            idCliente = Integer.parseInt(jTextPane2.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Digite um ID de cliente válido.",
                "Valor Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Cria o pedido 
        idPedido = criarPedidoComValorZero(idCliente);
        if (idPedido < 0) {
            // Se falhar ao criar, aborta a função
            return;
        }

    } else {
        // Linha selecionada → pegar idPedido e idCliente da tabela
        idPedido  = (int) jTable1.getValueAt(linhaSelecionada, 0); // coluna 0 = ID_Pedido
        // Agora busca o idCliente a partir do idPedido
        idCliente = obterIdClientePorPedido(idPedido);
        if (idCliente < 0) {
            JOptionPane.showMessageDialog(this,
                "Erro ao obter ID de cliente para o pedido selecionado.",
                "Erro de Banco", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }


    
    new Tela_PopularPedido(idPedido, idCliente).setVisible(true);
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
    
        java.awt.EventQueue.invokeLater(() -> new Tela_Pedidos().setVisible(true));
    }

    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBoxFiltro;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelBusca;
    private javax.swing.JLabel jLabelFiltro;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextPane jTextPane2;
    private javax.swing.JTextField jTextFieldBusca;
  
 
    private double obterValorPedido(int idPedido, Connection conn) {
    String sql = "SELECT valorPedido FROM pedidos WHERE idPedido = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, idPedido);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("valorPedido");
            }
        }
    } catch (SQLException e) {
      
    }
    return 0.0;
}

    private void buscarPedidosNaTabela(String nomeCliente, String status) {
    StringBuilder sql = new StringBuilder(
        "SELECT p.idPedido, p.idCliente, p.valorPedido, p.statusPedido " +
        "FROM pedidos p JOIN clientes c ON p.idCliente = c.idCliente WHERE 1=1"
    );
    if (nomeCliente != null && !nomeCliente.trim().isEmpty()) {
        sql.append(" AND c.nomeCliente LIKE ?");
    }
    if (status != null && !"Todos".equals(status)) {
        sql.append(" AND statusPedido = ?");
    }

    DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
    modelo.setRowCount(0);

    try (Connection conn = conectarBD();
         PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

        int paramIndex = 1;
        if (nomeCliente != null && !nomeCliente.trim().isEmpty()) {
            pstmt.setString(paramIndex++, "%" + nomeCliente + "%");
        }
        if (status != null && !"Todos".equals(status)) {
            pstmt.setString(paramIndex++, status);
        }

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            int idPedido   = rs.getInt("idPedido");
            int idCliente  = rs.getInt("idCliente");
            atualizarValorTotalDoPedido(idPedido);
            double valor   = obterValorPedido(idPedido, conn);
            String statusPedido  = rs.getString("statusPedido");
            String nomeCli = obterNomeCliente(idCliente);
            if (nomeCli == null) nomeCli = "";
            modelo.addRow(new Object[] {
                idPedido,
                nomeCli,
                valor,
                statusPedido
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Erro ao buscar pedidos: " + e.getMessage(),
            "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
    }
}
}
