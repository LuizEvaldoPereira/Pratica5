package cadastroserver;

import controller.*;
import javax.persistence.*;
import java.net.ServerSocket;
import java.net.Socket;

public class CadastroServer {
    public static void main(String[] args) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroPU");

            ProdutoJpaController ctrlProd = new ProdutoJpaController(emf);
            UsuarioJpaController ctrlUsu = new UsuarioJpaController(emf);
            MovimentoJpaController ctrlMov = new MovimentoJpaController(emf);
            PessoaJpaController ctrlPessoa = new PessoaJpaController(emf);

            ServerSocket server = new ServerSocket(4321);
            System.out.println("Servidor aguardando conexões na porta 4321...");

            while (true) {
                Socket clienteSocket = server.accept();
                System.out.println("Cliente conectado: " + clienteSocket.getInetAddress());

                // Instancia nova Thread versão 2
                CadastroThreadV2 thread = new CadastroThreadV2(ctrlProd, ctrlUsu, ctrlMov, ctrlPessoa, clienteSocket);
                thread.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
