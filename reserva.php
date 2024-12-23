<?php
require_once 'conexao.php';

if (!isset($_SESSION['id'])) {
    echo "<p style='color:red;'>Erro: Usuário não autenticado. Por favor, faça login.</p>";
    exit;
}

// Buscar dados de horários, ambientes e periféricos
$horarios = [];
$ambientes = [];
$perifericos = [];

$queryHorarios = "SELECT * FROM horarios";
$queryAmbientes = "SELECT * FROM ambientes";
$queryPerifericos = "SELECT * FROM perifericos";

$horariosResult = $conn->query($queryHorarios);
$ambientesResult = $conn->query($queryAmbientes);
$perifericosResult = $conn->query($queryPerifericos);

// Armazenar os resultados em arrays
while ($row = $horariosResult->fetch_assoc()) {
    $horarios[] = $row;
}
while ($row = $ambientesResult->fetch_assoc()) {
    $ambientes[] = $row;
}
while ($row = $perifericosResult->fetch_assoc()) {
    $perifericos[] = $row;
}

// Processar a reserva
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['confirmar_reserva'])) {
    $idHorario = $_POST['horario'];
    $idAmbiente = $_POST['ambiente'];
    $idPeriferico = $_POST['periferico'];
    $quantidadePeriferico = $_POST['quantidade_periferico'];

    // Verificar a disponibilidade
    $stmt = $conn->prepare("SELECT disponivel FROM horarios WHERE idHorarios = ? AND disponivel = 1");
    $stmt->bind_param("i", $idHorario);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows === 0) {
        echo "<p style='color:red;'>Horário não disponível.</p>";
        exit;
    }

    $stmt = $conn->prepare("SELECT disponibilidade FROM ambientes WHERE idAmbiente = ? AND disponibilidade = 1");
    $stmt->bind_param("i", $idAmbiente);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows === 0) {
        echo "<p style='color:red;'>Ambiente não disponível.</p>";
        exit;
    }

    $stmt = $conn->prepare("SELECT Quantidade FROM perifericos WHERE idPerifericos = ? AND Quantidade >= ?");
    $stmt->bind_param("ii", $idPeriferico, $quantidadePeriferico);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows === 0) {
        echo "<p style='color:red;'>Quantidade de periféricos não disponível.</p>";
        exit;
    }

    // Inserir a reserva na tabela
    $queryReserva = "INSERT INTO reserva (fk_id_usuario, fk_id_horario, fk_id_perifericos, dataHora, ambientes_idAmbiente) 
                     VALUES (?, ?, ?, NOW(), ?)";
    $stmt = $conn->prepare($queryReserva);
    $stmt->bind_param("iiii", $_SESSION['id'], $idHorario, $idPeriferico, $idAmbiente);
    if ($stmt->execute()) {

        // Atualizar os periféricos após a reserva
        $conn->query("UPDATE perifericos SET Quantidade = Quantidade - $quantidadePeriferico WHERE idPerifericos = $idPeriferico");

        echo "<p style='color:green;'>Reserva realizada com sucesso!</p>";
    } else {
        echo "<p style='color:red;'>Erro ao realizar a reserva. Tente novamente.</p>";
    }
}
?>

<!DOCTYPE html>
<html lang="pt-br">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Realizar Reserva</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="home.css" rel="stylesheet">
</head>

