<?php
include('protect.php');
?>
<!doctype html>
<html lang="pt-br">

<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>SMARTLAB</title>
  <link href="css/bootstrap.min.css" rel="stylesheet">
  <link href="home.css" rel="stylesheet">
</head>

<body>

  <!-- Inicia sessão -->
  <?php $_SESSION['nome']; ?>
  <header>
    <div class="container" id="nav-container">
      <?php
      include('navbar.php');
      ?>
    </div>
  </header>
  <main>
    <div class="container-fluid">
      <div class="container mt-4">
        <div class="row">
          <div class="col mt-12">
            <div class="card">
              <?php
              switch (@$_REQUEST["page"]) {
                case "horario":
                  include('horario.php');
                  break;
                case "horario-listar":
                  include('horario-listar.php');
                  break;
                case "ambiente-listar":
                  include('ambiente-listar.php');
                  break;
                case "agenda":
                  include('agenda.php');
                  break;
                case "reserva":  // Adicionando o caso para a página de inserção de reserva teste
                  include('reserva.php');  // Nome do arquivo PHP da página de reservaa 
                  break;
                case "perifericos-listar":
                  include('periferico-listar.php');
                  break;
                case "periferico":
                  include('periferico.php');
                  break;
                case "finalizar":
                  include('finalizar.php');
                  break;
                case "reserva-listar":
                  include('reserva.php');
                  break;
                case "perfil":
                  include('perfil.php');
                  break;
                default:
                  include('portfolio.html');
              }
              ?>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
  <!-- Rodapé -->
  <footer>
    <div class="container-fluid">
      <div class="container mt-4">
        <div class="row">
          <div class="col mt-12">
            <div class="card">
              <p>
                Você pode entrar em contato comigo através do meu e-mail: <strong>smartlabprojeto@gmail.com</strong>
              </p>
              <p>
                Desenvolvido por Albert, Alysson e Guilherme &copy; 2024
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </footer>

  <script src="js/bootstrap.bundle.min.js"></script>
</body>

</html>