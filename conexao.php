<?php

define('HOST', 'localhost');
define('USER', 'root');
define('PASS', 'root');
define('BASE', 'smartlab');

// Criação da conexão
$conn = new mysqli(HOST, USER, PASS, BASE);

// Verifica se houve erro na conexão
if ($conn->connect_error) {
    die("Erro na conexão: " . $conn->connect_error);
}

// TENTANDO CRIAR VIEW

