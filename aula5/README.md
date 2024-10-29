# Autenticação e Autorização utilizando redes sociais no lado cliente (Google).

Os conteúdos das aplicações cliente, servidor e admin estão nas respectivas pastas, descritos no arquivo README.md de cada uma.

Em relação aos projetos da pasta aula4 foram alteradas as aplicações **client** e **server**, sendo realizadas alterações no processo de autenticação de usuários de cada projeto.

## Autenticação utilizando o Google

Nesses projetos será realizada autenticação no lado cliente da aplicação, ou seja, no cliente desenvolvido com React.js. O primeiro passo para o processo de autenticação com o Google funcionar é realizar o cadastro no Google Cloud Console e depois realizar o cadastro de um projeto. Para isso basta acessar:

- [https://console.cloud.google.com/](https://console.cloud.google.com/) e ativar a conta, seguindo os passos 1 e 2 presentes no link abaixo:
- [https://cloud.google.com/apigee/docs/hybrid/v1.8/precog-gcpaccount?hl=pt-br](https://cloud.google.com/apigee/docs/hybrid/v1.8/precog-gcpaccount?hl=pt-br) Seguir apenas os passos 1 e 2.
- https://developers.google.com/identity/sign-in/web/sign-in?hl=pt-br Após concluir os passos, seguir esse link para criar um novas credenciais para autenticação.

Seguindo > **+ Criar Credenciais** > Criar ID do cliente do OAuth

Selecionar **Aplicativo da Web**

Nome: Minha aplicação (Pode escolher qualquer nome)

Origens JavaScript autorizadas
- http://localhost:5173

URIs de redirecionamento autorizados
- http://localhost:5173
- http://localhost:8080/oauth2/callback/google
- http://localhost:8080

Após preenchidos os dados, salvar, será gerado um ClientID e um Secret.