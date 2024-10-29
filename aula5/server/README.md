

# Spring Framework (back-end)

O conteúdo do projeto base é o mesmo do projeto **server** da aula4. Para autenticação com redes sociais foram realizadas as seguintes modificações:
- **POM.XML**: foram adicionadas as dependências do Google para validação do TOKEN de autenticação.
- **AuthProvider**: ENUM com o provedor utilizado no cadastro. Local se o usuário foi cadastrado localmente ou google, facebook, etc. caso tenha se cadastrado a partir de uma autenticação de rede social.
- **User**: Foi adicionado o atributo provider.
- **AuthController**: Controller utilizado na autenticação via redes sociais.
- **GoogleTokenVerifier**: Classe utilizada para verificar o token recebido pelo usuário no lado cliente da aplicação.

## Atualização das dependências

```xml
<!-- ... -->  
<dependencies>
	<!-- ... -->   
	<!-- Google Authentication -->  
	<dependency>  
	 <groupId>com.google.api-client</groupId>  
	 <artifactId>google-api-client</artifactId>  
	 <version>2.0.0</version>  
	</dependency>  
	<dependency>  
	 <groupId>com.google.oauth-client</groupId>  
	 <artifactId>google-oauth-client</artifactId>  
	 <version>1.34.1</version>  
	</dependency>
</dependencies> 
<!-- ... -->  	
```  
## Atualizações na camada de segurança

### Enum AuthProvider

O Enum AuthProvider é o provedor que será utilizado no cadastro de usuário para identificar a origem do cadastro. Irá receber o valor *local* se o usuário foi cadastrado localmente ou *google*, *facebook*, etc. caso tenha se cadastrado a partir de uma autenticação de rede social.

```java  
public enum AuthProvider {  
	local, 
	facebook, 
	google, 
	github
}  
```  

### Ajustes na classe User

Para identificar a origem do cadastro do usuário foi criado um atributo chamado *provider* na classe User, em que o tipo desse atributo é o Enum AuthProvider.


```java  
//...  
public class User implements UserDetails {  
	//... 
	@NotNull
	@Enumerated(EnumType.STRING)
	
	private AuthProvider provider;  
	//...
}  
```  

### A classe AuthController

A classe **AuthController** será utilizada quando um usuário fizer autenticação via rede social, no exemplo do projeto utilizando o método de autenticação do Google. A aplicação cliente irá fazer uma requisição do tipo **HTTP POST** para a URL da API [/auth-social](/auth-social) adicionando no cabeçalho da requisição o TOKEN recebido após autenticação com a API do Google por meio da variável **Auth-Id-Token**.

No *controller* o TOKEN recebido será validado por meio do método *verify()* da classe **GoogleTokenVerifier**, caso o TOKEN seja válido o *username* será extraído do TOKEN. Com o *username* será realizada  uma consulta no banco de dados, caso um cadastro com o *username* não exista será criado um novo cadastro com a permissão **ROLE_USER** e autenticado o usuário, e caso o usuário exista ele será autenticado.

