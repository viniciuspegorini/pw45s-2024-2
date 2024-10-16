package br.edu.utfpr.pb.pw26s.server.service;

import br.edu.utfpr.pb.pw26s.server.model.Authority;
import br.edu.utfpr.pb.pw26s.server.model.User;
import br.edu.utfpr.pb.pw26s.server.repository.AuthorityRepository;
import br.edu.utfpr.pb.pw26s.server.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    public UserService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        passwordEncoder = new BCryptPasswordEncoder();
        this.authorityRepository = authorityRepository;
    }

    public User save(User user) {

        user.setPassword( passwordEncoder.encode(user.getPassword()));

        Set<Authority> authorities = new HashSet<>();
        authorities.add(authorityRepository.findByAuthority("ROLE_USER"));
        user.setUserAuthorities(authorities);

        return userRepository.save(user);
    }

}
