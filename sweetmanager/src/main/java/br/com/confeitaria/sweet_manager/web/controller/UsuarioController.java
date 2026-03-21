// Local: sweetmanager/src/main/java/br/com/confeitaria/sweet_manager/web/controller/UsuarioController.java

package br.com.confeitaria.sweet_manager.web.controller;

import br.com.confeitaria.sweet_manager.application.service.UsuarioService;
import br.com.confeitaria.sweet_manager.domain.entity.Usuario;
import br.com.confeitaria.sweet_manager.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/cadastrar")
    public Usuario cadastrar(@RequestBody Usuario usuario) {
        return usuarioService.cadastrar(usuario);
    }

    @PostMapping("/cadastrar-admin")
    public Usuario cadastrarAdmin(@RequestBody Usuario usuario) {
        return usuarioService.cadastrarAdmin(usuario);
    }

    @GetMapping("/me")
    public Map<String, String> obterUsuarioLogado(Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElseThrow();
        return Map.of("nome", usuario.getNome(), "role", usuario.getRole());
    }
}