<?php
include('conexao.php');
session_start();

$errorMessage = ""; // Variável para armazenar mensagem de erro

if (isset($_POST['nome'], $_POST['email'], $_POST['telefone'], $_POST['senha'])) {
    // Verifica se os campos estão preenchidos
    if (strlen($_POST['nome']) == 0) {
        $errorMessage = "Preencha seu nome";
    } else if (strlen($_POST['email']) == 0) {
        $errorMessage = "Preencha seu e-mail";
    } else if (strlen($_POST['telefone']) == 0) {
        $errorMessage = "Preencha seu telefone";
    } else if (strlen($_POST['senha']) == 0) {
        $errorMessage = "Preencha sua senha";
    } else {
        // Requisitos de senha
        $senha = $_POST['senha'];
        $minLength = strlen($senha) >= 8;
        $hasLowerCase = preg_match('/[a-z]/', $senha);
        $hasUpperCase = preg_match('/[A-Z]/', $senha);
        $hasSpecialChar = preg_match('/[!@#$%^&*]/', $senha);

        if (!$minLength || !$hasLowerCase || !$hasUpperCase || !$hasSpecialChar) {
            $errorMessage = "A senha deve ter no mínimo 8 caracteres, incluindo uma letra maiúscula, uma letra minúscula e um caractere especial.";
        } else {
            $nome = $conn->real_escape_string($_POST['nome']);
            $email = $conn->real_escape_string($_POST['email']);
            $telefone = $conn->real_escape_string($_POST['telefone']);
            $senhaHash = md5($senha);

            // Verifica se o e-mail já está cadastrado
            $sql_code = "SELECT * FROM usuarios WHERE Email = '$email'";
            $sql_query = $conn->query($sql_code);

            if ($sql_query->num_rows > 0) {
                $errorMessage = "E-mail já cadastrado!";
            } else {
                // Insere o novo usuário no banco de dados
                $insert_code = "INSERT INTO usuarios (Nome, Email, Telefone, Senha) VALUES ('$nome', '$email', '$telefone', '$senhaHash')";
                if ($conn->query($insert_code)) {
                    header("Location: login.php"); // Redireciona para a página de login
                    exit();
                } else {
                    $errorMessage = "Erro ao cadastrar: " . $conn->error;
                }
            }
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
    <title>Cadastrar</title>
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
            border-radius: 15px;
            margin-top: 20px;
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 20px;
        }

        form p {
            margin-top: 10px;
        }

        form label {
            font-size: 1.2rem;
        }

        form input {
            padding: 10px;
            width: 300px;
            margin-bottom: 15px;
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

        .login-link {
            margin-top: 20px;
        }

        .password-requirements {
            margin-top: 15px;
            width: 100%;
            background-color: #f9f9f9;
            border-radius: 10px;
            padding: 10px;
            border: 1px solid #ccc;
        }

        .valid {
            color: green;
        }

        .invalid {
            color: red;
        }

        .error-message {
            color: red;
            font-size: 0.9rem;
            font-weight: bold;
            margin-top: 10px;
        }
    </style>
    <script>
        function validatePassword() {
            const passwordInput = document.querySelector('input[name="senha"]');
            const passwordRequirements = document.getElementById('password-requirements');

            const minLength = passwordInput.value.length >= 8;
            const hasLowerCase = /[a-z]/.test(passwordInput.value);
            const hasUpperCase = /[A-Z]/.test(passwordInput.value);
            const hasSpecialChar = /[!@#$%^&*]/.test(passwordInput.value);

            passwordRequirements.innerHTML = `
                <p class="${minLength ? 'valid' : 'invalid'}">Mínimo 8 caracteres: ${minLength ? '✔️' : '❌'}</p>
                <p class="${hasLowerCase ? 'valid' : 'invalid'}">Pelo menos uma letra minúscula: ${hasLowerCase ? '✔️' : '❌'}</p>
                <p class="${hasUpperCase ? 'valid' : 'invalid'}">Pelo menos uma letra maiúscula: ${hasUpperCase ? '✔️' : '❌'}</p>
                <p class="${hasSpecialChar ? 'valid' : 'invalid'}">Pelo menos um caractere especial: ${hasSpecialChar ? '✔️' : '❌'}</p>
            `;
        }

        // Função para ocultar a mensagem de erro após 2 segundos
        function hideErrorMessage() {
            const errorMessage = document.getElementById('errorMessage');
            if (errorMessage) {
                setTimeout(() => {
                    errorMessage.style.display = 'none';
                }, 2000);
            }
        }
    </script>
</head>

<body onload="hideErrorMessage()">
    <h1>SMARTLAB - Cadastro</h1>
    <form action="" method="POST">
        <p>
            <label>Nome:</label>
            <br>
            <input type="text" name="nome" required>
        </p>
        <p>
            <label>E-mail:</label>
            <br>
            <input type="text" name="email" required>
        </p>
        <p>
            <label>Telefone:</label>
            <br>
            <input type="text" name="telefone" required>
        </p>
        <p>
            <label>Senha:</label>
            <br>
            <input type="password" name="senha" required oninput="validatePassword()">
        </p>
        <div id="password-requirements" class="password-requirements">
            <p>Mínimo 8 caracteres: ❌</p>
            <p>Pelo menos uma letra minúscula: ❌</p>
            <p>Pelo menos uma letra maiúscula: ❌</p>
            <p>Pelo menos um caractere especial: ❌</p>
        </div>

        <?php if ($errorMessage): ?>
            <span class="error-message" id="errorMessage"><?php echo $errorMessage; ?></span>
        <?php endif; ?>

        <p>
            <button type="submit">Cadastrar</button>
        </p>
        <p class="login-link">
            <a href="login.php">Já tem uma conta? Entre aqui</a>
        </p>
    </form>
</body>

</html>