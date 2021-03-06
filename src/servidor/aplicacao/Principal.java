package servidor.aplicacao;

import servidor.jogo.Jogo;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.UIManager;
import servidor.frames.*;
import compartilhado.modelo.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLException;

public class Principal {

    private static boolean executando;
    public static int id;
    
    public static FrameInicio frmInicio;
    public static FramePrincipal frmPrincipal;
    
    public static String endereco;
    public static int porta;
    public static int portaRMI;
    
    private static ServerSocket servidor;
    public static GerenciadorBD gerenciador;
    
    public static ArrayList<Usuario> usuarios;
    public static ArrayList<Grupo> grupos;
    public static ArrayList<Conexao> conexoes;
    public static ArrayList<Jogo> jogos;
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        frmInicio = new FrameInicio();
        frmPrincipal = new FramePrincipal();
        frmInicio.setVisible(true);
    }
    
    public static void iniciarServidor(String e, int p) throws IOException, SQLException, NotBoundException{
        endereco = e;
        porta = p;
        portaRMI = porta + 1;
        
        id = 0; // id vinculado a conexão
        
        conexoes = new ArrayList<>();
        servidor = new ServerSocket(porta);
        
        Principal.frmPrincipal.enviarLog("O servidor está sendo iniciado...");
        
        frmPrincipal.enviarLog("Iniciando gerenciador de banco de dados...");
        gerenciador = new GerenciadorBD("localhost/mensageiro", "root", "");
        
        usuarios = gerenciador.getListaUsuarios();
        frmPrincipal.enviarLog("Lista de usuários recuperada");
        
        grupos = gerenciador.getListaGrupos();
        frmPrincipal.enviarLog("Lista de grupos recuperada");
        
        jogos = new ArrayList<>();
        
        System.setProperty("java.rmi.server.hostname", endereco);
        LocateRegistry.createRegistry(portaRMI); // inicia o registro RMI na porta informada + 1
        frmPrincipal.enviarLog("Registro RMI iniciado na porta " + portaRMI);
        
        executando = true;
        frmPrincipal.alterarStatus(2);
        frmPrincipal.enviarLog("Servidor iniciado com sucesso na porta " + porta);   
        
        while(executando){
            Socket socket = servidor.accept(); // aceita uma conexão do cliente
            Conexao conexao = new Conexao(id, socket); // cria a conexão passando id e socket
            conexao.conectar(); // faz a conexão principal e RMI
            Principal.conexoes.add(conexao); // adiciona na lista
            id++; // incrementa a id
            Thread t = conexao; // cria uma nova thread
            t.start(); // executa a thread
        }
    }

    public static void pararServidor() throws IOException{
        executando = false;
        servidor.close();
        for (Conexao conexao : conexoes) {
            conexao.desconectar();
        }
    }
    
    public static Conexao getConexao(int id){
        for (Conexao conexao : conexoes) {
            if(conexao.getIdConexao() == id)
                return conexao;
        }
        return null;
    }
    
    public static Conexao getConexaoPorIdUsuario(int id){
        for (Conexao conexao : conexoes) {
            if(conexao.getIdCliente() == id)
                return conexao;
        }
        return null;
    }
    
    public static Jogo getJogo(int id){
        for (Jogo jogo : jogos) {
            if(jogo.getIdGrupo() == id)
                return jogo;
        }
        return null;
    }
}
