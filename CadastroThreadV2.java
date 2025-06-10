package cadastroserver;

import controller.*;
import model.*;
import java.net.Socket;
import java.io.*;
import java.util.List;

public class CadastroThreadV2 extends Thread {
    private ProdutoJpaController ctrlProd;
    private UsuarioJpaController ctrlUsu;
    private MovimentoJpaController ctrlMov;
    private PessoaJpaController ctrlPessoa;
    private Socket s1;

    private Usuario usuarioLogado;

    public CadastroThreadV2(ProdutoJpaController ctrlProd,
                            UsuarioJpaController ctrlUsu,
                            MovimentoJpaController ctrlMov,
                            PessoaJpaController ctrlPessoa,
                            Socket s1) {
        this.ctrlProd = ctrlProd;
        this.ctrlUsu = ctrlUsu;
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
        this.s1 = s1;
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(s1.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s1.getInputStream())) {

            // Recebe login e senha
            String login = (String) in.readObject();
            String senha = (String) in.readObject();

            usuarioLogado = ctrlUsu.findUsuario(login, senha);
            if (usuarioLogado == null) {
                // Usuário inválido, fecha conexão
                s1.close();
                return;
            }

            // Loop principal de comandos
            while (true) {
                String comando = (String) in.readObject();

                if ("L".equalsIgnoreCase(comando)) {
                    // Enviar lista de produtos
                    List<Produto> produtos = ctrlProd.findProdutoEntities();
                    out.writeObject(produtos);
                    out.flush();

                } else if ("E".equalsIgnoreCase(comando) || "S".equalsIgnoreCase(comando)) {
                    // Movimento de Entrada (E) ou Saída (S)

                    // Recebe e configura Movimento
                    Movimento mov = new Movimento();
                    mov.setUsuario(usuarioLogado);
                    mov.setTipo(comando);

                    // Recebe Id Pessoa e atribui
                    Integer idPessoa = (Integer) in.readObject();
                    Pessoa pessoa = ctrlPessoa.findPessoa(idPessoa);
                    if (pessoa == null) {
                        out.writeObject("Pessoa não encontrada!");
                        out.flush();
                        continue; // Pula iteração
                    }
                    mov.setPessoa(pessoa);

                    // Recebe Id Produto e atribui
                    Integer idProduto = (Integer) in.readObject();
                    Produto produto = ctrlProd.findProduto(idProduto);
                    if (produto == null) {
                        out.writeObject("Produto não encontrado!");
                        out.flush();
                        continue;
                    }
                    mov.setProduto(produto);

                    // Recebe quantidade e valor unitário
                    Integer quantidade = (Integer) in.readObject();
                    Double valorUnitario = (Double) in.readObject();
                    mov.setQuantidade(quantidade);
                    mov.setValorUnitario(valorUnitario);

                    // Persiste o movimento
                    ctrlMov.create(mov);

                    // Atualiza quantidade de produto
                    int qtAtual = produto.getQuantidade();
                    if ("E".equalsIgnoreCase(comando)) {
                        produto.setQuantidade(qtAtual + quantidade);
                    } else if ("S".equalsIgnoreCase(comando)) {
                        produto.setQuantidade(qtAtual - quantidade);
                    }

                    ctrlProd.edit(produto);

                    out.writeObject("Movimento registrado com sucesso!");
                    out.flush();

                } else if ("X".equalsIgnoreCase(comando)) {
                    // Finaliza conexão
                    break;

                } else {
                    out.writeObject("Comando inválido!");
                    out.flush();
                }
            }
            
            s1.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
