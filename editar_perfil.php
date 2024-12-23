<?php
require_once 'conexao.php';
session_start();

if (!isset($_SESSION['id'])) {
    echo "<p>Você precisa estar logado para acessar esta página.</p>";
    exit;
}

$userId = $_SESSION['id'];

// Mensagem de feedback
$message = "";
$redirectMessage = "";

// Funções para atualizar os dados do usuário
function updateUserData($userId, $nome, $email, $conn)
{
    $query = $conn->prepare("UPDATE usuarios SET Nome = ?, Email = ? WHERE idUsuarios = ?");
    $query->bind_param("ssi", $nome, $email, $userId);
    return $query->execute();
}

function updateUserPassword($userId, $senha, $conn)
{
    $senhaHash = md5($senha); // Hash da nova senha com MD5
    $query = $conn->prepare("UPDATE usuarios SET Senha = ? WHERE idUsuarios = ?");
    $query->bind_param("si", $senhaHash, $userId);
    return $query->execute();
}

function deleteUserAccount($userId, $conn)
{
    // Primeiro, exclui as reservas do usuário na tabela reserva
    $delete_reservas_sql = "DELETE FROM reserva WHERE fk_id_usuario = ?";
    $stmt_delete_reservas = $conn->prepare($delete_reservas_sql);
    $stmt_delete_reservas->bind_param("i", $userId);
    $stmt_delete_reservas->execute();

    // Se houver alguma dependência em outras tabelas, remova também
    $delete_historico_sql = "DELETE FROM historicoreservas WHERE idReserva IN (SELECT idReserva FROM reserva WHERE fk_id_usuario = ?)";
    $stmt_delete_historico = $conn->prepare($delete_historico_sql);
    $stmt_delete_historico->bind_param("i", $userId);
    $stmt_delete_historico->execute();

    // Agora, exclua o usuário
    $query = $conn->prepare("DELETE FROM usuarios WHERE idUsuarios = ?");
    $query->bind_param("i", $userId);
    return $query->execute();
}

// Processar o formulário
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (isset($_POST['update_data'])) {
        $nome = $_POST['nome'];
        $email = $_POST['email'];

        if (updateUserData($userId, $nome, $email, $conn)) {
            $_SESSION['message'] = "<div class='alert success'>Dados atualizados com sucesso!</div>";
            $_SESSION['redirect_message'] = "<span class='redirect-message'>A página será recarregada.</span>";
            echo '<script>setTimeout(function() { window.location.href = "editar_perfil.php"; }, 4000);</script>';
        } else {
            $_SESSION['message'] = "<div class='alert error'>Erro ao atualizar dados.</div>";
        }
    } elseif (isset($_POST['update_password'])) {
        $senhaAtual = $_POST['senha_atual'] ?? '';
        $senhaNova = $_POST['senha_nova'] ?? '';

        // Verifica se a nova senha atende aos requisitos
        if (!preg_match('/^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[a-zA-Z\d!@#$%^&*]{8,}$/', $senhaNova)) {
            $_SESSION['message'] = "<div class='alert error'>A nova senha deve atender aos requisitos.</div>";
        } else {
            // Verifica a senha atual
            $sql_code = "SELECT Senha FROM usuarios WHERE idUsuarios = ?";
            $stmt = $conn->prepare($sql_code);
            $stmt->bind_param("i", $userId);
            $stmt->execute();
            $result = $stmt->get_result();
            $usuario = $result->fetch_assoc();

            // Verifica se o usuário foi encontrado e se a senha atual está correta
            if ($usuario && $usuario['Senha'] === md5($senhaAtual)) { // Comparação com MD5
                if (updateUserPassword($userId, $senhaNova, $conn)) {
                    $_SESSION['message'] = "<div class='alert success'>Senha atualizada com sucesso!</div>";
                    $_SESSION['redirect_message'] = "<span class='redirect-message'>A página será recarregada.</span>";
                    echo '<script>setTimeout(function() { window.location.href = "editar_perfil.php"; }, 4000);</script>';
                } else {
                    $_SESSION['message'] = "<div class='alert error'>Erro ao atualizar senha.</div>";
                }
            } else {
                $_SESSION['message'] = "<div class='alert error'>Senha atual incorreta.</div>";
            }
        }
    } elseif (isset($_POST['delete_account'])) {
        if (deleteUserAccount($userId, $conn)) {
            session_destroy();
            header("Location: login.php");
            exit;
        } else {
            $_SESSION['message'] = "<div class='alert error'>Erro ao excluir conta.</div>";
        }
    }
}

