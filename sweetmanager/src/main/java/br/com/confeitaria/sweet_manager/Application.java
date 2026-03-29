package br.com.confeitaria.sweet_manager;

import br.com.confeitaria.sweet_manager.domain.entity.Usuario;
import br.com.confeitaria.sweet_manager.domain.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.findByEmail("admin@sweet").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNome("Admin");
                admin.setEmail("admin@sweet");
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                repository.save(admin);
                System.out.println("Usuário ADMIN criado!");
            }

            if (repository.findByEmail("teste@teste.com").isEmpty()) {
                Usuario user = new Usuario();
                user.setNome("Teste");
                user.setEmail("teste@teste.com");
                user.setSenha(passwordEncoder.encode("123"));
                user.setRole("USER");
                repository.save(user);
                System.out.println("Usuário COMUM criado!");
            }
        };
    }
}