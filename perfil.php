<?php
if (session_status() == PHP_SESSION_NONE) {
    session_start();
}
require_once 'conexao.php';

// Verifica se a sessão do usuário está definida
if (!isset($_SESSION['id']) || !isset($_SESSION['nome'])) {
    echo "<p>Você precisa estar logado para acessar esta página.</p>";
    exit; // Encerra a execução se o usuário não estiver logado
}


// Função para obter a imagem do usuário
function getUserImage($userId, $conn)
{
    $query = $conn->prepare("SELECT foto FROM usuarios WHERE idUsuarios = ?");
    $query->bind_param("i", $userId);
    $query->execute();
    $result = $query->get_result();
    return $result->fetch_assoc()['foto'] ?? null;
}

// Função para atualizar a imagem do usuário
function updateUserImage($userId, $imagePath, $conn)
{
    $query = $conn->prepare("UPDATE usuarios SET foto = ? WHERE idUsuarios = ?");
    $query->bind_param("si", $imagePath, $userId);
    return $query->execute();
}

// Função para excluir a imagem do usuário
function deleteUserImage($userId, $conn)
{
    $query = $conn->prepare("UPDATE usuarios SET foto = NULL WHERE idUsuarios = ?");
    $query->bind_param("i", $userId);
    return $query->execute();
}

// Processamento do formulário de upload de imagem
$message = ""; // Variável para mensagens de status
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_FILES['profile_image']) && $_FILES['profile_image']['error'] == 0) {
        $targetDir = "uploads/";

        // Cria o diretório 'uploads' caso ele não exista
        if (!is_dir($targetDir)) {
            mkdir($targetDir, 0777, true);
        }

        $fileName = basename($_FILES["profile_image"]["name"]);
        $targetFile = $targetDir . uniqid() . '_' . $fileName;

        // Verifica se o arquivo é uma imagem e se o upload é válido
        $imageFileType = strtolower(pathinfo($targetFile, PATHINFO_EXTENSION));
        $check = getimagesize($_FILES["profile_image"]["tmp_name"]);
        if ($check !== false && in_array($imageFileType, ['jpg', 'jpeg', 'png', 'gif'])) {
            if (move_uploaded_file($_FILES["profile_image"]["tmp_name"], $targetFile)) {
                // Atualiza a imagem do usuário no banco
                if (updateUserImage($_SESSION['id'], $targetFile, $conn)) {
                    $message = "<div class='alert success'>Imagem de perfil atualizada com sucesso!</div>";
                } else {
                    $message = "<div class='alert error'>Erro ao atualizar imagem no banco de dados.</div>";
                }
            } else {
                $message = "<div class='alert error'>Erro ao mover o arquivo para o diretório de destino.</div>";
            }
        } else {
            $message = "<div class='alert error'>Arquivo não é uma imagem válida ou tipo não suportado.</div>";
        }
    } elseif (isset($_POST['delete_image'])) {
        // Exclui a imagem do banco de dados
        if (deleteUserImage($_SESSION['id'], $conn)) {
            $message = "<div class='alert success'>Imagem de perfil excluída com sucesso!</div>";
        } else {
            $message = "<div class='alert error'>Erro ao excluir imagem.</div>";
        }
    }

    // Redireciona após o processamento
    header('Location: home.php?page=perfil');
    exit;
}

// Obtém a imagem atual do usuário
$imagem = getUserImage($_SESSION['id'], $conn);
?>