```java  
package br.edu.utfpr.pb.pw26s.server.security.social;  
  
import br.edu.utfpr.pb.pw26s.server.model.AuthProvider;  
import br.edu.utfpr.pb.pw26s.server.model.User;  
import br.edu.utfpr.pb.pw26s.server.repository.AuthorityRepository;  
import br.edu.utfpr.pb.pw26s.server.repository.UserRepository;  
import br.edu.utfpr.pb.pw26s.server.security.SecurityConstants;  
import br.edu.utfpr.pb.pw26s.server.security.dto.AuthenticationResponse;  
import br.edu.utfpr.pb.pw26s.server.security.dto.UserResponseDTO;  
import br.edu.utfpr.pb.pw26s.server.service.UserService;  
import com.auth0.jwt.JWT;  
import com.auth0.jwt.algorithms.Algorithm;  
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;  
import lombok.extern.slf4j.Slf4j;  
import org.springframework.http.HttpStatus;  
import org.springframework.http.ResponseEntity;  
import org.springframework.web.bind.annotation.PostMapping;  
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RestController;  
  
import jakarta.servlet.http.HttpServletRequest;  
import jakarta.servlet.http.HttpServletResponse;  
import java.util.Date;  
import java.util.HashSet;  
  
@RestController  
@RequestMapping("auth-social")  
@Slf4j  
public class AuthController {  
  
    private final GoogleTokenVerifier googleTokenVerifier;  
    private final UserService userService;  
    private final UserRepository userRepository;  
    private final AuthorityRepository authorityRepository;  
  
    public AuthController(GoogleTokenVerifier googleTokenVerifier,  
                          UserService userService,  
                          UserRepository userRepository,  
                          AuthorityRepository authorityRepository) {  
        this.googleTokenVerifier = googleTokenVerifier;  
        this.userService = userService;  
        this.userRepository = userRepository;  
        this.authorityRepository = authorityRepository;  
    }  
  
    @PostMapping  
	public ResponseEntity<AuthenticationResponse> auth(HttpServletRequest request, HttpServletResponse response) {  
        String idToken = request.getHeader("Auth-Id-Token");  
        if (idToken != null) {  
            final Payload payload;  
            try {  
                payload = googleTokenVerifier.verify(idToken.replace(SecurityConstants.TOKEN_PREFIX, "") );  
                if (payload != null) {  
                    String username = payload.getEmail();  
  
                    User user = userRepository.findByUsername(username);  
                    if (user == null) {  
                        user = new User();  
                        user.setUsername(username);  
                        user.setDisplayName( (String) payload.get("name"));  
                        user.setPassword("P4ssword");  
                        user.setProvider(AuthProvider.google);  
                        user.setUserAuthorities( new HashSet<>() );  
                        user.getUserAuthorities().add(  
                                authorityRepository.findByAuthority("ROLE_USER")  
                        );  
                        userService.save(user);  
                    }  
                    String token = JWT.create()  
                            .withSubject( username  )  
                            .withExpiresAt(  
                                    new Date(System.currentTimeMillis() +  
                                    SecurityConstants.EXPIRATION_TIME  
  ))  
                            .sign(Algorithm.HMAC512(SecurityConstants.SECRET));  
  
                    return ResponseEntity.ok(  
                            new AuthenticationResponse(token, new UserResponseDTO(user)));  
  
                }  
            } catch (Exception e) {  
                log.error(e.getMessage());  
            }  
        }  
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  
    }  
}
```


### A classe GoogleTokenVerifier

A classe GoogleTokenVerifier possui o método *verifyToken* que ao ser chamado valida o TOKEN gerado no lado cliente na própria API do Google. Caso o TOKEN seja válido, retorna um objeto do tipo Payload, que contém algumas informações do usuário autenticado, como nome, email foto, entre outras.

Assim como na aplicação cliente quando foi realizada a autenticação, no lado servidor o *CLIENT_ID* gerado no console do Google Cloud será utilizado. Mas agora ele será utilizado ao validar o TOKEN, por isso é importante manter o mesmo *CLIENT_ID* do processo de autenticação.

```java
package br.edu.utfpr.pb.pw26s.server.security.social;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;

@Component
@Slf4j
public class GoogleTokenVerifier {

    private static final HttpTransport transport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private static final String CLIENT_ID = "310109923674-la5thl4s4t0b2ajp6acdhq7tra74dn31.apps.googleusercontent.com";

    public Payload verify(String idTokenString) throws GeneralSecurityException, IOException {
        return GoogleTokenVerifier.verifyToken(idTokenString);
    }

    private static Payload verifyToken(String idTokenString) throws GeneralSecurityException, IOException {
        final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                .Builder(transport, jsonFactory)
                .setIssuers(Arrays.asList("https://accounts.google.com",
                        "accounts.google.com"))
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
        log.info("Validating token: {}", idTokenString);
        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (idToken == null) {
            throw new RuntimeException("Google idToken is invalid.");
        }
        return idToken.getPayload();
    }
}
```