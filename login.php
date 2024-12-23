<?php
include('conexao.php');
session_start();

$errorMessage = ""; // Inicializa a mensagem de erro como vazia

if (isset($_POST['email']) || isset($_POST['senha'])) {
    if (strlen($_POST['email']) == 0) {
        $errorMessage = "Preencha seu e-mail";
    } else if (strlen($_POST['senha']) == 0) {
        $errorMessage = "Preencha sua senha";
    } else {
        // Limpa e escapa as entradas
        $email = $conn->real_escape_string(trim($_POST['email']));
        $senha = $conn->real_escape_string(trim($_POST['senha']));

        // Aplica MD5 na senha inserida
        $senhaHash = md5($senha);

        // Busca o usuário pelo e-mail
        $sql_code = "SELECT * FROM usuarios WHERE Email = '$email'";
        $sql_query = $conn->query($sql_code) or die("Falha na execução do código SQL: " . $conn->error);

        if ($sql_query->num_rows == 1) {
            $usuarios = $sql_query->fetch_assoc();

            // Verifica a senha
            if ($usuarios['Senha'] === $senhaHash) {
                // A senha está correta
                $_SESSION['id'] = $usuarios['idUsuarios'];
                $_SESSION['nome'] = $usuarios['Nome'];
                $_SESSION['email'] = $usuarios['Email'];
                header("Location: home.php"); // Redireciona para a página inicial
                exit();
            } else {
                $errorMessage = "Falha ao logar! E-mail ou senha incorretos.";
            }
        } else {
            $errorMessage = "Falha ao logar! E-mail ou senha incorretos.";
        }
    }
}
?>

<!DOCTYPE html>
<html lang="pt-br">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=SUSE:wght@100..800&display=swap');

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            display: flex;
            flex-direction: column;
            align-items: center;
            font-family: "SUSE", sans-serif;
            background: rgb(95, 91, 164);
            background: linear-gradient(90deg, rgba(95, 91, 164, 1) 0%, rgba(0, 212, 255, 1) 100%);
        }

        h1 {
            margin-top: 30px;
        }

        form {
            background: rgb(160, 197, 255);
            background: linear-gradient(90deg, rgba(160, 197, 255, 1) 0%, rgba(0, 212, 255, 1) 100%);
            width: 400px;
            height: 400px;
            border-radius: 15px;
            margin-top: 20px;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        form p {
            margin-top: 20px;
        }

        form label {
            font-size: 1.2rem;
        }

        form input {
            padding: 10px;
            width: 300px;
            height: 30px;
            margin-bottom: 15px;
            margin-top: 10px;
            border-radius: 10px;
            border: 1px solid darkcyan;
        }

        form button {
            width: 150px;
            height: 30px;
            border: none;
            border-radius: 15px;
            background-color: darkblue;
            color: white;
            font-weight: bold;
            cursor: pointer;
        }

        .register-link {
            margin-top: 20px;
        }

        .error-message {
            color: red;
            font-size: 0.9rem;
            margin-top: 10px;
            font-weight: bold;
        }
    </style>
</head>

<body>
    <h1>SMARTLAB</h1>
    <form action="" method="POST">
        <p>
            <label>E-mail:</label>
            <br>
            <input type="text" name="email" required>
        </p>
        <p>
            <label>Senha:</label>
            <br>
            <input type="password" name="senha" required>
        </p>
        <p>
            <button type="submit">Entrar</button>
        </p>
        <p class="register-link">
            <a href="cadastro.php">Criar conta</a>
        </p>

        <!-- Exibe a mensagem de erro, se existir -->
        <?php if ($errorMessage): ?>
            <span class="error-message" id="errorMessage"><?php echo $errorMessage; ?></span>
        <?php endif; ?>
    </form>

    <script>
        // Oculta a mensagem de erro após 3 segundos (3000 ms)
        window.addEventListener('load', function () {
            const errorMessage = document.getElementById('errorMessage');
            if (errorMessage) {
                setTimeout(() => {
                    errorMessage.style.display = 'none';
                }, 2000);
            }
        });
    </script>
</body>

</html>