<!DOCTYPE html>
<html lang="pt-br">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/style.css">
    <title>Perfil do Usuário</title>
    <style>
        body {
            background: linear-gradient(90deg, #004e92, #000428);
            /* Gradiente para a barra */
            box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.3);
            font-family: Arial, sans-serif;
            color: black;
            margin: 0;
            padding: 20px;
        }

        .container {
            background: linear-gradient(90deg, #007bff, #0056b3);
            /* Gradiente azul */
            color: #ffffff;
            /* Texto branco */
            border-radius: 10px;
            /* Bordas arredondadas */
            padding: 20px;
            /* Espaçamento interno */
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
            /* Sombra suave */
        }

        .card {
            background: #103667;
            /* Azul escuro sólido */
            color: #ffffff;
            /* Texto branco */
            border-radius: 10px;
            /* Bordas arredondadas */
            padding: 15px;
            /* Espaçamento interno */
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            /* Sombra suave */
            margin-bottom: 15px;
            /* Espaçamento inferior entre os cards */
        }

        .col-md-12 {
            background-color: #fff;
            padding-top: 15px;
        }

        h1 {
            font-size: 24px;
            margin-bottom: 20px;
        }

        .profile-image-container {
            display: flex;
            gap: 20px;
            align-items: center;
            margin-bottom: 20px;
        }

        .profile-image img,
        #imagePreview {
            width: 200px;
            height: 200px;
            object-fit: cover;
            border-radius: 50%;
            border: 2px solid #007bff;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
        }

        #imagePreview {
            display: none;
        }

        form {
            margin-top: 20px;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        form label {
            margin-bottom: 5px;
            font-weight: bold;
            font-size: 18px;
        }

        .custom-file-upload {
            display: inline-block;
            padding: 10px;
            cursor: pointer;
            background-color: #007bff;
            color: white;
            border-radius: 5px;
            text-align: center;
            font-size: 18px;
        }

        .custom-file-upload input[type="file"] {
            display: none;
        }

        form button {
            padding: 10px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 18px;
        }

        form button:hover {
            background-color: #0056b3;
        }

        .alert {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
        }

        .success {
            background-color: #d4edda;
            color: #155724;
        }

        .error {
            background-color: #f8d7da;
            color: #721c24;
        }
    </style>
    <script>
        function previewImage(input) {
            const file = input.files[0];
            const confirmButton = document.getElementById('confirmButton'); // Referência ao botão

            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    const imgPreview = document.getElementById('imagePreview');
                    imgPreview.src = e.target.result;
                    imgPreview.style.display = 'block';

                    // Exibe o botão Confirmar após a pré-visualização
                    confirmButton.style.display = 'inline-block';
                };
                reader.readAsDataURL(file);
            }
        }

        function hideMessage() {
            const alert = document.querySelector('.alert');
            if (alert) {
                setTimeout(() => {
                    alert.style.display = 'none';
                }, 3000);
            }
        }

        window.onload = hideMessage;
    </script>
</head>

<body>

        <?php echo "<h1>Bem-Vindo(a), " . htmlspecialchars($_SESSION['nome']) . "</h1>"; ?>
        <h1>Perfil do Usuário</h1>
        <div class="profile-image-container">
            <div class="profile-image">
                <?php if (!empty($imagem)): ?>
                    <img src="<?php echo htmlspecialchars($imagem); ?>" alt="Foto de Perfil">
                <?php else: ?>
                    <p>Sem foto de perfil</p>
                <?php endif; ?>
            </div>
            <div class="preview-image">
                <img id="imagePreview" alt="Pré-visualização da Foto">
            </div>
        </div>
        <form action="editar_perfil.php" method="get">
            <button type="submit">Alterar Dados Pessoais</button>
        </form>

        <?php if ($message): ?>
            <?php echo $message; ?>
        <?php endif; ?>

        <form action="perfil.php" method="POST" enctype="multipart/form-data">
            <label class="custom-file-upload">
                <input type="file" name="profile_image" id="profile_image" accept="image/*" required onchange="previewImage(this);">
                Escolher Foto
            </label>
            <img id="imagePreview" style="display:none; width: 200px; height: 200px; border-radius: 50%; border: 3px solid #007bff;">

            <!-- Botão Confirmar inicialmente oculto -->
            <button type="submit" id="confirmButton" style="display: none;">Confirmar</button>
        </form>

        <form action="perfil.php" method="POST">
            <input type="hidden" name="delete_image" value="1">
            <button type="submit">Excluir Foto de Perfil</button>
        </form>

</body>

</html>