<body>
        <h1>Realizar Reserva</h1>

        <form method="POST">
            <!-- Horário -->
            <div class="form-group">
                <label for="horario">Escolha o Horário:</label>
                <select name="horario" id="horario" class="form-control" required>
                    <option value="">Selecione um horário</option>
                    <?php foreach ($horarios as $horario): ?>
                        <option value="<?php echo $horario['idHorarios']; ?>">
                            <?php echo $horario['Turno'] . " - " . $horario['Horario']; ?>
                        </option>
                    <?php endforeach; ?>
                </select>
                <div id="status_horario" class="status-disponibilidade"></div>
            </div>

            <!-- Ambiente -->
            <div class="form-group">
                <label for="ambiente">Escolha o Ambiente:</label>
                <select name="ambiente" id="ambiente" class="form-control" required>
                    <option value="">Selecione um ambiente</option>
                    <?php foreach ($ambientes as $ambiente): ?>
                        <option value="<?php echo $ambiente['idAmbiente']; ?>">
                            <?php echo $ambiente['nomeAmbiente'] . " - " . $ambiente['tipoAmbiente']; ?>
                        </option>
                    <?php endforeach; ?>
                </select>
                <div id="status_ambiente" class="status-disponibilidade"></div>
            </div>

            <!-- Periférico e Quantidade -->
            <div class="form-group">
                <label for="periferico">Escolha o Periférico:</label>
                <select name="periferico" id="periferico" class="form-control" required>
                    <option value="">Selecione um periférico</option>
                    <?php foreach ($perifericos as $periferico): ?>
                        <option value="<?php echo $periferico['idPerifericos']; ?>" data-quantidade="<?php echo $periferico['Quantidade']; ?>">
                            <?php echo $periferico['Tipo']; ?>
                        </option>
                    <?php endforeach; ?>
                </select>
                <small class="quantidade-disponivel">Quantidade disponível: <span id="quantidade_disponivel"></span></small>
                <div id="status_periferico" class="status-disponibilidade"></div>
            </div>

            <!-- Quantidade de Periférico -->
            <div class="form-group" id="quantidade-container" style="display: none;">
                <label for="quantidade_periferico">Quantidade de Periféricos:</label>
                <input type="number" name="quantidade_periferico" id="quantidade_periferico" class="form-control" min="1" value="1" required>
            </div>

            <button type="submit" name="confirmar_reserva" id="confirmar_reserva" disabled>Confirmar Reserva</button>
        </form>

    <script>
        // Mesma funcionalidade JavaScript, como no seu exemplo, para manipular as verificações de disponibilidade e formulários
        const confirmarButton = document.getElementById('confirmar_reserva');
        const perifericoSelect = document.getElementById('periferico');
        const quantidadeContainer = document.getElementById('quantidade-container');
        const quantidadeInput = document.getElementById('quantidade_periferico');
        const quantidadeDisponivel = document.getElementById('quantidade_disponivel');
        const statusHorario = document.getElementById('status_horario');
        const statusAmbiente = document.getElementById('status_ambiente');

        function verificarDisponibilidade() {
            confirmarButton.disabled = !(statusHorario.classList.contains('disponivel') && statusAmbiente.classList.contains('disponivel') && parseInt(quantidadeDisponivel.textContent) >= parseInt(quantidadeInput.value));
        }

        document.getElementById('horario').addEventListener('change', function () {
            const idHorario = this.value;
            if (idHorario) {
                fetch('verificar_disponibilidade.php', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ idHorario: idHorario }),
                })
                .then(response => response.json())
                .then(data => {
                    if (data.disponivel) {
                        statusHorario.textContent = "Horário disponível";
                        statusHorario.className = 'status-disponibilidade disponivel';
                    } else {
                        statusHorario.textContent = "Horário indisponível";
                        statusHorario.className = 'status-disponibilidade indisponivel';
                    }
                    verificarDisponibilidade();
                });
            }
        });

        document.getElementById('ambiente').addEventListener('change', function () {
            const idAmbiente = this.value;
            if (idAmbiente) {
                fetch('verificar_disponibilidade.php', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ idAmbiente: idAmbiente }),
                })
                .then(response => response.json())
                .then(data => {
                    if (data.disponivel) {
                        statusAmbiente.textContent = "Ambiente disponível";
                        statusAmbiente.className = 'status-disponibilidade disponivel';
                    } else {
                        statusAmbiente.textContent = "Ambiente indisponível";
                        statusAmbiente.className = 'status-disponibilidade indisponivel';
                    }
                    verificarDisponibilidade();
                });
            }
        });

        perifericoSelect.addEventListener('change', function () {
            const quantidade = parseInt(this.options[this.selectedIndex].dataset.quantidade);
            quantidadeDisponivel.textContent = quantidade || 0;
            quantidadeContainer.style.display = quantidade > 0 ? 'block' : 'none';
            quantidadeInput.value = 1;
            verificarDisponibilidade();
        });

        quantidadeInput.addEventListener('input', function () {
            let quantidade = parseInt(this.value);
            const quantidadeMax = parseInt(quantidadeDisponivel.textContent);
            if (quantidade <= 0) quantidade = 1;
            if (quantidade > quantidadeMax) quantidade = quantidadeMax;
            this.value = quantidade;
            verificarDisponibilidade();
        });
    </script>
</body>

</html>