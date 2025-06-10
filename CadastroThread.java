package cadastroserver;

import controller.*;
import model.Usuario;
import model.Produto;
import java.net.Socket;
import java.io.*;
import java.util.List;

public class CadastroThread extends Thread {
    private ProdutoJpaController ctrl;
    private UsuarioJpaController ctrlUsu;
    private Socket s1;
    
    public CadastroThread(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.s1 = s1;
    }
    
    @Override
    public void run() {
        try(ObjectOutputStream out = new ObjectOutputStream(s1.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s1.getInputStream())) {
            
            // Recebe login e senha
            String login = (String) in.readObject();
            String senha = (String) in.readObject();
            
            // Valida usuário
            Usuario usuario = ctrlUsu.findUsuario(login, senha);
            if (usuario == null) {
                s1.close();
                return;
            }
            
            // Ciclo de resposta
            while (true) {
                String comando = (String) in.readObject();
                
                if ("L".equalsIgnoreCase(comando)) {
                    List<Produto> produtos = ctrl.findProdutoEntities();
                    out.writeObject(produtos);
                    out.flush();
                } else {
                    break; // Sai do loop em outros casos (pode ajustar conforme necessidade)
                }
            }
            
            s1.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
