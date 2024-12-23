<?php
require_once 'conexao.php';

$data = json_decode(file_get_contents("php://input"), true);

if (isset($data['idHorario'])) {
    $idHorario = $data['idHorario'];
    $query = "SELECT disponivel FROM horarios WHERE idHorarios = ?";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("i", $idHorario);
    $stmt->execute();
    $stmt->bind_result($disponivel);
    $stmt->fetch();
    echo json_encode(['disponivel' => (bool)$disponivel]);
    exit;
}

if (isset($data['idAmbiente'])) {
    $idAmbiente = $data['idAmbiente'];
    $query = "SELECT disponibilidade FROM ambientes WHERE idAmbiente = ?";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("i", $idAmbiente);
    $stmt->execute();
    $stmt->bind_result($disponibilidade);
    $stmt->fetch();
    echo json_encode(['disponivel' => (bool)$disponibilidade]);
    exit;
}
