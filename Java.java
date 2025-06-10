public Usuario findUsuario(String login, String senha) {
    try {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.login = :login AND u.senha = :senha", Usuario.class)
                 .setParameter("login", login)
                 .setParameter("senha", senha)
                 .getSingleResult();
    } catch (NoResultException e) {
        return null;
    }
}
