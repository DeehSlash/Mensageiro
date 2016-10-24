package servidor.frames;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import servidor.aplicacao.Principal;

public class FramePrincipal extends javax.swing.JFrame {
    
    private int usuariosConectados;
    
    public FramePrincipal() {
        initComponents();
        addListeners(); // chama a função que adiciona os listeners
        usuariosConectados = 0;
    }

    private void addListeners(){
        btnIniciar.addActionListener((ActionEvent e) -> { // Evento de clique no botão iniciar
            if(txtPorta.getText().isEmpty()) // mostra uma mensagem de erro caso o campo porta esteja vazio
                JOptionPane.showMessageDialog(this, "O campo porta não pode estar vazio!", "Erro", JOptionPane.ERROR_MESSAGE);
            else{
                txtLog.setText(null);
                lblStatus.setText("Iniciando...");
                lblStatus.setForeground(Color.yellow);
                new Thread(() -> {
                    try {
                        Principal.iniciarServidor(Integer.parseInt(txtPorta.getText())); // chama o método que faz o loop dos threads
                        Principal.pararServidor();
                        lblStatus.setText("Parado");
                        lblStatus.setForeground(Color.red);
                    } catch (IOException | SQLException ex) {
                        ex.printStackTrace();
                        enviarLog("Exceção: " + ex.getMessage());
                    }
                }).start();
                lblStatus.setText("Rodando");
                lblStatus.setForeground(Color.GREEN);
                btnIniciar.setEnabled(false);
                btnParar.setEnabled(true);
                txtPorta.setEnabled(false);
            }
        });
        
        btnParar.addActionListener((ActionEvent e) -> {
            try {
                Principal.pararServidor(); // chama a função que para o servidor e desvincula da porta usada
                enviarLog("Servidor parado com sucesso");
                lblStatus.setText("Parado");
                lblStatus.setForeground(Color.red);
                btnIniciar.setEnabled(true);
                btnParar.setEnabled(false);
                txtPorta.setEnabled(true);
            } catch (IOException ex) {
                ex.printStackTrace();
                enviarLog("Exceção: " + ex.getMessage());
            }
        });
    }
    
    public void alterarUsuarios(boolean incremento){
        if(incremento) usuariosConectados++; else usuariosConectados--;
        lblUsuariosConectados.setText(Integer.toString(usuariosConectados));
    }
    
    public void enviarLog(String mensagem){
        txtLog.append(mensagem + "\n");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlConexao = new javax.swing.JPanel();
        lblEndereco = new javax.swing.JLabel();
        txtPorta = new javax.swing.JTextField();
        btnIniciar = new javax.swing.JButton();
        btnParar = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        pnlInfo = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        lblUsuarios = new javax.swing.JLabel();
        lblUsuariosConectados = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Servidor");
        setMaximumSize(new java.awt.Dimension(450, 500));
        setMinimumSize(new java.awt.Dimension(450, 500));
        setName("frmPrincipal"); // NOI18N
        setPreferredSize(new java.awt.Dimension(450, 500));
        setResizable(false);
        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWeights = new double[] {1.0};
        getContentPane().setLayout(layout);

        pnlConexao.setBorder(javax.swing.BorderFactory.createTitledBorder("Conexão"));
        java.awt.GridBagLayout pnlConexaoLayout = new java.awt.GridBagLayout();
        pnlConexaoLayout.columnWeights = new double[] {1.0};
        pnlConexao.setLayout(pnlConexaoLayout);

        lblEndereco.setText("Porta");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 5, 5);
        pnlConexao.add(lblEndereco, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 5, 20);
        pnlConexao.add(txtPorta, gridBagConstraints);

        btnIniciar.setText("Iniciar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 20, 90);
        pnlConexao.add(btnIniciar, gridBagConstraints);

        btnParar.setText("Parar");
        btnParar.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 20, 20);
        pnlConexao.add(btnParar, gridBagConstraints);

        lblStatus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(255, 0, 0));
        lblStatus.setText("Parado");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 20, 5);
        pnlConexao.add(lblStatus, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 5, 20);
        getContentPane().add(pnlConexao, gridBagConstraints);

        pnlInfo.setBorder(javax.swing.BorderFactory.createTitledBorder("Informações"));
        pnlInfo.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setMaximumSize(new java.awt.Dimension(170, 100));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(170, 100));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(170, 100));

        txtLog.setEditable(false);
        txtLog.setColumns(20);
        txtLog.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtLog.setRows(5);
        txtLog.setToolTipText("");
        txtLog.setWrapStyleWord(true);
        txtLog.setMaximumSize(new java.awt.Dimension(170, 100));
        txtLog.setMinimumSize(new java.awt.Dimension(170, 100));
        txtLog.setName(""); // NOI18N
        txtLog.setPreferredSize(new java.awt.Dimension(170, 100));
        jScrollPane1.setViewportView(txtLog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 100;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 5, 20);
        pnlInfo.add(jScrollPane1, gridBagConstraints);

        lblUsuarios.setText("Usuários conectados:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 20, 5);
        pnlInfo.add(lblUsuarios, gridBagConstraints);

        lblUsuariosConectados.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 20, 20);
        pnlInfo.add(lblUsuariosConectados, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 20, 20);
        getContentPane().add(pnlInfo, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIniciar;
    private javax.swing.JButton btnParar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEndereco;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblUsuarios;
    private javax.swing.JLabel lblUsuariosConectados;
    private javax.swing.JPanel pnlConexao;
    private javax.swing.JPanel pnlInfo;
    private javax.swing.JTextArea txtLog;
    private javax.swing.JTextField txtPorta;
    // End of variables declaration//GEN-END:variables

}
