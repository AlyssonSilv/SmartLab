<?php
// Inicia a sessão para acessar as variáveis de sessão
session_start();

// Inclui o arquivo de conexão com o banco de dados
require 'conexao.php';

// Verifica se o botão de reserva foi acionado
if (isset($_POST['reserva'])) {
	// Obtém o ID do usuário a partir da sessão
	$idUsuario = $_SESSION['id'];

	// Obtém os dados enviados pelo formulário
	$fk_id_ambiente = $_POST['idAmbiente'];
	$fk_id_horario = $_POST['idHorario'];
	$fk_id_perifericos = $_POST['idPerifericos'];

	// Atualiza a quantidade de periféricos no banco de dados, reduzindo em 1
	$sql_reduzir_quantidade = "UPDATE perifericos SET Quantidade = Quantidade - 1 WHERE idPerifericos = ?";
	$stmt_reduzir_quantidade = $conn->prepare($sql_reduzir_quantidade);
	$stmt_reduzir_quantidade->bind_param("i", $fk_id_perifericos);
	$stmt_reduzir_quantidade->execute();

	// Insere os dados da nova reserva na tabela 'reserva'
	$sql = "INSERT INTO reserva
                    (fk_id_usuario, fk_id_horario, fk_id_perifericos, ambientes_idAmbiente) 
                VALUES ('{$idUsuario}', '{$fk_id_horario}', '{$fk_id_perifericos}', '{$fk_id_ambiente}')";

	$res = $conn->query($sql);

	// Verifica se a reserva foi feita com sucesso
	if ($res == true) {
		// Redireciona para a página inicial ou de confirmação
		header('Location: home.php?page=?');
	} else {
		// Exibe um alerta e redireciona para a página de finalização em caso de erro
		print "<script>alert('Não foi possível concluir a reserva');</script>";
		print "<script>location.href='?page=finalizar';</script>";
	}
	exit; // Encerra o script após a execução
}

// Verifica se o botão de exclusão da reserva foi acionado
if (isset($_POST['delete_reserva'])) {
	// Obtém os dados da reserva a ser excluída
	$idReserva = $_POST['delete_reserva'];
	$fk_id_horario = $_POST['fk_id_horario'];
	$ambientes_idAmbiente = $_POST['ambientes_idAmbiente'];
	$fk_id_perifericos = $_POST['fk_id_perifericos']; // Identifica o periférico associado à reserva

	// Chama a procedure no banco de dados que faz a exclusão e a atualização necessárias
	$sql = "CALL DeleteReserva(?, ?, ?, ?)";
	$stmt = $conn->prepare($sql);
	$stmt->bind_param("iiii", $idReserva, $fk_id_horario, $ambientes_idAmbiente, $fk_id_perifericos);

	// Executa a stored procedure e verifica o resultado
	if ($stmt->execute()) {
		// Redireciona para a página de listagem de reservas após a exclusão
		header("Location: home.php?page=reserva-listar");
		exit;
	} else {
		// Exibe um alerta e redireciona em caso de erro
		print "<script>alert('Não foi possível excluir a reserva.');</script>";
		print "<script>location.href='home.php?page=reserva';</script>";
	}
}
