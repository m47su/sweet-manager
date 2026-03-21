package br.com.confeitaria.sweet_manager.application.service;

import br.com.confeitaria.sweet_manager.domain.entity.Usuario;
import br.com.confeitaria.sweet_manager.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

   public Usuario cadastrar(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setRole("USER"); 
        return usuarioRepository.save(usuario);
    }

    public Usuario cadastrarAdmin(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setRole("ADMIN"); 
        return usuarioRepository.save(usuario);
    }
}
