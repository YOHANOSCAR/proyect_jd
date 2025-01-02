    package com.jennyduarte.sis.service;
    
    import com.jennyduarte.sis.entity.Usuario;
    import com.jennyduarte.sis.repository.UsuarioRepository;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.User;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    
    import java.util.Collections;
    import java.util.List;
    
    @Service
    public class UsuarioService implements UserDetailsService {
    
        private final UsuarioRepository usuarioRepository;
        private final PasswordEncoder passwordEncoder;
    
        public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
            this.usuarioRepository = usuarioRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

            if (usuario.getEstado() != Usuario.EstadoUsuario.ACTIVO) {
                throw new UsernameNotFoundException("El usuario está inactivo: " + username);
            }
            return User.builder()
                    .username(usuario.getUsername())
                    .password(usuario.getPassword()) // Contraseña encriptada
                    .roles(usuario.getRol().name()) // Se pasa el rol con el prefijo
                    .build();
        }

        public List<Usuario> listarTodos() {
            return usuarioRepository.findAll();
        }
    
        public Usuario obtenerPorId(Long id) {
            return usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado."));
        }

        public Usuario guardarUsuario(Usuario usuario) {

            Usuario existente = usuarioRepository.findByContactoId(usuario.getContacto().getId()).orElse(null);
            if (existente != null) {
                throw new IllegalArgumentException("El contacto ya está asociado a otro usuario.");
            }

            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            return usuarioRepository.save(usuario);
        }
        public Usuario actualizarUsuario(Usuario usuario) {
            Usuario existente = usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("El usuario con ID " + usuario.getId() + " no existe."));

            // Validar que el contacto no esté asociado a otro usuario
            Usuario asociado = usuarioRepository.findByContactoId(usuario.getContacto().getId()).orElse(null);
            if (asociado != null && !asociado.getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("El contacto ya está asociado a otro usuario.");
            }

            existente.setContacto(usuario.getContacto());
            existente.setUsername(usuario.getUsername());
            existente.setRol(usuario.getRol());
            existente.setEstado(usuario.getEstado());

            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                existente.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }

            return usuarioRepository.save(existente);
        }

        public void eliminarUsuario(Long id) {
            if (!usuarioRepository.existsById(id)) {
                throw new RuntimeException("Usuario con ID " + id + " no encontrado.");
            }
            usuarioRepository.deleteById(id);
        }
    
        public Usuario obtenerPorUsername(String username) {
            return usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con username: " + username));
        }

        public Usuario obtenerUsuarioAutenticado() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new IllegalStateException("No hay un usuario autenticado.");
            }
    
            String username = authentication.getName();
            return obtenerPorUsername(username);
        }
    
    }
