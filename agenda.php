<?php
require('conexao.php');
?>

<div class="card-header">
    <h4>
        Agenda
    </h4>
    <table class="table table-striped">
        <thead>
            <tr>
                <th>Local</th>
                <th>Periférico</th>
                <th>Horário</th>
                <th>Status</th>
                <th></th>
            </tr>
        </thead>
        <?php

        $idUsuario = $_SESSION['id'];

        // Chamando a função ListarReservasAtivas do banco de dados
        $sql = "SELECT ListarReservasAtivas(?) AS reservas_ativas";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("i", $idUsuario);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $reservasAtivasJson = $row['reservas_ativas'];

            if ($reservasAtivasJson) {
                $reservasAtivas = json_decode($reservasAtivasJson, true); // Decodifica o JSON para um array
                
                if (!empty($reservasAtivas)) {
                    foreach ($reservasAtivas as $reserva) {
                        // Subqueries para buscar informações relacionadas
                        $sqlAmbiente = "SELECT nomeAmbiente FROM ambientes WHERE idAmbiente = ?";
                        $stmtAmbiente = $conn->prepare($sqlAmbiente);
                        $stmtAmbiente->bind_param("i", $reserva['ambientes_idAmbiente']);
                        $stmtAmbiente->execute();
                        $ambiente = $stmtAmbiente->get_result()->fetch_assoc()['nomeAmbiente'];

                        $sqlPeriferico = "SELECT Tipo FROM perifericos WHERE idPerifericos = ?";
                        $stmtPeriferico = $conn->prepare($sqlPeriferico);
                        $stmtPeriferico->bind_param("i", $reserva['fk_id_perifericos']);
                        $stmtPeriferico->execute();
                        $periferico = $stmtPeriferico->get_result()->fetch_assoc()['Tipo'];

                        $sqlHorario = "SELECT Horario FROM horarios WHERE idHorarios = ?";
                        $stmtHorario = $conn->prepare($sqlHorario);
                        $stmtHorario->bind_param("i", $reserva['fk_id_horario']);
                        $stmtHorario->execute();
                        $horario = $stmtHorario->get_result()->fetch_assoc()['Horario'];

                        ?>
                        <tr>
                            <td><?= htmlspecialchars($ambiente) ?></td>
                            <td><?= htmlspecialchars($periferico) ?></td>
                            <td><?= htmlspecialchars($horario) ?></td>
                            <td><?= htmlspecialchars($reserva['status'] == 1 ? 'Ativa' : 'Inativa') ?></td>
                            <td>
                                <form action="acoes.php" method="POST" class="d-inline">
                                    <input type="hidden" name="fk_id_horario" value="<?= htmlspecialchars($reserva['fk_id_horario']) ?>">
                                    <input type="hidden" name="ambientes_idAmbiente" value="<?= htmlspecialchars($reserva['ambientes_idAmbiente']) ?>">
                                    <input type="hidden" name="fk_id_perifericos" value="<?= htmlspecialchars($reserva['fk_id_perifericos']) ?>"> <!-- Adicionando o ID do periférico -->
                                    <button onclick="return confirm('Deseja excluir reserva?')" 
                                        type="submit" name="delete_reserva" 
                                        value="<?= htmlspecialchars($reserva['idReserva']) ?>" class="btn btn-danger btn-sm">
                                        <span class="bi-trash3-fill"></span>&nbsp;Excluir
                                    </button>
                                </form>
                            </td>
                        </tr>
                        <?php
                    }
                } else {
                    echo "<tr><td colspan='5'>Nenhuma reserva ativa encontrada.</td></tr>";
                }
            } else {
                echo "<tr><td colspan='5'>Nenhuma reserva ativa.</td></tr>";
            }
        } else {
            echo "<tr><td colspan='5'>Erro ao buscar reservas.</td></tr>";
        }
        ?>
    </table>
</div>
