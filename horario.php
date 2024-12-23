<?php
include('conexao.php');

// Verifica se o usuário está logado
if (!isset($_SESSION['id'])) {
    echo "<p style='color:red;'>Erro: Usuário não autenticado. Por favor, faça login.</p>";
    exit();
}

// Mensagem de feedback
if (isset($_SESSION['msg'])) {
    echo $_SESSION['msg'];
    unset($_SESSION['msg']);
}

// Verifica se foi solicitado reservar um horário
if (isset($_POST['reservar'])) {
    $horario_id = $_POST['horario_id'];
    $usuario_id = $_SESSION['id']; // ID do usuário logado
    $ambiente_id = $_POST['ambiente_id']; // ID do ambiente selecionado
    $periferico_id = $_POST['periferico_id']; // ID do periférico selecionado

    // Primeiro, vamos verificar se o horário está disponível
    $check_sql = "SELECT disponivel FROM horarios WHERE idHorarios = ?";
    $stmt_check = $conn->prepare($check_sql);
    $stmt_check->bind_param("i", $horario_id);
    $stmt_check->execute();
    $result_check = $stmt_check->get_result();

    if ($result_check->num_rows > 0) {
        $row_check = $result_check->fetch_assoc();
        if ($row_check['disponivel'] == 1) {
            // Insere a reserva
            $insert_sql = "INSERT INTO reserva (fk_id_usuario, fk_id_horario, fk_id_perifericos, ambientes_idAmbiente) 
                           VALUES (?, ?, ?, ?)";
            $stmt = $conn->prepare($insert_sql);
            $stmt->bind_param("iiii", $usuario_id, $horario_id, $periferico_id, $ambiente_id);

            if ($stmt->execute()) {
                // Atualiza o horário para não disponível
                $update_sql = "UPDATE horarios SET disponivel = 0 WHERE idHorarios = ?";
                $stmt_update = $conn->prepare($update_sql);
                $stmt_update->bind_param("i", $horario_id);
                $stmt_update->execute();
                $stmt_update->close();

                $_SESSION['msg'] = "<p style='color:green;'>Reserva criada com sucesso!</p>";
            } else {
                $_SESSION['msg'] = "<p style='color:red;'>Erro ao criar reserva: " . $stmt->error . "</p>";
            }
            $stmt->close();
        } else {
            $_SESSION['msg'] = "<p style='color:red;'>Este horário já está reservado.</p>";
        }
    } else {
        $_SESSION['msg'] = "<p style='color:red;'>Erro: Horário não encontrado.</p>";
    }
    $stmt_check->close();
    header("Location: horarios.php");
    exit();
}
?>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Horários</title>
    <link rel="stylesheet" href="style.css"> <!-- Supondo que você tenha um arquivo CSS -->
    <script>
        function confirmarReserva(button) {
            const row = button.closest('tr');
            const horarioId = row.querySelector('input[name="horario_id"]').value;
            const ambienteId = row.querySelector('input[name="ambiente_id"]').value; // Obtém o ID do ambiente
            const perifericoId = row.querySelector('input[name="periferico_id"]').value; // Obtém o ID do periférico

            // Pergunta ao usuário se ele realmente deseja reservar
            const confirmar = confirm("Você tem certeza que deseja reservar este horário?");
            if (confirmar) {
                // Preenche os campos ocultos do formulário antes de submeter
                const form = row.querySelector('form');
                form.querySelector('input[name="horario_id"]').value = horarioId;
                form.querySelector('input[name="ambiente_id"]').value = ambienteId;
                form.querySelector('input[name="periferico_id"]').value = perifericoId;
                form.submit(); // Envia o formulário
            }
        }
    </script>
</head>
<body>
    <div class="card-header">
        <h4>Horários</h4>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Turno</th>
                    <th>Horário</th>
                    <th>Status</th>
                    <th>Ação</th>
                </tr>
            </thead>
            <tbody>
                <?php
                // Consulta para trazer apenas os horários e status
                $sql = "SELECT idHorarios, Turno, Horario, disponivel FROM horarios ORDER BY Horario ASC";
                if (!$res = $conn->query($sql)) {
                    die("Erro na consulta: " . $conn->error);
                }

                $qtd = $res->num_rows;

                if ($qtd > 0) {
                    while ($row = $res->fetch_object()) {
                        $status = $row->disponivel ? 'Livre' : 'Ocupado'; // Alterado para Livre/Ocupado
                ?>
                        <tr>
                            <td><?= htmlspecialchars($row->Turno) ?></td>
                            <td><?= htmlspecialchars($row->Horario) ?></td>
                            <td><?= htmlspecialchars($status) ?></td>
                            <td>
                                <?php if ($row->disponivel == 1) { ?>
                                    <form method="POST" action="">
                                        <input type="hidden" name="horario_id" value="<?= htmlspecialchars($row->idHorarios) ?>">
                                        <input type="hidden" name="ambiente_id" value="1"> <!-- Exemplo de ID do ambiente, ajuste conforme necessário -->
                                        <input type="hidden" name="periferico_id" value="1"> <!-- Exemplo de ID do periférico, ajuste conforme necessário -->
                                        <button type="button" class="btn btn-success" onclick="confirmarReserva(this)">Reservar</button>
                                    </form>
                                <?php } else { ?>
                                    <button class="btn btn-secondary" disabled>Indisponível</button>
                                <?php } ?>
                            </td>
                        </tr>
                <?php
                    }
                } else {
                    echo "<tr><td colspan='4'>Nenhum horário disponível.</td></tr>";
                }
                ?>
            </tbody>
        </table>
    </div>
</body>
</html>