// Obtém os dados do usuário novamente após uma possível atualização
$sql_code = "SELECT Nome, Email FROM usuarios WHERE idUsuarios = ?";
$stmt = $conn->prepare($sql_code);
$stmt->bind_param("i", $userId);
$stmt->execute();
$result = $stmt->get_result();
$usuario = $result->fetch_assoc();

// Verifica se há mensagens na sessão
if (isset($_SESSION['message'])) {
    $message = $_SESSION['message'];
    unset($_SESSION['message']); // Limpa a mensagem após exibição
}
if (isset($_SESSION['redirect_message'])) {
    $redirectMessage = $_SESSION['redirect_message'];
    unset($_SESSION['redirect_message']); // Limpa a mensagem após exibição
}
?>

<!DOCTYPE html>
<html lang="pt-br">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="home.css" rel="stylesheet">
    <title>Editar Perfil</title>
   
    <script>
        function validatePassword() {
            const passwordInput = document.querySelector('#senha_nova');
            const requirements = document.getElementById('password-requirements');

            const minLength = passwordInput.value.length >= 8;
            const hasLowerCase = /[a-z]/.test(passwordInput.value);
            const hasUpperCase = /[A-Z]/.test(passwordInput.value);
            const hasSpecialChar = /[!@#$%^&*]/.test(passwordInput.value);

            const allValid = minLength && hasLowerCase && hasUpperCase && hasSpecialChar;

            requirements.innerHTML = `
                <div class="${minLength ? 'valid' : 'invalid'}">Mínimo de 8 caracteres</div>
                <div class="${hasLowerCase ? 'valid' : 'invalid'}">Pelo menos 1 letra minúscula</div>
                <div class="${hasUpperCase ? 'valid' : 'invalid'}">Pelo menos 1 letra maiúscula</div>
                <div class="${hasSpecialChar ? 'valid' : 'invalid'}">Pelo menos 1 caractere especial</div>
            `;
        }
    </script>
</head>

<body>
    <div class="container-alterar">
        <h1>Editar Perfil</h1>

        <?php echo $message; ?>
        <?php echo $redirectMessage; ?>

        <form method="POST">
            <label for="nome">Nome</label>
            <input type="text" id="nome" name="nome" value="<?php echo $usuario['Nome']; ?>" required>

            <label for="email">Email</label>
            <input type="email" id="email" name="email" value="<?php echo $usuario['Email']; ?>" required>

            <button type="submit" name="update_data">Atualizar Dados</button>
        </form>

        <form method="POST">
            <label for="senha_atual">Senha Atual</label>
            <input type="password" id="senha_atual" name="senha_atual" required>

            <label for="senha_nova">Nova Senha</label>
            <input type="password" id="senha_nova" name="senha_nova" required oninput="validatePassword()">

            <div id="password-requirements" class="password-requirements"></div>

            <button type="submit" name="update_password">Atualizar Senha</button>
        </form>

        <form method="POST">
            <button type="submit" name="delete_account" onclick="return confirm('Tem certeza que deseja excluir sua conta?')">Excluir Conta</button>
        </form>

        <a href="home.php?page=perfil"><button class="back-button">Voltar</button></a>
    </div>
</body>
</html>
