package servidor.aplicacao;

import compartilhado.modelo.*;
import compartilhado.aplicacao.*;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class GerenciadorBD {
        
    private String url = "jdbc:mysql://";
    private String usuario;
    private String senha;
    
    public GerenciadorBD(String url, String usuario, String senha){
        this.url += url;
        this.usuario = usuario;
        this.senha = senha;
    }
    
    public Connection conexao() throws SQLException{
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        return DriverManager.getConnection(url, usuario, senha);
    }
    
    private String convData(Date dt){
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS").format(dt);
    }    
    
    public int autenticarUsuario(UsuarioAutenticacao userAuth) throws SQLException{
        Statement st = conexao().createStatement();
        String SQL = "SELECT * FROM usuario WHERE usuario = '" + userAuth.getUsuario() + "'";
        ResultSet rs = st.executeQuery(SQL);
        if(!rs.next())
            return 0; // retorna 0 caso não achou registro desse usuário
        if(rs.getString("senha").equals(userAuth.getSenha()))
            return 3; // retorna 3 caso as senhas batem
        else
            return 2; // ou então retorna 0 caso a senha esteja incorreta
    }
     
    public boolean alterarUsuario(Usuario usuario) throws SQLException, IOException{
        PreparedStatement ps = conexao().prepareStatement("UPDATE usuario SET usuario = ?, foto = ? WHERE id = ?");
        ps.setString(1, usuario.getUsuario());
        ps.setBlob(2, compartilhado.aplicacao.Imagem.imagemParaBlob(usuario.getFoto().getImage()));
        ps.setInt(3, usuario.getId());
        int result = ps.executeUpdate();
        return result == 1;
    }
    
    public int receberIdGrupoDisponivel() throws SQLException{
        Statement st = conexao().createStatement();
        String SQL = "SELECT * FROM grupo ORDER BY id";
        ResultSet rs = st.executeQuery(SQL);
        rs.last();
        int id = rs.getInt("id");
        return ++id;
    }
    
    public boolean criarGrupo(Grupo grupo) throws SQLException, IOException{
        PreparedStatement ps = conexao().prepareStatement("INSERT INTO grupo (id, nomeGrupo,"
                + " idMembro1, idMembro2, idMembro3, idMembro4, idMembro5, idMembro6, idMembro7, idMembro8, idMembro9, idMembro10, foto)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        int[] m = grupo.getMembros();
        ps.setInt(1, grupo.getId());
        ps.setString(2, grupo.getNome());
        int j = 0;
        for (int i = 3; i < 13; i++) {
            ps.setInt(i, m[j]);
            j++;
        }
        ps.setBlob(13, compartilhado.aplicacao.Imagem.imagemParaBlob(grupo.getFoto().getImage()));
        int result = ps.executeUpdate();
        return result == 1;
    }
    
    public boolean alterarGrupo(Grupo grupo) throws SQLException, IOException{
        PreparedStatement ps = conexao().prepareStatement("UPDATE grupo SET nomeGrupo = ?, idMembro1 = ?, idMembro2 = ?, idMembro3 = ?, idMembro4 = ?,"
                + " idMembro5 = ?, idMembro6 = ?, idMembro7 = ?, idMembro8 = ?, idMembro9 = ?, idMembro10 = ?, foto = ? WHERE id = ?");
        int[] m = grupo.getMembros();
        ps.setString(1, grupo.getNome());
        int j = 0;
        for (int i = 2; i < 12; i++) {
            ps.setInt(i, m[j]);
            j++;
        }
        ps.setBlob(12, compartilhado.aplicacao.Imagem.imagemParaBlob(grupo.getFoto().getImage()));
        ps.setInt(13, grupo.getId());
        int result = ps.executeUpdate();
        return result == 1;
    }
    
    public boolean deletarGrupo(int id) throws SQLException{
        Statement st = conexao().createStatement();
        String SQL = "DELETE FROM grupo WHERE id = '" + id + "'"; 
        int result1 = st.executeUpdate(SQL);
        
        SQL = "DELETE FROM mensagem WHERE destinoTipo = 'G' AND idGrupoDestino = '" + id + "'"; 
        int result2 = st.executeUpdate(SQL);
        
        return (result1 == 1) && (result2 >= 1);
    }
   
    public boolean alterarSenha(int id, String novaSenha) throws SQLException{
        Statement st = conexao().createStatement();
        String SQL = "UPDATE usuario SET senha = '" + novaSenha + "' WHERE id = '" + Integer.toString(id) + "'";
        int result = st.executeUpdate(SQL);
        return result == 1;
    }
    
    public boolean deletarUsuario(int id) throws SQLException{
        Statement st = conexao().createStatement();
        String SQL = "DELETE FROM usuario WHERE id = '" + Integer.toString(id) + "'";
        int result = st.executeUpdate(SQL);
        return result == 1;
    }
    
    public boolean cadastrarUsuario(UsuarioAutenticacao usuario) throws SQLException, IOException, URISyntaxException{
        ImageIcon imagem = new ImageIcon(getClass().getResource("/compartilhado/imagens/usuario.png"));
        Image foto = compartilhado.aplicacao.Imagem.redimensionarImagem(imagem.getImage(), 50, false);
        PreparedStatement ps = conexao().prepareStatement("INSERT INTO usuario (usuario, senha, foto) VALUES (?, ?, ?)");
        ps.setString(1, usuario.getUsuario());
        ps.setString(2, usuario.getSenha());
        ps.setBlob(3, compartilhado.aplicacao.Imagem.imagemParaBlob(foto));
        int result = ps.executeUpdate();
        return result == 1;
    }
    
    public ArrayList<Usuario> getListaUsuarios() throws SQLException, IOException{
        Statement st = conexao().createStatement();
        String SQL = "SELECT * FROM usuario ORDER BY id";
        ResultSet rs = st.executeQuery(SQL);
        ArrayList<Usuario> usuarios = new ArrayList<>();
        while(rs.next()){
            int id = rs.getInt("id");
            if(id != 0){ // se for 0, é o usuário que não é usado, apenas para fins de não dar problema na FOREIGN KEY
                String usuario = rs.getString("usuario");
                Blob blob = rs.getBlob("foto");
                InputStream is = blob.getBinaryStream();
                Image imagem = ImageIO.read(is);
                ImageIcon foto = new ImageIcon(imagem);
                usuarios.add(new Usuario(id, usuario, foto));
            }
        }
        return usuarios;
    }
    
    public ArrayList<Grupo> getListaGrupos() throws SQLException, IOException{
        Statement st = conexao().createStatement();
        String SQL = "SELECT * FROM grupo ORDER BY id";
        ResultSet rs = st.executeQuery(SQL);
        ArrayList<Grupo> grupos = new ArrayList<>();
        while(rs.next()){
            int id = rs.getInt("id");
            if(id != 0){
                String nome = rs.getString("nomeGrupo");
                int[] membros = new int[10];
                for (int i = 0; i < 10; i++) {
                    membros[i] = rs.getInt("idMembro" + (i + 1));
                }
                Blob blob = rs.getBlob("foto");
                InputStream is = blob.getBinaryStream();
                Image imagem = ImageIO.read(is);
                ImageIcon foto = new ImageIcon(imagem);
                grupos.add(new Grupo(id, nome, membros, foto));
            }
        }
        return grupos;
    }
    
    public ArrayList<Mensagem> getListaMensagens(int idOrigem, int idDestino, char tipoDestino) throws SQLException, IOException{
        ArrayList<Mensagem> mensagens = new ArrayList<>();
        MensagemBuilder mensagemBuilder = new MensagemBuilder(0, 0, 'U');
        PreparedStatement ps;
        if(tipoDestino == 'U'){
            ps = conexao().prepareStatement("SELECT * FROM mensagem WHERE (idUsuarioOrigem = ? AND idUsuarioDestino = ?) OR (idUsuarioOrigem = ? AND idUsuarioDestino = ?) AND destinoTipo = 'U' ORDER BY idMensagem");
            ps.setInt(1, idOrigem);
            ps.setInt(2, idDestino);
            ps.setInt(3, idDestino);
            ps.setInt(4, idOrigem);    
        }else{
            ps = conexao().prepareStatement("SELECT * FROM mensagem WHERE idGrupoDestino = ? AND destinoTipo = 'G' ORDER BY idMensagem");
            ps.setInt(1, idDestino);
        }
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            Mensagem mensagem = mensagemBuilder.criarMensagem(rs.getInt("idMensagem"), rs.getString("tipoMens").charAt(0), null);
            mensagem.setIdOrigem(rs.getInt("idUsuarioOrigem"));
            switch(mensagem.getDestinoTipo()){
                case 'U':
                    mensagem.setIdDestino(rs.getInt("idUsuarioDestino"));
                    mensagem.setDestinoTipo('U');
                    break;
                case 'G':
                    mensagem.setIdDestino(rs.getInt("idGrupoDestino"));
                    mensagem.setDestinoTipo('G');
                    break;
            }
            switch(mensagem.getTipoMensagem()){
                case 'T':
                    mensagem.setMensagem(rs.getString("txtMensagem"));
                    break;
                case 'I':
                    Blob blob = rs.getBlob("arquivo");
                    InputStream is = blob.getBinaryStream();
                    Image imagem = ImageIO.read(is);
                    mensagem.setMensagem(new ImageIcon(imagem));
                    break;
                case 'A':
                    // falta implementar para arquivo
                    break;
            }
            mensagem.setDataMensagem(new Date(rs.getTimestamp("timeMensagem").getTime()));
            mensagens.add(mensagem);
        }
        return mensagens;
    }
    
    public boolean enviarMensagem(Mensagem mensagem) throws SQLException, IOException {
        PreparedStatement ps = conexao().prepareStatement("INSERT INTO mensagem (idUsuarioOrigem, idUsuarioDestino, destinoTipo, txtMensagem, timeMensagem, tipoMens, idMensagem, idGrupoDestino, arquivo) VALUES ("
                + "?, ?, ?, ?, ?, ?, ?, ?, ?)");
        ps.setInt(1, mensagem.getIdOrigem());
        if(mensagem.getDestinoTipo() == 'U')
            ps.setInt(2, mensagem.getIdDestino());
        else
            ps.setInt(2, 0);
        ps.setString(3, Character.toString(mensagem.getDestinoTipo()));
        ps.setTimestamp(5, new Timestamp(mensagem.getDataMensagem().getTime()));
        ps.setString(6, Character.toString(mensagem.getTipoMensagem()));
        ps.setInt(7, mensagem.getIdMensagem());
        if(mensagem.getDestinoTipo() == 'G')
            ps.setInt(8, mensagem.getIdDestino());
        else
            ps.setInt(8, 0);
        switch(mensagem.getTipoMensagem()){
            case 'I':
                ImageIcon imagem = (ImageIcon)mensagem.getMensagem();
                ps.setBlob(9, compartilhado.aplicacao.Imagem.imagemParaBlob(imagem.getImage()));
                ps.setString(4, "");
                break;
            case 'A':
                // implementar conversão de arquivo para blob
                break;
            case 'T':
                ps.setString(4, (String)mensagem.getMensagem());
                ps.setNull(9, java.sql.Types.BLOB);
                break;
        }
        int result = ps.executeUpdate();
        return result == 1;
    }
}
